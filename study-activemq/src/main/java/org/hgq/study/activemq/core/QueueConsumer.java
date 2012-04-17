/**
 * 
 */
package org.hgq.study.activemq.core;


import java.util.Map;
import java.util.Set;

/**
 * 接受Queue方式消费
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-16 下午4:18:02
 * @version
 */
public class QueueConsumer {

	public void receive(Map<String, Object> message) {
		Set<String> set = message.keySet();
		String str = "";
		for (String key : set) {
			str += key + "_" + message.get(key) + ":Queue";
		}
		System.out.println(str);
	}
}
