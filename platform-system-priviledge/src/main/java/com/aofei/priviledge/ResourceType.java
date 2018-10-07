package com.aofei.priviledge;


import com.aofei.priviledge.i18n.Messages;

enum ResourceType {

    File(1, "Priviledge.ResourceType.File"),

    Directory(2, "Priviledge.ResourceType.Directory"),

    Cluster(4, "Priviledge.ResourceType.Cluster"),

    Datasource(8, "Priviledge.ResourceType.Datasource");

    final int resourceTypeId;
    final String resourceName;

    ResourceType(int resourceTypeId, String resourceName_property) {
        this.resourceTypeId = resourceTypeId;
        this.resourceName = Messages.getString(resourceName_property);
    }

    public int getResourceTypeId() {
        return this.resourceTypeId;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public static ResourceType getResourceType(int resourceTypeId) {
        switch (resourceTypeId) {
            case 1:
                return File;
            case 2:
                return Directory;
            case 4:
                return Cluster;
            case 8:
                return Datasource;
        }
        return null;
    }

    public static void main(String[] args) {
        for (int i = 0; i < ResourceType.values().length; i++) {
            //INSERT kdi_t_resource_type VALUES (1,'目录');
            System.out.println("INSERT INTO KDI_SYS_RESOURCE_TYPE VALUES(" + ResourceType.values()[i].resourceTypeId
                    + ",'" + ResourceType.values()[i].resourceName + "');");
        }
        return;
    }
}
