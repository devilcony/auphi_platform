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
package com.auphi.ktrl.system;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.auphi.ktrl.system.user.bean.LoginResponse;

public class test
{
    public static void main(String[] args) throws Exception
    {
        // Create a URLConnection object for a URL        
        URL url = new URL("http://localhost:8080/KDI_V3.0_Platform/usermanager?action=login&user_name=TEST_USER&password=123456") ;
        URLConnection conn = url.openConnection() ;
        
//        // List all the response headers from the server.
//        // Note: The first call to getHeaderFieldKey() will implicit send
//        // the HTTP request to the server.
//        for (int i=0; ; i++) 
//        {
//            
//            String headerName = conn.getHeaderFieldKey(i);
//            String headerValue = conn.getHeaderField(i);
//
//            if (headerName == null && headerValue == null) 
//            {
//                // No more headers
//                break;
//            }
//            if (headerName == null) 
//            {
//                // The header value contains the server's HTTP version
//            }
//            System.out.println(headerValue) ;
//        }
        InputStream in = conn.getInputStream();
        String encoding = conn.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        String body = IOUtils.toString(in, encoding);
        LoginResponse lrs = LoginResponse.decode(body) ;
        
        System.out.println(body);
        System.out.println(lrs.toJSONString()) ;
        
//        RandomAccessFile raf = null ;
//        raf.skipBytes(1) ;
//        raf.close() ;
        
    }
    public static void importData()
    {
        
    }
}
