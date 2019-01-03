package com.aofei.kettle.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NodeUtils {

	public static Map createLeaf(int id, String text) {
		return createLeaf(String.valueOf(id), text);
	}

	public static Map createLeaf(String id, String text) {
		return createLeaf(id, text, null);
	}

	public static Map createLeaf(int id, String text, String iconCls) {
		return createLeaf(String.valueOf(id), text, iconCls);
	}

	public static Map createLeaf(String id, String text, String iconCls) {
		HashMap node = new HashMap();
		node.put("id", id);
		node.put("text",  text);
		node.put("cls", "node-24");
		node.put("leaf", true);

		if(iconCls != null) {
			node.put("iconCls", iconCls);
		}

		return node;
	}

	public static Map createFolder(String id, String text) {
		return createFolder(id, text, false);
	}

	public static Map createFolder(int id, String text) {
		return createFolder(String.valueOf(id), text);
	}

	public static Map createFolder(String id, String text, boolean expanded) {
		return createFolder(id, text, "imageFolder", expanded);
	}

	public static Map createFolder(int id, String text, boolean expanded) {
		return createFolder(String.valueOf(id), text, expanded);
	}

	public static Map createFolder(String id, String text, String iconCls) {
		return createFolder(id, text, iconCls, false);
	}

	public static Map createFolder(int id, String text, String iconCls) {
		return createFolder(String.valueOf(id), text, iconCls, false);
	}

	public static Map createFolder(String id, String text, String iconCls,boolean expanded) {
		HashMap node = new HashMap();
		node.put("id", id);
		node.put("text",  text);
		node.put("cls", "node-24");
		node.put("iconCls", iconCls);
		node.put("expanded", expanded);
		node.put("children", new ArrayList());

		return node;
	}

}
