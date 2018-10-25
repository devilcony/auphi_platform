/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2018 by Auphi BI : http://www.doetl.com

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
package com.aofei.admin.init;


import com.aofei.kettle.App;
import com.aofei.kettle.core.PropsUI;
import com.aofei.sys.model.request.RepositoryRequest;
import com.aofei.sys.model.response.RepositoryDatabaseResponse;
import com.aofei.sys.model.response.RepositoryResponse;
import com.aofei.sys.service.IRepositoryDatabaseService;
import com.aofei.sys.service.IRepositoryService;
import com.aofei.sys.utils.RepositoryCodec;
import org.joda.time.DateTime;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @auther Tony
 * @create 2018-09-21 18:13
 */
@Component
public class SystemInitializingBean implements InitializingBean, DisposableBean {

    @Autowired
    private IRepositoryService repositoryService;

    @Autowired
    private IRepositoryDatabaseService repositoryDatabaseService;

    private static Logger logger = LoggerFactory.getLogger(SystemInitializingBean.class);
    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        long start = System.currentTimeMillis();
        logger.info("********************************************");
        logger.info("********北京傲飞商智软件有限公司****************");
        logger.info("********傲飞数据整合平台***********************");
        logger.info("********系统开始启动字典装载程序****************");
        logger.info("********开始加载资源库*************************");
        logger.info("********************************************");
        KettleLogStore.init( 5000, 720 );
        KettleEnvironment.init();
        PropsUI.init( "KettleWebConsole", Props.TYPE_PROPERTIES_KITCHEN );
        //加载
        List<RepositoryResponse> repositorys = repositoryService.getRepositorys(new RepositoryRequest());
        Map<String, Repository> repositoryMap = new HashMap<>();
        for(RepositoryResponse repository : repositorys){
            RepositoryDatabaseResponse repositoryDatabase =  repositoryDatabaseService.get(repository.getRepositoryConnectionId());
            Repository databaseRepository = RepositoryCodec.decode(repository,repositoryDatabase);
            repositoryMap.put(repository.getRepositoryName(),databaseRepository);
        }
        App.getInstance().setRepositorys(repositoryMap);


        long timeSec = (System.currentTimeMillis() - start) / 1000;
        logger.info("********************************************");
        logger.info("平台启动成功[" + DateTime.now().toString() + "]");
        logger.info("启动总耗时: " + timeSec / 60 + "分 " + timeSec % 60 + "秒 ");
        logger.info("********************************************");
    }

    @Override
    public void destroy() throws Exception {
        logger.info("********************************************");
        logger.info("******************正在停止系统****************");
        Map<String, Repository> repositorys = App.getInstance().getRepositorys();
        for(String key : repositorys.keySet()){

            KettleDatabaseRepository repository = (KettleDatabaseRepository) repositorys.get(key);
            if(repository.isConnected()){
                repository.disconnect();
                if(!repository.getDatabase().getConnection().isClosed()){
                    repository.getDatabase().getConnection().close();
                }
                logger.info("disconnect=>"+key);
            }
        }
    }
}
