package com.aofei.base.model.vo;

import com.aofei.base.model.response.IgnoreResponse;

import java.util.List;

/**
 * Easyui Tree的数据模型封装. <br/>
 */
public class TreeNode extends IgnoreResponse {

    private Long id;
    private Long pid;
    private String text;
    private String state = "open";//'open','closed'
    private Boolean checked = false;
    private List<TreeNode> children;

    public TreeNode() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}
