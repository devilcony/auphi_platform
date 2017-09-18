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
package com.auphi.data.hub.core.util;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;


public class HadoopUtil {

	private String server;
	private int port;
	private String userid;
	private String password;
	
	private static HadoopUtil myins;


	public static HadoopUtil getInstence() {
		if (myins == null) {
			myins = new HadoopUtil();
		}

		return myins;
	}
	
	public boolean test(String host, int portno, String username, String password){
		boolean isconn = false;
		 try{
			  String dst = "hdfs://"+host+":"+portno+"/";  
			  Configuration conf = new Configuration();  
			  //conf.set("hadoop.job.ugi", "root,123456");  //设置存储服务器的用户名，密码
			  FileSystem fs = FileSystem.get(URI.create(dst), conf);
//			  FileStatus fileList[] = fs.listStatus(new Path(dst));
//			  int size = fileList.length;
//			  for(int i = 0; i < size; i++){
//			  System.out.println("name:" + fileList[i].getPath().getName() + ",size:" + fileList[i].getLen());
//			  }
			  fs.close();
			  isconn = true;
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return isconn;
	}
	
}
