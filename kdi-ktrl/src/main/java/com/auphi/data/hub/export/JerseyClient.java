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
package com.auphi.data.hub.export;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;




import com.sun.jersey.api.client.Client;


public class JerseyClient {
	private static Log log = LogFactory.getLog(JerseyClient.class);
	
	 public static void jerseyClient(String url,Object obj){
	        ClientConfig clientConfig = new DefaultClientConfig();
	        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,Boolean.TRUE);
	        Client client = Client.create(clientConfig);
	        WebResource webResource = client.resource(url);
	        ClientResponse response = webResource.accept("application/json").type("application/json").post(ClientResponse.class, obj);
	        if (response.getStatus() != 200) {
	            throw new RuntimeException("Failed : HTTP error code : " +  response.getStatus());
	        }
	        String output = response.getEntity(String.class);
	        log.info("Server response .... \n");
	        log.info(output);
	    }
	
}
