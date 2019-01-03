package com.aofei.sys.utils;

import com.aofei.sys.model.response.RepositoryExplorerTreeResponse;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Tony
 * @create 2018-10-09 14:43
 */
public class RepositoryUtils {

    public static List<RepositoryExplorerTreeResponse> getRepositoryExplorerTree(Repository repository) throws KettleException {

        RepositoryDirectoryInterface dir = repository.getUserHomeDirectory();

        return getRepositoryExplorerTree(repository,dir);

    }

    private static List<RepositoryExplorerTreeResponse> getRepositoryExplorerTree(Repository repository, RepositoryDirectoryInterface dir) throws KettleException {
        List<RepositoryExplorerTreeResponse> list = new ArrayList<>();
        for(RepositoryDirectoryInterface child :  dir.getChildren()){

            List<RepositoryElementMetaInterface> transformationObjects = repository.getTransformationObjects(child.getObjectId(), false);
            if(transformationObjects != null) {
                for(RepositoryElementMetaInterface e : transformationObjects) {
                    String transPath = dir.getPath();
                    if(!transPath.endsWith("/"))
                        transPath = transPath + '/';
                    transPath = transPath + e.getName();
                    list.add(RepositoryExplorerTreeResponse.initTree(e.getName(),  transPath, e.getObjectType()));
                }
            }
            List<RepositoryElementMetaInterface> jobObjects = repository.getJobObjects(child.getObjectId(), false);
            if(jobObjects != null) {
                for(RepositoryElementMetaInterface e : jobObjects) {
                    String transPath = dir.getPath();
                    if(!transPath.endsWith("/"))
                        transPath = transPath + '/';
                    transPath = transPath + e.getName();
                    list.add(RepositoryExplorerTreeResponse.initTree(e.getName(),  transPath, e.getObjectType()));
                }
            }

            RepositoryExplorerTreeResponse response =  RepositoryExplorerTreeResponse.initTree(child.getName(), child.getPath());
            response.setChildren(getRepositoryExplorerTree(repository,child));
            list.add(response);
        }

        return list;
    }
}
