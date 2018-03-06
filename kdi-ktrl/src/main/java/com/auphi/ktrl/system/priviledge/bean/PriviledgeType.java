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
package com.auphi.ktrl.system.priviledge.bean;

public enum PriviledgeType
{
    //  Create resource 
    CreateFile(1,OperationType.Create,ResourceType.File),
    CreateDirectory(CreateFile.priviledge_id<<1,OperationType.Create,ResourceType.Directory),
    CreateUser(CreateDirectory.priviledge_id<<1,OperationType.Create,ResourceType.User),
    CreateRole(CreateUser.priviledge_id<<1,OperationType.Create,ResourceType.Role),
    CreateCluster(CreateRole.priviledge_id<<1,OperationType.Create,ResourceType.Cluster),
    
    //  Delete resource
    DeleteFile(CreateCluster.priviledge_id<<1,OperationType.Delete,ResourceType.File),
    DeleteDirectory(DeleteFile.priviledge_id<<1,OperationType.Delete,ResourceType.Directory),
    DeleteUser(DeleteDirectory.priviledge_id<<1,OperationType.Delete,ResourceType.User),
    DeleteRole(DeleteUser.priviledge_id<<1,OperationType.Delete,ResourceType.Role),
    DeleteCluster(DeleteRole.priviledge_id<<1,OperationType.Delete,ResourceType.Cluster),
    
    //  Modify resource
    ModifyFile(DeleteCluster.priviledge_id<<1,OperationType.Modify,ResourceType.File),
    ModifyDirectory(ModifyFile.priviledge_id<<1,OperationType.Modify,ResourceType.Directory),
    ModifyUser(ModifyDirectory.priviledge_id<<1,OperationType.Modify,ResourceType.User),
    ModifyRole(ModifyUser.priviledge_id<<1,OperationType.Modify,ResourceType.Role),
    ModifyCluster(ModifyRole.priviledge_id<<1,OperationType.Modify,ResourceType.Cluster),
    
    //  Execute resource
    ExecuteFile(ModifyCluster.priviledge_id<<1,OperationType.Execute,ResourceType.File),
    
    //  Read resource
    ReadFile(ExecuteFile.priviledge_id<<1,OperationType.Read,ResourceType.File),
    ReadDirectory(ReadFile.priviledge_id<<1,OperationType.Read,ResourceType.Directory),
    ReadUser(ReadDirectory.priviledge_id<<1,OperationType.Read,ResourceType.User),
    ReadeRole(ReadUser.priviledge_id<<1,OperationType.Read,ResourceType.Role) ;    

    final long priviledge_id ;
    final OperationType opeartionType ;
    final ResourceType resourceType ;
    final String priviledge_name;

    PriviledgeType(
        long priviledge_id,
        OperationType operationType,
        ResourceType resourceType)
    {
        this.priviledge_id = priviledge_id ; 
        this.resourceType = resourceType ;
        this.opeartionType = operationType;
        this.priviledge_name = operationType.getOperationName()+resourceType.getResourceName() ;
    }    
    
    public long getPriviledgeId()
    {
        return this.priviledge_id ;
    }
    
    public String getPriviledgeName()
    {
        return this.priviledge_name ;
    }

    public long getPriviledge_id()
    {
        return priviledge_id;
    }

    public OperationType getOpeartionType()
    {
        return opeartionType;
    }

    public ResourceType getResourceType()
    {
        return resourceType;
    }

    public String getPriviledge_name()
    {
        return priviledge_name;
    }
    
