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


import com.alibaba.druid.pool.DruidDataSource;
import com.aofei.base.common.Const;
import com.aofei.kettle.App;
import com.aofei.kettle.core.PropsUI;
import com.aofei.sys.utils.RepositoryCodec;
import org.joda.time.DateTime;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.i18n.LanguageChoice;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @auther Tony
 * @create 2018-09-21 18:13
 */
@Component
public class SystemInitializingBean implements InitializingBean, DisposableBean {


    @Autowired
    private DruidDataSource dataSource;

    private static Logger logger = LoggerFactory.getLogger(SystemInitializingBean.class);

    private final Timer repositoryTimer = new Timer();
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
        LanguageChoice.getInstance().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        KettleLogStore.init( 5000, 720 );
        KettleEnvironment.init();
        PropsUI.init( "KettleWebConsole", Props.TYPE_PROPERTIES_KITCHEN );
        /*//加载
        List<RepositoryResponse> repositorys = repositoryService.getRepositorys(new RepositoryRequest());
        Map<String, Repository> repositoryMap = new HashMap<>();
        for(RepositoryResponse repository : repositorys){
            RepositoryDatabaseResponse repositoryDatabase =  repositoryDatabaseService.get(repository.getRepositoryConnectionId());
            Repository databaseRepository = RepositoryCodec.decode(repository,repositoryDatabase);
            repositoryMap.put(repository.getRepositoryName(),databaseRepository);
            if(repository.getIsDefault()== Const.YES){
                App.getInstance().setRepository(databaseRepository);
            }
        }*/
        // App.getInstance().setRepositories(repositoryMap);

        //默认当前系统dataSource为默认资源库
        /*if(App.getInstance().getRepository()==null){
            App.getInstance().setRepository(RepositoryCodec.decodeDefault(dataSource));
        }*/
        KettleDatabaseRepository repository =  RepositoryCodec.decodeDefault(dataSource);
        repository.getDatabase().getDatabaseMeta().setSupportsBooleanDataType(true);
        repository.connect(Const.REPOSITORY_USERNAME,Const.REPOSITORY_PASSWORD);
        App.getInstance().setRepository(repository);
        CheckRepositoryTimerTask checkRepositoryTimerTask = new CheckRepositoryTimerTask();
        repositoryTimer.schedule(checkRepositoryTimerTask,0,1000*60*60);
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
        Map<String, Repository> repositorys = App.getInstance().getRepositories();
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

    /**
     * 检查资源库的连接;连接断开要重新连接的
     */
    class CheckRepositoryTimerTask extends TimerTask {
        private  Logger logger = LoggerFactory.getLogger(CheckRepositoryTimerTask.class);


        @Override
        public void run() {
            KettleDatabaseRepository repository = (KettleDatabaseRepository) App.getInstance().getRepository();
            Database database = repository.getDatabase();
            try {
                logger.info("==============check repository connect=================");
                database.openQuery("select 1");
            } catch (KettleDatabaseException e) {
                try {
                    repository.disconnect();
                    repository.setConnected(false);
                    repository.connect(Const.REPOSITORY_USERNAME,Const.REPOSITORY_PASSWORD);

                } catch (KettleException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
}
