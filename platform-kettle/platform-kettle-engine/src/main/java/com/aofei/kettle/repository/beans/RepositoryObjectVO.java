package com.aofei.kettle.repository.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.pentaho.di.repository.RepositoryElementMetaInterface;

import java.util.Date;

public class RepositoryObjectVO {

	private String id;
	private String name;
	private String type;
	private String path;
	private String modifiedUser;
	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	private Date modifiedDate;

	public RepositoryObjectVO() {

	}

	public RepositoryObjectVO(RepositoryElementMetaInterface re) {
		id = re.getObjectId().getId();
		name = re.getName();
		type = re.getObjectType().getExtension();
		path = re.getRepositoryDirectory().getPath();
	  	modifiedUser = re.getModifiedUser();
	  	modifiedDate = re.getModifiedDate();
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getModifiedUser() {
		return modifiedUser;
	}

	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
