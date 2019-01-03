package com.aofei.kettle.repository.beans;

import org.pentaho.di.repository.RepositoryDirectoryInterface;

public class DirectoryVO {

	private String id;
	private String name;
	private String path;
	private String userPath;
	private String type = "dir";

	public DirectoryVO() {

	}

	public DirectoryVO(RepositoryDirectoryInterface rdi) {
		name = rdi.getName();
		path = rdi.getParent().getPath();
		id = rdi.getObjectId().getId();
	}

	public DirectoryVO(RepositoryDirectoryInterface rdi,String replace) {
		this(rdi);
		userPath = getPath().replace(replace,"/");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserPath() {
		return userPath;
	}

	public void setUserPath(String userPath) {
		this.userPath = userPath;
	}
}
