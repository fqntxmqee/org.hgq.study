
package org.hgq.study.activemq;

import org.webframe.support.driver.AbstractModulePluginDriver;
import org.webframe.support.driver.ModulePluginManager;


/**
 * AcitveMQModule
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-16 下午3:44:50
 * @version
 */
public class AcitveMQModulePluginDriver extends AbstractModulePluginDriver {

	static {
		ModulePluginManager.registerDriver(new AcitveMQModulePluginDriver());
	}

	/* (non-Javadoc)
	 * @see org.webframe.support.driver.ModulePluginDriver#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "AcitveMQModule";
	}
}
