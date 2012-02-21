/*
 * study-perf4j
 * Created on 2012-2-21-下午09:18:11
 */

package org.hgq.study.perf4j;

import org.webframe.support.driver.AbstractModulePluginDriver;
import org.webframe.support.driver.ModulePluginManager;

/**
 * Perf4j 性能分析
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆 </a>
 * @since 2012-2-21 下午09:18:11
 * @version
 */
public class Perf4jModulePluginDriver extends AbstractModulePluginDriver {

	static {
		ModulePluginManager.registerDriver(new Perf4jModulePluginDriver());
	}

	/* (non-Javadoc)
	 * @see org.webframe.support.driver.ModulePluginDriver#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "Perf4jModule";
	}
}
