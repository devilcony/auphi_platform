package com.aofei.admin.init;

import com.aofei.kettle.core.PropsUI;
import com.aofei.sys.model.request.RepositoryRequest;
import com.aofei.sys.model.response.RepositoryDatabaseResponse;
import com.aofei.sys.model.response.RepositoryResponse;
import com.aofei.sys.service.IRepositoryDatabaseService;
import com.aofei.sys.service.IRepositoryService;
import com.aofei.sys.utils.DatabaseCodec;
import org.joda.time.DateTime;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.logging.KettleLogStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @auther Tony
 * @create 2018-09-21 18:13
 */
@Component
public class SystemInitBean implements InitializingBean {

    @Autowired
    private IRepositoryService repositoryService;

    @Autowired
    private IRepositoryDatabaseService repositoryDatabaseService;

    private static Logger logger = LoggerFactory.getLogger(SystemInitBean.class);
    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        long start = System.currentTimeMillis();
        StringBuffer startInfo = new StringBuffer();

        startInfo.append(System.getProperty("line.separator"))
                .append("********************************************").append(System.getProperty("line.separator"))
                .append("********北京傲飞商智软件有限公司****************").append(System.getProperty("line.separator"))
                .append("********傲飞数据整合平台***********************").append(System.getProperty("line.separator"))
                .append("********系统开始启动字典装载程序****************").append(System.getProperty("line.separator"))
                .append("********开始加载资源库*************************").append(System.getProperty("line.separator"))
                .append("********************************************").append(System.getProperty("line.separator"));
        logger.info(startInfo.toString());
        KettleLogStore.init( 5000, 720 );
        KettleEnvironment.init();

        List<RepositoryResponse> repositorys = repositoryService.getRepositorys(new RepositoryRequest());

        for(RepositoryResponse repository : repositorys){
            RepositoryDatabaseResponse repositoryDatabase =  repositoryDatabaseService.get(repository.getRepositoryConnectionId());
            DatabaseMeta databaseMeta = DatabaseCodec.decode(repositoryDatabase);

        }




        PropsUI.init( "KettleWebConsole", Props.TYPE_PROPERTIES_KITCHEN );

//        KettleDatabaseRepository repository = new KettleDatabaseRepository();
//        repository.set
//        meta.setBaseDirectory(path.getAbsolutePath());
//        meta.setDescription("default");
//        meta.setName("default");
//        meta.setReadOnly(false);
//        meta.setHidingHiddenFiles(true);


       // KettleFileRepository rep = new KettleFileRepository();
       // rep.init(meta);


        long timeSec = (System.currentTimeMillis() - start) / 1000;
        logger.info("********************************************");
        logger.info("平台启动成功[" + DateTime.now().toString() + "]");
        logger.info("启动总耗时: " + timeSec / 60 + "分 " + timeSec % 60 + "秒 ");
        logger.info("********************************************");
    }
}
