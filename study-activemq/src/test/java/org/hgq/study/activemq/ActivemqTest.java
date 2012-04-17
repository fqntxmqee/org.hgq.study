
package org.hgq.study.activemq;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hgq.study.activemq.core.QueueMessageProducer;
import org.hgq.study.activemq.core.TopicMessageProducer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.webframe.test.BaseSpringTests;


/**
 * ActivemqTest
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-16 下午4:23:51
 * @version
 */
public class ActivemqTest extends BaseSpringTests {

	@Autowired
	TopicMessageProducer	topicMessageProducer;

	@Autowired
	QueueMessageProducer	queueMessageProducer;

	@Test
	public void testActivemq() {
		Map<String, Object> message = new LinkedHashMap<String, Object>();
		message.put("test", "ActiveMQ");
		queueMessageProducer.send(message);
		topicMessageProducer.send(message);
		System.out.println("完成");
	}
}
