package com.aofei.priviledge;


import com.aofei.priviledge.i18n.Messages;

enum OperationType
{
    
    Create(1,"Priviledge.OperationType.Create"),
    
    Delete(2,"Priviledge.OperationType.Delete"),
    
    Modify(4,"Priviledge.OperationType.Modify"),
    
    Execute(8,"Priviledge.OperationType.Execute"),
    
    Read(16,"Priviledge.OperationType.Read") ;


    final int operationTypeId ;
    final String operationName;

    OperationType(
        int operationTypeId,
        String operationName)
    {
        this.operationTypeId = operationTypeId ;
        this.operationName = Messages.getString(operationName) ;
    }
    
    public int getOperationTypeId()
    {
        return this.operationTypeId ;
    }
    
    public String getOperationName()
    {
        return this.operationName ;
    }
    
    public static OperationType getOperationType(int operationTypeId) throws Exception
    {
        switch(operationTypeId)
        {
            case 1:
                return Create ;
            case 2:
                return Delete ;
            case 4:
                return Execute ;
            case 8:
                return Modify ;
            case 16:
                return Read ;
            default:
                throw new Exception("Unsupported operation type!") ;
                
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < OperationType.values().length; i++) {
            //INSERT kdi_t_resource_type VALUES (1,'目录');
            System.out.println("INSERT INTO KDI_SYS_OPERATION VALUES(" + OperationType.values()[i].operationTypeId
                    + ",'" + OperationType.values()[i].operationName + "');");
        }
        return;
    }

}
