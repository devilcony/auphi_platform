package com.aofei.kettle.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class JsonUtils {

	public static void success(String message) throws IOException {
		success("系统提示", message);
	}

	public static void success(String title, String message) throws IOException {
		response(true, title, message);
	}

	public static void fail(String message) throws IOException {
		fail("系统提示", message);
	}

	public static void fail(String title, String message) throws IOException {
		response(false, title, message);
	}

	public static void response(boolean success, String title, String message) throws IOException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", success);
		jsonObject.put("title", title);
		jsonObject.put("message", message);

		response(jsonObject);
	}

	public static void response(JSONObject jsonObject) throws IOException {
		HttpServletResponse response = response();
		response.setContentType("text/html; charset=utf-8");
		response.getWriter().write(jsonObject.toString());
	}

	public static void response(JSONArray jsonArray) throws IOException {
		HttpServletResponse response = response();
		response.setContentType("text/html; charset=utf-8");
		response.getWriter().write(jsonArray.toString());
	}

	public static void responseXml(String xml) throws IOException {
		HttpServletResponse response = response();
		response.setContentType("text/xml; charset=utf-8");
		response.getWriter().write(xml);
	}

	public static void download(File file) throws IOException {
		HttpServletResponse response = response();
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName=" + file.getName());

		InputStream is = null;
		try {
			is = FileUtils.openInputStream(file);

			IOUtils.copy(is, response.getOutputStream());
		} finally {
			IOUtils.closeQuietly(is);
		}

	}

	private static final String request = "request";
	private static final String response = "response";

	private static ThreadLocal<Map> tl = new ThreadLocal<Map>();

	static Map get() {
		Map map = tl.get();
		if(map == null) {
			map = new HashMap();
			tl.set(map);
		}
		return map;
	}

	public static void put(HttpServletRequest r) {
		get().put(request, r);
	}

	public static void put(HttpServletResponse r) {
		get().put(response, r);
	}

	public static HttpServletResponse response() {
		HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		return resp;
	}

	public static HttpServletRequest request() {
		return (HttpServletRequest) tl.get().get(request);
	}

	public static HttpSession session() {
		return request().getSession();
	}
}
