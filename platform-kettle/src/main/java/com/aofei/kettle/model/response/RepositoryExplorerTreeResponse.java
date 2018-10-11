package com.aofei.kettle.model.response;

import lombok.Data;
import org.pentaho.di.repository.RepositoryObjectType;

import java.util.List;

/**
 * @auther Tony
 * @create 2018-10-09 13:59
 */
@Data
public class RepositoryExplorerTreeResponse {

    private boolean expanded;
    private String iconCls;
    private String id;
    private boolean leaf;
    private String path;
    private String text;
    private String type;
    private List<RepositoryExplorerTreeResponse> children;

    public static RepositoryExplorerTreeResponse initTree(String text, String path) {
        return initTree(text, path, null, false, false, null);
    }

    public static RepositoryExplorerTreeResponse initTree(String text, String path, RepositoryObjectType type) {
        if(RepositoryObjectType.TRANSFORMATION.equals(type))
            return initTree(text, path, "trans", true, false, type.getTypeDescription());
        else if(RepositoryObjectType.JOB.equals(type))
            return initTree(text, path, "job", true, false, type.getTypeDescription());
        return null;
    }

    public static RepositoryExplorerTreeResponse initTree(String text, String path, String iconCls, boolean leaf, boolean expanded, String type) {
        RepositoryExplorerTreeResponse node = new RepositoryExplorerTreeResponse();
        node.setText(text);
        node.setPath(path);
        if(iconCls == null && !leaf)
            node.setIconCls("imageFolder");
        else
            node.setIconCls(iconCls);
        node.setLeaf(leaf);
        node.setExpanded(expanded);
        node.setType(type);
        return node;
    }
}
