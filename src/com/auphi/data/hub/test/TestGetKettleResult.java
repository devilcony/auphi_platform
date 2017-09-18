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
package com.auphi.data.hub.test;

import java.util.List;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleSecurityException;
import org.pentaho.di.trans.*;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.repository.*;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;

import com.auphi.data.hub.service.MyKettleDatabaseRepositoryMeta;

public class TestGetKettleResult {
	static KettleDatabaseRepositoryMeta meta = null;
	static DatabaseMeta database = null;
	
	private static MyKettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta;
	
	public static void main(String[] args) {
		
		TestGetKettleResult job = new TestGetKettleResult();
		try {
			database = new DatabaseMeta("Kettle_MySQL","mysql","jdbc","localhost","zss_mis","3306","zhangfeng","zhangfeng");
			meta = new KettleDatabaseRepositoryMeta("kettle","kettle","Transformation description",database);
//			kettleDatabaseRepositoryMeta = (MyKettleDatabaseRepositoryMeta) SpringBeanLoader.getInstance().getSpringBean(MyKettleDatabaseRepositoryMeta.class);

			job.exec();
		} catch (KettleSecurityException e) {
			e.printStackTrace();
		} catch (KettleException e) {
			e.printStackTrace();
		}

	}
	
	public void init()
	{
		try {
			KettleEnvironment.init();
		} catch (KettleException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean exec() throws KettleSecurityException, KettleException
	{
		
		init();
		KettleDatabaseRepository repository = new KettleDatabaseRepository();
		repository.init(meta);
		repository.connect("admin", "admin");
		RepositoryDirectoryInterface directoryInterface = repository.loadRepositoryDirectoryTree();
		TransMeta transMeta = repository.loadTransformation("table_input", directoryInterface, null, false, null);
		Trans trans = new Trans( transMeta);
		
		trans.execute(new String[]{}); // 执行转换，String[] 是命令行参数
		trans.waitUntilFinished(); // 等待所有子线程运行结束
		
		StepInterface si = trans.getStepInterface("result", 0);
		List list =  trans.getTransMeta().getResultRows();
		for(Object obj : list){
			System.out.println(obj);
		}
		
		
		return true;
	}
	
}
