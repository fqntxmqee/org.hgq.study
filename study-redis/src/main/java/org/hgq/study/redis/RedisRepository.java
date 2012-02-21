
package org.hgq.study.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hgq.study.redis.model.Post;
import org.hgq.study.redis.model.Range;
import org.hgq.study.redis.util.KeyUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.JacksonHashMapper;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.DefaultRedisSet;
import org.springframework.data.redis.support.collections.RedisList;
import org.springframework.data.redis.support.collections.RedisSet;
import org.springframework.util.StringUtils;

public class RedisRepository {

	private static final Pattern							MENTION_REGEX	= Pattern.compile("@[\\w]+");

	private final StringRedisTemplate					template;

	private final ValueOperations<String, String>	valueOps;

	private final RedisAtomicLong							userIdCounter;

	// global users
	private RedisList<String>								users;

	// global timeline
	private final RedisList<String>						timeline;

	private final HashMapper<Post, String, String>	postMapper		= new DecoratingStringHashMapper<Post>(new JacksonHashMapper<Post>(Post.class));

	public RedisRepository(StringRedisTemplate template) {
		this.template = template;
		valueOps = template.opsForValue();
		users = new DefaultRedisList<String>(KeyUtils.users(), template);
		timeline = new DefaultRedisList<String>(KeyUtils.timeline(), template);
		userIdCounter = new RedisAtomicLong(KeyUtils.globalUid(), template.getConnectionFactory());
	}

	public String addUser(String name, String password) {
		String uid = String.valueOf(userIdCounter.incrementAndGet());
		// save user as hash
		// uid -> user
		BoundHashOperations<String, String, String> userOps = template.boundHashOps(KeyUtils.uid(uid));
		userOps.put("name", name);
		userOps.put("pass", password);
		valueOps.set(KeyUtils.user(name), uid);
		users.addFirst(name);
		return addAuth(name);
	}

	public Collection<String> getFollowers(String uid) {
		return covertUidsToNames(KeyUtils.followers(uid));
	}

	public Collection<String> getFollowing(String uid) {
		return covertUidsToNames(KeyUtils.following(uid));
	}

	private void handleMentions(Post post, String pid, String name) {
		// find mentions
		Collection<String> mentions = findMentions(post.getContent());
		for (String mention : mentions) {
			String uid = findUid(mention);
			if (uid != null) {
				mentions(uid).addFirst(pid);
			}
		}
	}

	public String findUid(String name) {
		return valueOps.get(KeyUtils.user(name));
	}

	public boolean isUserValid(String name) {
		return template.hasKey(KeyUtils.user(name));
	}

	public boolean isPostValid(String pid) {
		return template.hasKey(KeyUtils.post(pid));
	}

	private String findName(String uid) {
		if (!StringUtils.hasText(uid)) {
			return "";
		}
		BoundHashOperations<String, String, String> userOps = template.boundHashOps(KeyUtils.uid(uid));
		return userOps.get("name");
	}

	public boolean auth(String user, String pass) {
		// find uid
		String uid = findUid(user);
		if (StringUtils.hasText(uid)) {
			BoundHashOperations<String, String, String> userOps = template.boundHashOps(KeyUtils.uid(uid));
			return userOps.get("pass").equals(pass);
		}
		return false;
	}

	public String findNameForAuth(String value) {
		String uid = valueOps.get(KeyUtils.authKey(value));
		return findName(uid);
	}

	public String addAuth(String name) {
		String uid = findUid(name);
		// add random auth key relation
		String auth = UUID.randomUUID().toString();
		valueOps.set(KeyUtils.auth(uid), auth);
		valueOps.set(KeyUtils.authKey(auth), uid);
		return auth;
	}

	public void deleteAuth(String user) {
		String uid = findUid(user);
		String authKey = KeyUtils.auth(uid);
		String auth = valueOps.get(authKey);
		template.delete(Arrays.asList(authKey, KeyUtils.authKey(auth)));
	}

	public boolean hasMorePosts(String targetUid, Range range) {
		return posts(targetUid).size() > range.end + 1;
	}

	public boolean hasMoreTimeline(String targetUid, Range range) {
		return timeline(targetUid).size() > range.end + 1;
	}

	public boolean hasMoreTimeline(Range range) {
		return timeline.size() > range.end + 1;
	}

	public boolean isFollowing(String uid, String targetUid) {
		return following(uid).contains(targetUid);
	}

	public List<String> alsoFollowed(String uid, String targetUid) {
		RedisSet<String> tempSet = following(uid).intersectAndStore(followers(targetUid),
			KeyUtils.alsoFollowed(uid, targetUid));
		String key = tempSet.getKey();
		template.expire(key, 5, TimeUnit.SECONDS);
		return covertUidsToNames(key);
	}

	public List<String> commonFollowers(String uid, String targetUid) {
		RedisSet<String> tempSet = following(uid).intersectAndStore(following(targetUid),
			KeyUtils.commonFollowers(uid, targetUid));
		tempSet.expire(5, TimeUnit.SECONDS);
		return covertUidsToNames(tempSet.getKey());
	}

	// collections mapping the core data structures
	private RedisList<String> timeline(String uid) {
		return new DefaultRedisList<String>(KeyUtils.timeline(uid), template);
	}

	private RedisSet<String> following(String uid) {
		return new DefaultRedisSet<String>(KeyUtils.following(uid), template);
	}

	private RedisSet<String> followers(String uid) {
		return new DefaultRedisSet<String>(KeyUtils.followers(uid), template);
	}

	private RedisList<String> mentions(String uid) {
		return new DefaultRedisList<String>(KeyUtils.mentions(uid), template);
	}

	private RedisList<String> posts(String uid) {
		return new DefaultRedisList<String>(KeyUtils.posts(uid), template);
	}

	// various util methods
	private String replaceReplies(String content) {
		Matcher regexMatcher = MENTION_REGEX.matcher(content);
		while (regexMatcher.find()) {
			String match = regexMatcher.group();
			int start = regexMatcher.start();
			int stop = regexMatcher.end();
			String uName = match.substring(1);
			if (isUserValid(uName)) {
				content = content.substring(0, start)
							+ "<a href=\"!"
							+ uName
							+ "\">"
							+ match
							+ "</a>"
							+ content.substring(stop);
			}
		}
		return content;
	}

	private List<String> covertUidsToNames(String key) {
		return template.sort(SortQueryBuilder.sort(key).noSort().get("uid:*->name").build());
	}

	public static Collection<String> findMentions(String content) {
		Matcher regexMatcher = MENTION_REGEX.matcher(content);
		List<String> mentions = new ArrayList<String>(4);
		while (regexMatcher.find()) {
			mentions.add(regexMatcher.group().substring(1));
		}
		return mentions;
	}
}