    public static String toJsonString()
    {
        StringBuffer sb = new StringBuffer(1024) ;
        sb.append("[") ;
            // File 
            sb.append("{id:'d_").append(ResourceType.File.getResourceTypeId()).append("',").
                                    append("text:'").append(ResourceType.File.getResourceName()).append("',").
                                    append("leaf:false,expanded: true,children:[") ;
                sb.append("{id:'").append(CreateFile.priviledge_id).append("',").
                append("text:'").append(OperationType.Create.getOperationName()).append("',").
                append("leaf:true,checked:false},") ;
                sb.append("{id:'").append(ModifyFile.priviledge_id).append("',").
                append("text:'").append(OperationType.Modify.getOperationName()).append("',").
                append("leaf:true,checked:false},") ;
                sb.append("{id:'").append(DeleteFile.priviledge_id).append("',").
                append("text:'").append(OperationType.Delete.getOperationName()).append("',").
                append("leaf:true,checked:false},") ;
                sb.append("{id:'").append(ExecuteFile.priviledge_id).append("',").
                append("text:'").append(OperationType.Execute.getOperationName()).append("',").
                append("leaf:true,checked:false},") ;
                sb.append("{id:'").append(ReadFile.priviledge_id).append("',").
                append("text:'").append(OperationType.Read.getOperationName()).append("',").
                append("leaf:true,checked:false}") ;                  
            sb.append("]}");
            
//            // Directory
//            sb.append(",{id:'d_").append(ResourceType.Directory.getResourceTypeId()).append("',").
//                                    append("text:'").append(ResourceType.Directory.getResourceName()).append("',").
//                                    append("leaf:false,expanded: true,children:[") ;
//                sb.append("{id:'").append(CreateDirectory.priviledge_id).append("',").
//                append("text:'").append(OperationType.Create.getOperationName()).append("',").
//                append("leaf:true,checked:false},") ;
//                sb.append("{id:'").append(ModifyDirectory.priviledge_id).append("',").
//                append("text:'").append(OperationType.Modify.getOperationName()).append("',").
//                append("leaf:true,checked:false},") ;
//                sb.append("{id:'").append(DeleteDirectory.priviledge_id).append("',").
//                append("text:'").append(OperationType.Delete.getOperationName()).append("',").
//                append("leaf:true,checked:false}") ;                
//            sb.append("]}");
        
            
            //Cluster
            sb.append(",{id:'d_").append(ResourceType.Cluster.getResourceTypeId()).append("',").
                append("text:'").append(ResourceType.Cluster.getResourceName()).append("',").
                append("leaf:false,expanded: true,children:[") ;
                sb.append("{id:'").append(CreateCluster.priviledge_id).append("',").
                append("text:'").append(OperationType.Create.getOperationName()).append("',").
                append("leaf:true,checked:false},") ;
                sb.append("{id:'").append(ModifyCluster.priviledge_id).append("',").
                append("text:'").append(OperationType.Modify.getOperationName()).append("',").
                append("leaf:true,checked:false},") ;
                sb.append("{id:'").append(DeleteCluster.priviledge_id).append("',").
                append("text:'").append(OperationType.Delete.getOperationName()).append("',").
                append("leaf:true,checked:false}") ;               
            sb.append("]}");
            
        sb.append("]") ;
        return sb.toString() ;
    }
    
    public static void main(String []args)
    {
        PriviledgeType.hasPriviledge(11, PriviledgeType.CreateFile) ;
        //INSERT INTO [dbo].[KDI_T_PRIVILEDGE] VALUES (65536,8,16);
        String sql_prefix = "INSERT INTO [dbo].[KDI_T_PRIVILEDGE] VALUES(" ;
        PriviledgeType[] priviledges = PriviledgeType.values() ;
        for (int i = 0 ; i < priviledges.length ; i ++)
            System.out.println(sql_prefix+priviledges[i].priviledge_id+","+priviledges[i].resourceType.getResourceTypeId()+","+priviledges[i].opeartionType.getOperationTypeId()+");") ;
    }
    
    public static boolean hasPriviledge(long priviledges, PriviledgeType privilidge)
    {
        return (priviledges & privilidge.getPriviledge_id()) ==  privilidge.getPriviledge_id();
    }
    
    public static long getPriviledges(String priviledges)
    {
        if (priviledges == null)
            return 0 ;
        
        // Priviledge string array to priviledge number
        String [] priviledge = priviledges.split(",") ;
        long pri = 0 ;
        for (int i = 1 ; i < priviledge.length ; i ++)
            pri = pri | Long.parseLong(priviledge[i]) ;
        pri = pri | Long.parseLong(priviledge[0]) ;
        
        return pri ;
    }    

    public static boolean hasPriviledge(long priviledges, String itemNeed){
    	char[] ch = itemNeed.toCharArray();
    	if(ch[0] == '1'){
    		if(hasPriviledge(priviledges,PriviledgeType.CreateFile )){
    			return true;
    		}
    	}
    	if(ch[1] == '1'){
    		if(hasPriviledge(priviledges,PriviledgeType.ModifyFile )){
    			return true;
    		}
    	}
    	if(ch[2] == '1'){
    		if(hasPriviledge(priviledges,PriviledgeType.DeleteFile )){
    			return true;
    		}
    	}
    	if(ch[3] == '1'){
    		if(hasPriviledge(priviledges,PriviledgeType.ReadFile )){
    			return true;
    		}
    	}
    	if(ch[4] == '1'){
    		if(hasPriviledge(priviledges,PriviledgeType.ExecuteFile )){
    			return true;
    		}
    	}
		return false;
    }
}
