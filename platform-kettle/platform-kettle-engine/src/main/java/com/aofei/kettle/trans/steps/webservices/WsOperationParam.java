package com.aofei.kettle.trans.steps.webservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WsOperationParam {

	private String title;

	private List params = new ArrayList();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List getParams() {
		return params;
	}

	public void addParam(String name, String type) {
		HashMap rec = new HashMap();
		rec.put("wsName", name);
		rec.put("xsdType", type);
		params.add(rec);
	}
}
