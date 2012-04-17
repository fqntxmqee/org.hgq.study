/**
 * 
 */
package org.hgq.study.activemq.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建10个线程池 1.使用线程异步消息处理 2.不使用线程，那么消息等待上一个消息处理完成后才继续 注:如果担心据同步问题，那么使用第2种方法
 */
public class TopicConsumer {

	protected static ExecutorService	exec	= Executors.newFixedThreadPool(10);

	public void receive(Map<String, Object> message) {
		Set<String> set = message.keySet();
		String str = "";
		for (String key : set) {
			str += key + "_" + message.get(key) + ":Topic";
		}
		System.out.println(str);
	}
}