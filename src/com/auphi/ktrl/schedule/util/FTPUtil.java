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
package com.auphi.ktrl.schedule.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.vfs.FileNotFoundException;
import org.apache.log4j.Logger;
import org.pentaho.di.core.encryption.Encr;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.schedule.template.TemplateUtil;
import com.auphi.ktrl.schedule.view.FTPView;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;
import com.auphi.ktrl.schedule.view.HashMapAndArray;

public class FTPUtil {
	private FTPClient ftpClient;  
    private String strIp;  
    private int intPort;  
    private String user;  
    private String password;  
  
    private static Logger logger = Logger.getLogger(FTPUtil.class.getName());
    
    /* * 
     * Ftp构造函数 
     */  
    public FTPUtil(FTPView ftpView) {
        this.strIp = ftpView.getStrIp();
        this.intPort = ftpView.getIntPort();  
        this.user = ftpView.getUser();  
        this.password =Encr.decryptPasswordOptionallyEncrypted(ftpView.getPassword());  
        this.ftpClient = new FTPClient();  
    }  
    /** 
     * @return 判断是否登入成功 
     * */  
    public boolean ftpLogin() {  
        boolean isLogin = false;  
        FTPClientConfig ftpClientConfig = new FTPClientConfig();  
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());  
        this.ftpClient.setControlEncoding("UTF-8");  
        this.ftpClient.configure(ftpClientConfig);  
        try {  
            if (this.intPort > 0) {  
                this.ftpClient.connect(this.strIp, this.intPort);  
            } else {  
                this.ftpClient.connect(this.strIp);  
            }  
            // FTP服务器连接回答  
            int reply = this.ftpClient.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                this.ftpClient.disconnect();  
                logger.error("登录FTP服务失败！");  
                return isLogin;  
            }  
            this.ftpClient.login(this.user, this.password);  
            // 设置传输协议  
            this.ftpClient.enterLocalPassiveMode();  
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            logger.info("恭喜" + this.user + "成功登陆FTP服务器");  
            isLogin = true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error(this.user + "登录FTP服务失败！" + e.getMessage());  
        }  
        this.ftpClient.setBufferSize(1024 * 2);  
        this.ftpClient.setDataTimeout(30 * 1000);  
        return isLogin;  
    } 
    
    /** 
     * @退出关闭服务器链接 
     * */  
    public void ftpLogOut() {  
        if (null != this.ftpClient && this.ftpClient.isConnected()) {  
            try {  
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器  
                if (reuslt) {  
                    logger.info("成功退出服务器");  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
                logger.warn("退出FTP服务器异常！" + e.getMessage());  
            } finally {  
                try {  
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    logger.warn("关闭FTP服务器的连接异常！");  
                }  
            }  
        }  
    }  
    /*** 
     * 上传Ftp文件 
     * @param localFile 当地文件 
     * @param romotUpLoadePath上传服务器路径 - 应该以/结束 
     * */  
    public boolean uploadFile(File localFile, String romotUpLoadePath) {  
        BufferedInputStream inStream = null;  
        boolean success = false;  
        try {  
            this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径  
            inStream = new BufferedInputStream(new FileInputStream(localFile));  
            logger.info(localFile.getName() + "开始上传.....");  
            success = this.ftpClient.storeFile(localFile.getName(), inStream);  
            if (success == true) {  
                logger.info(localFile.getName() + "上传成功");  
                return success;  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            logger.error(localFile + "未找到");  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (inStream != null) {  
                try {  
                    inStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return success;  
    }  
  
    /*** 
     * 下载文件 
     * @param remoteFileName   待下载文件名称 
     * @param localDires 下载到当地那个路径下 
     * @param remoteDownLoadPath remoteFileName所在的路径 
     * */  
  
    public boolean downloadFile(String remoteFileName, String localDires,  
            String remoteDownLoadPath) {  
        String strFilePath = localDires + remoteFileName;  
        BufferedOutputStream outStream = null;  
        boolean success = false;  
        try {  
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);  
            outStream = new BufferedOutputStream(new FileOutputStream(  
                    strFilePath));  
            logger.info(remoteFileName + "开始下载....");  
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);  
            if (success == true) {  
                logger.info(remoteFileName + "成功下载到" + strFilePath);  
                return success;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error(remoteFileName + "下载失败");  
        } finally {  
            if (null != outStream) {  
                try {  
                    outStream.flush();  
                    outStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        if (success == false) {  
            logger.error(remoteFileName + "下载失败!!!");  
        }  
        return success;  
    }  
  
    /*** 
     * @上传文件夹 
     * @param localDirectory 
     *            当地文件夹 
     * @param remoteDirectoryPath 
     *            Ftp 服务器路径 以目录"/"结束 
     * */  
    public boolean uploadDirectory(String localDirectory,  
            String remoteDirectoryPath) {  
        File src = new File(localDirectory);  
        try {  
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";  
            this.ftpClient.makeDirectory(remoteDirectoryPath);  
            // ftpClient.listDirectories();  
        } catch (IOException e) {  
            e.printStackTrace();  
            logger.info(remoteDirectoryPath + "目录创建失败");  
        }  
        File[] allFile = src.listFiles();  
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
            if (!allFile[currentFile].isDirectory()) {  
                String srcName = allFile[currentFile].getPath().toString();  
                uploadFile(new File(srcName), remoteDirectoryPath);  
            }  
        }  
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
            if (allFile[currentFile].isDirectory()) {  
                // 递归  
                uploadDirectory(allFile[currentFile].getPath().toString(),  
                        remoteDirectoryPath);  
            }  
        }  
        return true;  
    }  
  
    /*** 
     * @下载文件夹 
     * @param localDirectoryPath本地地址 
     * @param remoteDirectory 远程文件夹 
     * */  
    public boolean downLoadDirectory(String localDirectoryPath,String remoteDirectory) {  
        try {  
            String fileName = new File(remoteDirectory).getName();  
            localDirectoryPath = localDirectoryPath + fileName + "//";  
            new File(localDirectoryPath).mkdirs();  
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);  
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
                if (!allFile[currentFile].isDirectory()) {  
                    downloadFile(allFile[currentFile].getName(),localDirectoryPath, remoteDirectory);  
                }  
            }  
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {  
                if (allFile[currentFile].isDirectory()) {  
                    String strremoteDirectoryPath = remoteDirectory + "/"+ allFile[currentFile].getName();  
                    downLoadDirectory(localDirectoryPath,strremoteDirectoryPath);  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
            logger.info("下载文件夹失败");  
            return false;  
        }  
        return true;  
    }  
    
    /*** 
     * @读取文件 
     * @param fileName 文件名称，可以为正则表达式
     * @param delimiter 分隔符，
     * @param remoteDirectory 远程文件夹 
     * */  
    public  TreeMap<Integer, FieldMappingView> readFile(String fileName,Integer isFirstLineFieldName,String delimiter,String remoteDirectory)
    {
    	delimiter = getRegExp(delimiter);
    	TreeMap<Integer, FieldMappingView> sourceHashMap=new TreeMap<Integer, FieldMappingView>();
    	
    	try {
			FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
			   // 输出流用于输出文件  
            InputStream ins = null;
            String columnName[]=null;
            String content[]=null;
            int index=0;
			for (FTPFile ftpFile : allFile) {
				 if(ftpFile.isFile())
				 {
					 //正则匹配
//					 Pattern pattern = Pattern.compile(fileName);
//					 Matcher matcher = pattern.matcher(ftpFile.getName());
			         if(ftpFile.getName().matches(fileName)){   
			        	  String filePath="/"+remoteDirectory+"/"+ftpFile.getName();
			              // 从服务器上读取指定的文件
			              ins = ftpClient.retrieveFileStream(filePath);
			              
			              System.out.println(ftpFile.getName());
			              BufferedReader reader = new BufferedReader(new InputStreamReader(ins,"UTF-8"));
			              String line=null;
			              String secondLine=null;
			              String data=null;
			              Integer count=1;
			             switch (isFirstLineFieldName) {
						case 0:    //first line is column name
							while ((data = reader.readLine()) != null) {
								if(count==1)
								{
								  line=data;
								}
								if (count==2) {
									secondLine=data;
									break;		
								}
                                count++;
							   }
							 if(line!=null)
							 {
								  columnName=line.split(delimiter,-1);
								  content=secondLine.split(delimiter,-1);
					              for (int i=0; i < content.length; i++) {			            	  
					            	  FieldMappingView source= new FieldMappingView();
					            	  source.setSourceColumnName(columnName[i]);
						                //得到源字段类型
						              source.setReference(content[i]);
						              source.setSourceColumnType("");
						              source.setDestColumuName("");
						              source.setDestColumnType("String");
						            //  source.setDestColumuTypeViews(typeList);
						              source.setDestLength("");
						              source.setDestScale("-1");
						              source.setStartIndex(0);
						              source.setEndIndex(0);
						              source.setIsPrimary(false);
						              source.setIsNullable(false);
									  sourceHashMap.put(index, source);
									  index++;
					              }	 
							 }	
							  ins.close();
					          reader.close();
					          ftpClient.completePendingCommand(); 
							break;
						case 1:
							 line=reader.readLine();       // 读取第一行
							 if(line!=null)
							 {
								 content=line.split(delimiter,-1);
					              for (int i=0; i < content.length; i++) {			            	  
					            	  FieldMappingView source= new FieldMappingView();
					            	  source.setSourceColumnName("FIELD"+index);
						                //得到源字段类型
						              source.setReference(content[i]);
						              source.setSourceColumnType("");
						              source.setDestColumuName("");
						              source.setDestColumnType("String");
						            //  source.setDestColumuTypeViews(typeList);
						              source.setDestLength("");
						              source.setDestScale("-1");
						              source.setStartIndex(0);
						              source.setEndIndex(0);
						              source.setIsPrimary(false);;
						              source.setIsNullable(false);
									  sourceHashMap.put(index, source);
									  index++;
								}							 
							 } 
                            ins.close();
				            reader.close();
				            ftpClient.completePendingCommand(); 
							break;

						default:
							break;
						}		            	  		             
			         }
				 }
   
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    	return sourceHashMap;
    }
    private String getRegExp(String delimiter) {
    	if(delimiter ==null || delimiter.length()==0)
    	{
    		return delimiter;
    	}
    	
		if(delimiter.length()==1)
			return "["+delimiter+"]";
		else
		{ 
			StringBuffer result = new StringBuffer();
			for(int i=0;i<delimiter.length();i++)
			{
				result.append("[").append(delimiter.charAt(i)).append("]");
			}
			return result.toString();
		}
	}
    
	public static boolean testFTP( FastConfigView fastConfigView)
    {
    	HashMap<Integer, FieldMappingView> sourceHashMap=new HashMap<Integer, FieldMappingView>();
    	HashMapAndArray hashMapAndArray= new HashMapAndArray();
		Connection connection=null;
		PreparedStatement smt=null;
		ResultSet rs= null;
        connection = ConnectionPool.getConnection();

        FTPView ftpView= new FTPView();
        try {
            smt=  connection.prepareStatement("SELECT *FROM KDI_T_FTP  WHERE ID_FTP =?");
            smt.setInt(1, fastConfigView.getIdSourceFTP());
            rs=smt.executeQuery();
            while (rs.next()) {
				ftpView.setStrIp(rs.getString("HOST_NAME"));
				ftpView.setIntPort(Integer.valueOf(rs.getString("PORT")));
				ftpView.setUser(rs.getString("USERNAME"));
				ftpView.setPassword(rs.getString("PASSWORD"));           				
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.getLogger(e.getMessage());
		}finally{
			try {
				 if(smt!=null)
		        	{
		        	  smt.close();
		        	}
		            if(rs!=null)
		            {
		            	rs.close();
		            }
		            if(connection!=null)
		            {
		               connection.close();
		            }
			} catch (Exception e2) {
				// TODO: handle exception
			}
           
		}              
    	FTPUtil ftpUtil= new FTPUtil(ftpView);
    
    	if(ftpUtil.ftpLogin())
    	{
    		ftpUtil.ftpLogOut();
    	    return true;
    	}else {
    	    return false;
		}
    }  
    public static HashMapAndArray getSourceHashMap( FastConfigView fastConfigView)
    {
    	TreeMap<Integer, FieldMappingView> sourceHashMap=new TreeMap<Integer, FieldMappingView>();
    	HashMapAndArray hashMapAndArray= new HashMapAndArray();
		Connection connection=null;
		PreparedStatement smt=null;
		ResultSet rs= null;
        connection = ConnectionPool.getConnection();

        FTPView ftpView= new FTPView();
        try {
            smt=  connection.prepareStatement("SELECT *FROM KDI_T_FTP  WHERE ID_FTP =?");
            smt.setInt(1, fastConfigView.getIdSourceFTP());
            rs=smt.executeQuery();
            while (rs.next()) {
				ftpView.setStrIp(rs.getString("HOST_NAME"));
				ftpView.setIntPort(Integer.valueOf(rs.getString("PORT")));
				ftpView.setUser(rs.getString("USERNAME"));
				ftpView.setPassword(rs.getString("PASSWORD"));           				
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.getLogger(e.getMessage());
		}finally{
			try {
				 if(smt!=null)
		        	{
		        	  smt.close();
		        	}
		            if(rs!=null)
		            {
		            	rs.close();
		            }
		            if(connection!=null)
		            {
		               connection.close();
		            }
			} catch (Exception e2) {
				// TODO: handle exception
			}
           
		}              
    	FTPUtil ftpUtil= new FTPUtil(ftpView);
    
    	if(ftpUtil.ftpLogin())
    	{
    		Date date =new Date();
    		long time = System.currentTimeMillis()-24*60*60*1000;//yesterday
    		date.setTime(time);
    		String fileNameWithVariable=fastConfigView.getSourceFileName();
    		String fileName  = fileNameWithVariable;
			try {
				fileName = TemplateUtil.replaceVariable(fileNameWithVariable,date,false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	   sourceHashMap=ftpUtil.readFile(fileName, fastConfigView.getIsFirstLineFieldName(),fastConfigView.getSourceSeperator(),fastConfigView.getSourceFilePath());
    	   ftpUtil.ftpLogOut();
    	}
    	hashMapAndArray.setHashMap(sourceHashMap);
    	return hashMapAndArray;
    }
    public static void main(String[] args) {
    	String str="2015-01-25 00:00:00|60|SHTMME101BHw||SHTMME101BHw/跟踪区:460002300|107.0|1146.0|||";
    	String delimiter="[|]";
    	String[] c = str.split(delimiter,-1);
    	for (int i=0;i<c.length;i++)
    	{
    		System.out.println(c[i]);
    	}
    	
//    	String str="-1457@1276@#$|L2碣石湖坑村R2@#$|SWM03B1@#$|2015-01-25 05:00:00.0@#$|60@#$|小区名称=L2";
//    	String delimiter="[[@][#][$][|]]";
//    	String[] c = str.split(delimiter);
//    	for (int i=0;i<c.length;i++)
//    	{
//    		System.out.println(c[i]);
//    	}
    	

	}
    
    // FtpClient的Set 和 Get 函数  
    public FTPClient getFtpClient() {  
        return ftpClient;  
    }  
    public void setFtpClient(FTPClient ftpClient) {  
        this.ftpClient = ftpClient;  
    }  
    
}
