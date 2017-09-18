/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0 

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.auphi.data.hub.core.idgenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.idgenerator.id.CreateIDException;
import com.auphi.data.hub.core.idgenerator.id.DefaultSequenceGenerator;
import com.auphi.data.hub.core.idgenerator.id.IDGenerator;
import com.auphi.data.hub.core.idgenerator.id.PrefixGenerator;
import com.auphi.data.hub.core.idgenerator.id.SequenceFormater;
import com.auphi.data.hub.core.idgenerator.id.SequenceGenerator;
import com.auphi.data.hub.dao.SystemDao;

/**
 * 默认主键生成器
 * @author zhangjiafeng
 *
 */
public class DefaultIDGenerator implements IDGenerator {

	private PrefixGenerator prefixGenerator;
	private SequenceGenerator sequenceGenerator = new DefaultSequenceGenerator();
	private SequenceFormater sequenceFormater;

	private final Log logger = LogFactory.getLog(DefaultIDGenerator.class);

	public synchronized String create(SystemDao systemDao) throws CreateIDException {
		final String prefix = prefixGenerator == null ? "" : prefixGenerator.create();
		logger.debug("ID前缀是:[" + prefix + "]");
		long sequence = sequenceGenerator.next(systemDao);
		final String strSequence = sequenceFormater == null ? new Long(sequence).toString() : sequenceFormater
				.format(sequence);
		return prefix + strSequence;
	}

	public void setPrefixGenerator(PrefixGenerator prefixGenerator) {
		this.prefixGenerator = prefixGenerator;
	}

	public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
		this.sequenceGenerator = sequenceGenerator;
	}

	public void setSequenceFormater(SequenceFormater sequenceFormater) {
		this.sequenceFormater = sequenceFormater;
	}

}
