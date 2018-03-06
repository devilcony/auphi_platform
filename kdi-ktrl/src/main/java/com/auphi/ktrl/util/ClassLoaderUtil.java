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
package com.auphi.ktrl.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ClassLoaderUtil extends URLClassLoader{
	
	private static Logger logger = Logger.getLogger(ClassLoaderUtil.class);
	
	//private URLClassLoader system = (URLClassLoader)ClassLoader.getSystemClassLoader();
	
	public ClassLoaderUtil() {
		super(new URL[]{}, Thread.currentThread().getContextClassLoader());
		// TODO Auto-generated constructor stub
	}
	
    /** 
     * 循环遍历目录，找出所有的JAR包 
     */  
    private void loopFiles(File file, List<File> files) {  
        if (file.isDirectory()) {  
            File[] tmps = file.listFiles();  
            for (File tmp : tmps) {  
                loopFiles(tmp, files);  
            }  
        } else {  
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {  
                files.add(file);  
            }  
        }  
    }  
  
    /** 
     * <pre> 
     * 加载JAR文件 
     * </pre> 
     * 
     * @param file 
     */  
    public void loadJarFile(File file) {  
        try {  
            addURL( file.toURI().toURL() );  
            //System.out.println("加载JAR包：" + file.getAbsolutePath());  
        } catch (Exception e) {  
            logger.error(e.getMessage(),e);  
        }  
    }  
  
    /** 
     * <pre> 
     * 从一个目录加载所有JAR文件 
     * </pre> 
     * 
     * @param path 
     */  
    public void loadJarPath(String path) { 
    	String classPath = "";
    	if(getClass().getResource("/") != null){
    		classPath = this.getClass().getResource("/").getPath();
    	}else {
    		classPath = this.getClass().getResource("").getPath();
    	}
    	classPath = classPath.substring(0, classPath.indexOf("WEB-INF/")).replaceAll("%20", " ").replaceAll("file:", "");
        List<File> files = new ArrayList<File>();  
        File lib = new File(classPath + path);
        loopFiles(lib, files);  
        for (File file : files) {  
            loadJarFile(file);  
        }  
    }  
}
