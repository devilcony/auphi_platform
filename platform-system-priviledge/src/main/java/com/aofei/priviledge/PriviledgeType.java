package com.aofei.priviledge;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public enum PriviledgeType {
    //File
    CreateFile(1, OperationType.Create, ResourceType.File),
    ModifyFile(CreateFile.priviledgeId << 1, OperationType.Modify, ResourceType.File),
    DeleteFile(ModifyFile.priviledgeId << 1, OperationType.Delete, ResourceType.File),
    ReadFile(DeleteFile.priviledgeId << 1, OperationType.Read, ResourceType.File),
    ExecuteFile(ReadFile.priviledgeId << 1, OperationType.Execute, ResourceType.File),

    //Directory
    CreateDirectory(ExecuteFile.priviledgeId << 1, OperationType.Create, ResourceType.Directory),
    ModifyDirectory(CreateDirectory.priviledgeId << 1, OperationType.Modify, ResourceType.Directory),
    DeleteDirectory(ModifyDirectory.priviledgeId << 1, OperationType.Delete, ResourceType.Directory),
    ReadDirectory(DeleteDirectory.priviledgeId << 1, OperationType.Read, ResourceType.Directory),


    //Cluster
    CreateCluster(ReadDirectory.priviledgeId << 1, OperationType.Create, ResourceType.Cluster),
    ModifyCluster(CreateCluster.priviledgeId << 1, OperationType.Modify, ResourceType.Cluster),
    DeleteCluster(ModifyCluster.priviledgeId << 1, OperationType.Delete, ResourceType.Cluster),

    //Datasource
    CreateDatasource(DeleteCluster.priviledgeId << 1, OperationType.Create, ResourceType.Datasource),
    ModifyDatasource(CreateDatasource.priviledgeId << 1, OperationType.Modify, ResourceType.Datasource),
    DeleteDatasource(ModifyDatasource.priviledgeId << 1, OperationType.Delete, ResourceType.Datasource),
    ReadeDatasource(DeleteDatasource.priviledgeId << 1, OperationType.Read, ResourceType.Datasource);

    final long priviledgeId;
    final OperationType opeartionType;
    final ResourceType resourceType;
    final String priviledgeName;

    PriviledgeType(long priviledgeId, OperationType operationType, ResourceType resourceType) {
        this.priviledgeId = priviledgeId;
        this.resourceType = resourceType;
        this.opeartionType = operationType;
        this.priviledgeName = operationType.getOperationName() + resourceType.getResourceName();
    }

    public long getPriviledgeId() {
        return this.priviledgeId;
    }

    public String getPriviledgeName() {
        return this.priviledgeName;
    }



    public OperationType getOpeartionType() {
        return opeartionType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getPriviledge_name() {
        return priviledgeName;
    }

    public static String toJSONString() {
        JSONArray jsonArray = new JSONArray();

        for (ResourceType resourceType : ResourceType.values()) {
            JSONObject object = new JSONObject();
            object.put("id","d_"+resourceType.resourceTypeId);
            object.put("text",resourceType.resourceName);
            object.put("leaf",false);
            object.put("expanded",true);
            JSONArray children = new JSONArray();

            for(PriviledgeType priviledgeType : PriviledgeType.values()){
                if(priviledgeType.getResourceType().resourceTypeId == resourceType.resourceTypeId){
                    JSONObject priviledge = new JSONObject();
                    priviledge.put("id",priviledgeType.priviledgeId);
                    priviledge.put("text",priviledgeType.priviledgeName);
                    priviledge.put("leaf",true);
                    priviledge.put("expanded",false);
                    children.add(priviledge);
                }
            }

            object.put("children",children);
            jsonArray.add(object);
        }
        return jsonArray.toJSONString();
    }

    public static String toJSONString(long privil ) {
        JSONArray jsonArray = new JSONArray();

        for (ResourceType resourceType : ResourceType.values()) {
            JSONObject object = new JSONObject();
            object.put("id","d_"+resourceType.resourceTypeId);
            object.put("text",resourceType.resourceName);
            object.put("leaf",false);
            object.put("expanded",true);
            JSONArray children = new JSONArray();

            for(PriviledgeType priviledgeType : PriviledgeType.values()){
                if(priviledgeType.getResourceType().resourceTypeId == resourceType.resourceTypeId){
                    JSONObject priviledge = new JSONObject();
                    priviledge.put("id",priviledgeType.priviledgeId);
                    priviledge.put("text",priviledgeType.priviledgeName);
                    priviledge.put("leaf",true);
                    priviledge.put("expanded",false);
                    priviledge.put("select",hasPriviledge(privil,priviledgeType));
                    children.add(priviledge);
                }
            }

            object.put("children",children);
            jsonArray.add(object);
        }
        return jsonArray.toJSONString();
    }

    public static String toJSONString(long[] priviledges ) {
        JSONArray jsonArray = new JSONArray();

        for (ResourceType resourceType : ResourceType.values()) {
            JSONObject object = new JSONObject();
            object.put("id","d_"+resourceType.resourceTypeId);
            object.put("text",resourceType.resourceName);
            object.put("leaf",false);
            object.put("expanded",true);
            JSONArray children = new JSONArray();

            for(PriviledgeType priviledgeType : PriviledgeType.values()){
                if(priviledgeType.getResourceType().resourceTypeId == resourceType.resourceTypeId){
                    JSONObject priviledge = new JSONObject();
                    priviledge.put("id",priviledgeType.priviledgeId);
                    priviledge.put("text",priviledgeType.priviledgeName);
                    priviledge.put("leaf",true);
                    priviledge.put("expanded",false);
                    priviledge.put("select",hasPriviledge(priviledges,priviledgeType));
                    children.add(priviledge);
                }
            }

            object.put("children",children);
            jsonArray.add(object);
        }
        return jsonArray.toJSONString();
    }



    public static boolean hasPriviledge(long priviledges, PriviledgeType privilidge) {
        return (priviledges & privilidge.getPriviledgeId()) == privilidge.getPriviledgeId();
    }

    public static boolean hasPriviledge(long[] priviledges, PriviledgeType privilidge) {
        for(long priviledge : priviledges){
            if(hasPriviledge(priviledge,privilidge)){
               return true;
            }
        }
        return false;
    }

    public static long  getPriviledges(String priviledges) {
        if (priviledges == null)
            return 0;

        // Priviledge string array to priviledge number
        String[] priviledge = priviledges.split(",");
        long pri = 0;
        for (int i = 1; i < priviledge.length; i++)
            pri = pri | Long.parseLong(priviledge[i]);
        pri = pri | Long.parseLong(priviledge[0]);

        return pri;
    }

    public static boolean hasPriviledge(long priviledges, String itemNeed) {
        char[] ch = itemNeed.toCharArray();
        if (ch[0] == '1') {
            if (hasPriviledge(priviledges, PriviledgeType.CreateFile)) {
                return true;
            }
        }
        if (ch[1] == '1') {
            if (hasPriviledge(priviledges, PriviledgeType.ModifyFile)) {
                return true;
            }
        }
        if (ch[2] == '1') {
            if (hasPriviledge(priviledges, PriviledgeType.DeleteFile)) {
                return true;
            }
        }
        if (ch[3] == '1') {
            if (hasPriviledge(priviledges, PriviledgeType.ReadFile)) {
                return true;
            }
        }
        if (ch[4] == '1') {
            if (hasPriviledge(priviledges, PriviledgeType.ExecuteFile)) {
                return true;
            }
        }
        return false;
    }
}
