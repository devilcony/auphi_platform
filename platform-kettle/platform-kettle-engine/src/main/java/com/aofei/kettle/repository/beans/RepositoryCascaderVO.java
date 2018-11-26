package com.aofei.kettle.repository.beans;

import lombok.Data;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;

import java.util.List;

@Data
public class RepositoryCascaderVO {

    public RepositoryCascaderVO(){

    }
    public RepositoryCascaderVO(String label,String value){
        setLabel(label);
        setValue(value);
    }

    private String value;

    private String label;

    private List<RepositoryCascaderVO> children;



    public RepositoryCascaderVO(RepositoryElementMetaInterface re, String v) {
        value = re.getName()+v;
        label = re.getName()+v;
    }

    public RepositoryCascaderVO(RepositoryDirectoryInterface child) {
        value = child.getName();
        label = child.getName();
    }
}
