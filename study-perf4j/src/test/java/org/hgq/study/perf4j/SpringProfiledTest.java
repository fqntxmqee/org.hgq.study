/*
 * study-perf4j
 * Created on 2012-2-21-下午09:25:21
 */

package org.hgq.study.perf4j;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.webframe.test.BaseSpringTests;

/**
 * SpringProfiledTest
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆 </a>
 * @since 2012-2-21 下午09:25:21
 * @version
 */
public class SpringProfiledTest extends BaseSpringTests {

	@Autowired
	private PrimeGenerator	primeGenerator;

	@Test
	public void testPrimeGenerator() {
		for (int i = 0; i < 500; i++) {
			System.out.println("nextPrime() returned " + primeGenerator.nextPrime());
		}
	}
}
