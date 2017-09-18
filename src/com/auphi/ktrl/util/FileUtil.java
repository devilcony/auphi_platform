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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class FileUtil
{
	private static Logger logger = Logger.getLogger(FileUtil.class);
	
    /**
     * get data from stream and save it into a file
     * @param path filepath
     * @param ins stream
     * @throws Exception
     */
    public static void saveAsFile(String path, InputStream ins) throws Exception{
        File file = new File(path);
        if(!file.exists()){
            File dir = file.getParentFile();
            dir.mkdirs();
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream buffOut = new BufferedOutputStream(fos);
        byte buf[]=new byte[2048];
        for(int i=0;(i=ins.read(buf))>0;){
            buffOut.write(buf,0,i);
        }
        buffOut.close();
        fos.close();
    }
    /**
     * 
     * @param filename
     * @param fileContent
     * @param isAppend
     * @throws IOException 
     */
    public static void writeFile(String filename,String fileContent,boolean isAppend) throws IOException
    {
    	
		createFile(filename);
		FileWriter fw = new FileWriter(filename,isAppend);
		fw.write(fileContent);
		fw.flush();
		fw.close();
    }
    /**
     * 
     * @param fileName
     */
    public static void createFile(String fileName)
    {
    	try
    	{
    		File file = new File(fileName);
    		if(!file.exists())
    		{
    			File dir = file.getParentFile();
    	        dir.mkdirs();
    			file.createNewFile();
    		}
    	}
    	catch(Exception e)
    	{
    		logger.error(e.getMessage(),e);
    	}
    }
    /**
     * 
     * @param fileName
     * @param xpaths
     * @return
     */
	 public static List<List<String>> getElementValueXML(String fileName,String[] xpaths)
	    {
		 	List<List<String>> elementsValueList = new ArrayList<List<String>>();
	        try
	        {
	            SAXReader saxReader = new SAXReader();
	            File file = new File(fileName);
	            if(file.exists())
	            {
	                saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	                Document document = saxReader.read(file);
	                for(int i=0;i<xpaths.length;i++)
	                {
	                	List<String> valuesList = new ArrayList<String>();
		                List list = document.selectNodes(xpaths[i]);// xpath = /books/book
		                Iterator iter = list.iterator();
		                while(iter.hasNext())
		                {
		                    Element element = (Element)iter.next();
		                    valuesList.add(element.getText());
		                }
		                elementsValueList.add(valuesList);
	                }
	            }
	        }
	        catch(Exception e)
	        {
	            logger.error(e.getMessage(),e);
	        }
	        
	        List<List<String>> returnValuesList = new ArrayList<List<String>>();
	        if(elementsValueList != null && elementsValueList.size() !=0 && 
	           elementsValueList.get(0) != null && elementsValueList.get(0).size() != 0)
	        {
	        	int outSize = elementsValueList.get(0).size();
	        	for(int i=0;i<outSize;i++)
	        	{
	        		List<String> valuesList = new ArrayList<String>();
	        		for(int j=0;j<elementsValueList.size();j++)
	        		{
	        			valuesList.add(elementsValueList.get(j).get(i));
	        		}
	        		returnValuesList.add(valuesList);
	        	}
	        }
			
	        
	        return returnValuesList;
	    }
	 /**
	  * 
	  * @param filePath
	  * @return
	  */
	 public static synchronized String readFile(String filePath){
		 StringBuffer sb = new StringBuffer();
		 try{
			 File f = new File(filePath);
			 if(f.exists()){
			 InputStreamReader read = new InputStreamReader (new FileInputStream(f),"GBK");
			 BufferedReader reader=new BufferedReader(read);
			 String line;
			 while ((line = reader.readLine()) != null) {
				 sb.append(line);
			 }}
		 }catch(Exception e){
			 logger.error(e.getMessage(),e);
		 }
		 return sb.toString();
	 }
	 
		/**
		 * 
		 * @param dir
		 * @param prefix
		 * @param extension
		 * @return
		 */
		public static boolean deletefile(String dir,String prefix,String extension){
			boolean isSuccess = true;
			try{
				File file = new File(dir);
					file = file.getParentFile();
					File[] fileList = file.listFiles();
					for(File f:fileList){
						if(f.getName().startsWith(prefix) && f.getName().endsWith(extension))
						{
							f.delete();
						}
					}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			return isSuccess;
		}
		public static synchronized boolean deletefileOther(String dir,String prefix,String extension,String OtherfileName,int maxfilelength){
			boolean isSuccess = false;
			try{
				File file = new File(dir);
					file = file.getParentFile();
					File[] fileList = file.listFiles();
					int count = 0;
					for(File f:fileList){
						if(f.getName().startsWith(prefix) && f.getName().toLowerCase().endsWith(extension.toLowerCase()) ){
								count++;
						}
					}
					if(count > maxfilelength){
						for(File f:fileList){
							if(f.getName().startsWith(prefix) && f.getName().toLowerCase().endsWith(extension.toLowerCase()) &&
									fileList.length > maxfilelength && !f.getName().equals(OtherfileName)){
								f.delete();
								isSuccess = true;
							}
						}
					}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			return isSuccess;
		}
		
		
		public static synchronized File getFile(String filePath,String prefix,String extension) {
			File file = new File(filePath);
				file = file.getParentFile();
				File[] files = file.listFiles();
				for(File f:files){
					if(f.getName().startsWith(prefix) && f.getName().toLowerCase().endsWith(extension.toLowerCase())){
						file = f;
						break;
					}
				}
			return file;
		}
		
		public static boolean findfileExists(File file){
			boolean ret = false;
			if(file.exists())
				ret = true;
			else{
				ret = findfileExists(file);
			}
			return ret;
		}
		
		public static boolean isExist(File file){
			return file.exists();
		}
		
		public static boolean isExist(String filepath){
			File file = new File(filepath);
			return file.exists();
		}
		public static boolean createFileDir(String fileDir){
			File file = new File(fileDir);
			if(!file.exists()){
				file.mkdirs();
			}
			return true;
		}
		/**
		 * 
		 * @param f1
		 * @param f2
		 * @return
		 * @throws Exception
		 */
	public static boolean copyFile(File f1, File f2) throws Exception {
		byte[] buffer = new byte[1024];
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(f1.getAbsolutePath());
			out = new FileOutputStream(f2.getAbsolutePath());
			int c;
			while ((c = in.read(buffer)) >= 0) {
				out.write(buffer, 0, c);
			}
			return true;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception err) {
					// Ignore the exception.
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception err) {
					// Ignore the exception.
				}
			}
		}
	}
}
