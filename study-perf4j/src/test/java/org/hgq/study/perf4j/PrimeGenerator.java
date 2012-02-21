/*
 * study-perf4j
 * Created on 2012-2-21-下午09:24:26
 */

package org.hgq.study.perf4j;

import java.math.BigInteger;

import org.perf4j.aop.Profiled;

/**
 * PrimeGenerator
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆 </a>
 * @since 2012-2-21 下午09:24:26
 * @version
 */
public class PrimeGenerator {

	// keeps track of the prime we're going to return
	private BigInteger	currentPrime	= new BigInteger("0");

	public void setCurrentPrime(BigInteger currentPrime) {
		this.currentPrime = currentPrime;
	}

	@Profiled(tag = "nextPrime")
	public BigInteger nextPrime() {
		currentPrime = currentPrime.nextProbablePrime();
		return currentPrime;
	}
}
