package com.aofei.kettle.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.util.LinkedCaseInsensitiveMap;

public enum FileNodeType {

	ALL(1, "所有文件", "*"), KJB(2, "作业文件", "kjb"), KTR(4, "转换文件", "ktr"), TXT(8, "文本文件", "txt"), CSV(16, "CSV文件", "csv"),
		XLS(32, "Excel97~2003", "xls"), XLSX(64, "Excel", "xlsx"), ODS(128, "OpenDocument电子表格", "ods"), PNG(256, "PNG图片", "png");

	private int type;
	private String typeDesc;
	private String extension;

	private FileNodeType(int type, String typeDesc, String extension) {
		this.type = type;
		this.typeDesc = typeDesc;
		this.extension = extension;
	}

	public static boolean includeNone(int type) {
		return type == 0;
	}

	public static boolean match(String extension, int type) throws IllegalArgumentException, IllegalAccessException {
		if((FileNodeType.ALL.type & type) > 0)
			return true;

		HashSet<String> subfix = new HashSet<String>();
		for(FileNodeType fnt : FileNodeType.values()) {
			if((fnt.type & type) != 0)
				subfix.add(fnt.extension);
		}

		return subfix.contains(extension);
//		for(Field field : FileNodeType.class.getDeclaredFields()) {
//			if(field.getType().equals(FileNodeType.class)) {
//				FileNodeType fnt = (FileNodeType) field.get(null);
//
//				if((fnt.type & type) == 0)
//					continue;
//
//
//				return fnt.extension.equalsIgnoreCase(extension);
//			}
//		}
//
//		return false;
	}

	public static String getExtension(String fileName) {
		if(fileName == null || fileName.indexOf(".") < 0)
			return null;
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public static List toList(int type) throws IllegalArgumentException, IllegalAccessException {
		ArrayList list = new ArrayList();
		for(Field field : FileNodeType.class.getDeclaredFields()) {
			if(field.getType().equals(FileNodeType.class)) {
				FileNodeType fnt = (FileNodeType) field.get(null);

				if((fnt.type & type) > 0 || fnt.type == 1) {
					LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
					record.put("type", fnt.type);
					record.put("desc", fnt.typeDesc);
					list.add(record);
				}
			}
		}
		return list;
	}
}
