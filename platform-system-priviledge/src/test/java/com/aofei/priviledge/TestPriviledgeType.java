package com.aofei.priviledge;

import com.aofei.priviledge.i18n.Messages;
import org.junit.jupiter.api.Test;

/**
 * @auther Tony
 * @create 2018-09-30 19:21
 */
public class TestPriviledgeType {

    @Test
    public  void main() {

        System.out.println(Messages.getString("Priviledge.OperationType.Create"));
        PriviledgeType.hasPriviledge(11, PriviledgeType.CreateFile);
        //INSERT INTO [dbo].[KDI_T_PRIVILEDGE] VALUES (65536,8,16);
        String sql_prefix = "INSERT INTO KDI_SYS_ROLE_PRIVILEDGE VALUES(";
        PriviledgeType[] priviledges = PriviledgeType.values();
        for (int i = 0; i < priviledges.length; i++)
            System.out.println(sql_prefix + priviledges[i].priviledgeId + "," + priviledges[i].resourceType.getResourceTypeId() + "," + priviledges[i].opeartionType.getOperationTypeId() + ");");
    }

    @Test
    public  void getJsonStr() {
        System.out.println(PriviledgeType.toJSONString());
    }



}
