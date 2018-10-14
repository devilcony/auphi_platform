package com.aofei.generator;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * 
 * 自动生成映射工具类
 * 
 * @author hubin
 *
 */
public class AutoGeneratorHelper {

	/**
	 * <p>
	 * 测试 run 执行
	 * </p>
	 * <p>
	 * 更多使用查看 http://mp.baomidou.com
	 * </p>
	 */
	public static void main(String[] args) {
		AutoGenerator mpg = new AutoGenerator();


		mpg.setGlobalConfig(new GlobalConfig()
						.setOutputDir( "/Users/Tony/Workspaces/Test/auphi_platform/")//输出目录
						.setIdType(IdType.ID_WORKER)
						.setFileOverride(true)// 是否覆盖文件
						.setActiveRecord(true)// 开启 activeRecord 模式
						.setEnableCache(true)// XML 二级缓存
						.setBaseResultMap(true)// XML ResultMap
						.setBaseColumnList(true)// XML columList
						.setOpen(true)//生成后打开文件夹

						.setAuthor("Tony")
						// 自定义文件命名，注意 %s 会自动填充表实体属性！
						.setMapperName("%sMapper")
						.setXmlName("%sMapper")
						.setServiceName("I%sService")
						.setServiceImplName("%sService")
						.setControllerName("%sController"));



		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(DbType.MYSQL);
		dsc.setTypeConvert(new MySqlTypeConvert());
		dsc.setDriverName("com.mysql.jdbc.Driver");
		dsc.setUsername("root");
		dsc.setPassword("123456");
		dsc.setUrl("jdbc:mysql://localhost:3306/kettle?characterEncoding=utf8");

		mpg.setDataSource(dsc);
		// 策略配置
		StrategyConfig strategy = new StrategyConfig();

		strategy.setTablePrefix(new String[] { "" });// 此处可以修改为您的表前缀
		strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
		strategy.setSuperEntityClass("com.aofei.base.entity.DataEntity");
		strategy.setSuperServiceImplClass("com.aofei.base.service.impl.BaseService");
		strategy.setSuperMapperClass("com.aofei.base.mapper.BaseMapper");
		strategy.setSuperControllerClass("com.aofei.base.controller.BaseController");
		strategy.setEntityLombokModel(true);

		//strategy.setSuperEntityColumns(new String[]{"CREATE_USER","UPDATE_USER","CREATE_TIME","UPDATE_TIME","DEL_FLAG"});
		strategy.setInclude(new String[] {
				 "PROFILE_TABLE"
				,"PROFILE_TABLE_COLUMN"
				,"PROFILE_TABLE_GROUP"
				,"PROFILE_TABLE_RESULT"
				});
		//strategy.setInclude(new String[] {"SYS_MENU"});
		//strategy.setFieldPrefix(new String[] {"C_"});
		//strategy.setInclude(new String[] { "KDI_QRTZ_GROUP" });

		mpg.setStrategy(strategy);

		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setModuleName("profile");

		pc.setParent("com.aofei");// 自定义包路径
		pc.setController("controller");// 这里是控制器包名，默认 web
		mpg.setPackageInfo(pc);
		// 执行生成
		mpg.execute();
	}

}
