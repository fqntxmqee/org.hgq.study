/**
 * 
 */
package org.hgq.study.activemq.core;

import java.util.Map;

import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;

/**
 * 生产Queue方式的消息
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-16 下午4:20:58
 * @version
 */
public class QueueMessageProducer {

	private JmsTemplate	template;

	private Queue			destination;

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public void setDestination(Queue destination) {
		this.destination = destination;
	}

	public void send(Map<String, Object> message) {
		template.convertAndSend(this.destination, message);
	}
}
