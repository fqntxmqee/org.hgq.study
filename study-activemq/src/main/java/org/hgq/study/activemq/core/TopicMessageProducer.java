/**
 * 
 */
package org.hgq.study.activemq.core;

import java.util.Map;

import javax.jms.Topic;

import org.springframework.jms.core.JmsTemplate;

/**
 * 生产Topic方式的消息
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-16 下午4:21:40
 * @version
 */
public class TopicMessageProducer {

	private JmsTemplate	template;

	private Topic			destination;

	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public void setDestination(Topic destination) {
		this.destination = destination;
	}

	public void send(Map<String, Object> message) {
		template.convertAndSend(this.destination, message);
	}
}
