/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0 

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.auphi.ktrl.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XmlUtil implements java.io.Serializable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(XmlUtil.class);

	// 读取文本文件
	public static String load(String path){
		String s = "";
		FileInputStream fis = null;
		try{
			File f = new File(path);
			fis = new FileInputStream(f);
			f.length();
			byte[] b = new byte[fis.available()];
			fis.read(b);
			s = new String(b,"UTF-8");
			fis.close();
		}catch(Exception e){
			try{
			if(fis!=null)
				fis.close();
			}catch(Exception ex){
				
			}
			System.out.println("Error reading XML: " + e.toString());
		}
		
		return s;
	}
	
	// 封装 XML 格式的 xmlHttp 请求
	public static String load(HttpServletRequest request) {
		StringBuffer xml = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				String s = new String(line.getBytes("ISO8859_1"), "UTF-8");
				xml.append(s);
			}
		} catch (Exception e) {
			System.out.println("Error reading XML: " + e.toString());
		}
		return xml.toString();
	}

	// 转换xml字符串为xml对象
	public static Document toXmlDocument(String xml) {
		Document xmlDoc = null;
		try {
			xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return xmlDoc;
	}
	// 封装 XML 格式的 xmlHttp 请求
	public static String readXmlFromRequestBody(HttpServletRequest request) {
		StringBuffer xml = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				String s = new String(line.getBytes("ISO8859_1"), "UTF-8");
				xml.append(s+"\n");
			}
		} catch (Exception e) {
			System.out.println("Error reading XML: " + e.toString());
		}
		return xml.toString();
	}
	
	public static String readXmlFromUrl(String url) throws Exception {
		Document doc = getDocumentFromUri(url);
		String xml = XmlUtil.toString(doc);
		return xml;
	}

	// 转换xml字符串为xml对象
	public static Document getXmlDocument(String xml) {
		Document xmlDoc = null;
		try {
			xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return xmlDoc;
	}

	public static DocumentBuilder getBuilder()
			throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		return builder;
	}

	// get a Document from given File
	public static Document getDocument(String path) throws Exception {
		// bufferedreader filein=new bufferedreader(new filereader(path));
		File f = new File(path);
		DocumentBuilder builder = getBuilder();
		Document doc = builder.parse(f);
		return doc;
	}

	// get a Document from InputStream
	public static Document getDocument(InputStream in) throws Exception {
		DocumentBuilder builder = getBuilder();
		Document doc = builder.parse(in);
		return doc;
	}
	
	public static Document getDocumentFromUri(String uri) throws Exception{
		DocumentBuilder builder = getBuilder();
		Document doc = builder.parse(uri);
		return doc;
	}

	// create a empty Document
	public static Document getNewDoc() throws Exception {
		DocumentBuilder builder = getBuilder();
		Document doc = builder.newDocument();
		return doc;
	}

	// create a Document from given String
	public static Document getNewDoc(String xmlstr) {
		Document doc = null;
		try {
			StringReader sr = new StringReader(xmlstr);
			InputSource isrc = new InputSource(sr);
			DocumentBuilder builder = getBuilder();
			doc = builder.parse(isrc);
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
		}
		return doc;
	}

	// save a Document as a File at the given File path
	public static void save(Document doc, String filepath) {
		try {
			OutputFormat format = new OutputFormat(doc,"UTF-8",false); // serialize dom
			//format.setEncoding("UTF-8");
			StringWriter stringout = new StringWriter(); // writer will be a
															// String
			XMLSerializer serial = new XMLSerializer(stringout, format);
			serial.asDOMSerializer(); // as a dom serializer
			serial.serialize(doc.getDocumentElement());
			String strxml = stringout.toString(); // spit out dom as a String
			String path = filepath;
			writeXml(strxml, path);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	// save a String(xml) in the given File path
	public static void writeXml(String strxml, String path) {
		try {
			File f = new File(path);
			Writer writer = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(f), "UTF-8")));
			writer.write(strxml);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	public static void printXml(String strxml, javax.servlet.jsp.JspWriter out) {
		if (!strxml.startsWith("<?xml")) {
			strxml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + strxml;
		}
		try {
			out.print(strxml);
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
		}
	}

	// format a Document to String
	public static String toString(Document doc) {
		String strxml = null;
		try {
			OutputFormat format = new OutputFormat(doc); // serialize dom
			format.setEncoding("UTF-8");
			StringWriter stringout = new StringWriter(); // writer will be a
															// String
			XMLSerializer serial = new XMLSerializer(stringout, format);
			serial.asDOMSerializer(); // as a dom serializer
			serial.serialize(doc.getDocumentElement());
			strxml = stringout.toString(); // spit out dom as a String
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return strxml;
	}

	// format a node to String
	public static String toString(Node node, Document doc) {
		String strxml = null;
		try {
			OutputFormat format = new OutputFormat(doc); // serialize dom
			format.setEncoding("UTF-8");
			StringWriter stringout = new StringWriter(); // writer will be a
															// String
			XMLSerializer serial = new XMLSerializer(stringout, format);
			serial.asDOMSerializer(); // as a dom serializer
			serial.serialize((Element) node);
			strxml = stringout.toString(); // spit out dom as a String
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return strxml;
	}
	
	
	public static String toJSON(String xmlstr) {
		return toJSON(xmlstr,null);
	}
	public static String toJSON(String xmlstr,String step) {
		if (!xmlstr.startsWith("<?xml")) {
			xmlstr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + xmlstr;
		}
		
		StringBuilder json = new StringBuilder();

		try {
			Document doc = XmlUtil.getNewDoc(xmlstr);
			Node node = doc.getFirstChild();
			json.append("{");
			addJsonAttribute(json, node, 0, step);
			json.append("}");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			logger.error(ex.getMessage(),ex);
		}
		return json.toString();
	}

	private static void addJsonAttribute(StringBuilder json, Node node,
			int level,String step) {
		
		json.append("\"");
		json.append(node.getNodeName());
		json.append("\":");
		addJsonBody(json, node, level, step);
	}

	private static void addJsonBody(StringBuilder json, Node node, int level,String step) {
		json.append("{");
		NodeList txtnl = node.getChildNodes();
		String content = null;

		if (txtnl == node) {
			if (txtnl.getLength() == 1) {
				
				if(node.getFirstChild().getNodeType() == Node.ELEMENT_NODE){
					addStep(json,level+1,step);
					addJsonAttribute(json,node.getFirstChild(),level+1,step);
					addStep(json,level,step);
					json.append("}");
					return;
				}else{
					content = node.getChildNodes().item(0).getNodeValue().replace("\\", "\\\\")
					.replace("\r", "\\r").replace("\n", "\\n")
					.replace("\t", "\\t").replace("\"", "\\\"");
				}
			} else {
				for (int i = 0; i < txtnl.getLength(); i++) {
					Node n = txtnl.item(i);
					if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
						content = n.getNodeValue().replace("\\", "\\\\")
								.replace("\r", "\\r").replace("\n", "\\n")
								.replace("\t", "\\t").replace("\"", "\\\"");
					}
				}
			}
		}
		
		NamedNodeMap attributes = node.getAttributes();
		int t = 0;
		if (attributes.getLength() > 0) {
			//json.append("{");
			for (int i = 0; i < attributes.getLength(); i++) {
				if (t > 0)
					json.append(",");
				addStep(json,level+1,step);
				
				Node att = attributes.item(i);
				json.append("\""+att.getNodeName()+"\":\"");
				
				json.append(att.getNodeValue());
				json.append("\"");
				t++;
			}
		}
		if (content != null) {
			if (t > 0)
				json.append(",");
			addStep(json,level+1,step);
			json.append("\"content\":\"");
			json.append(content);
			json.append("\"");
			//addStep(json,level,step);
		}
		addJsonChilds(json, node, level, t > 0, step);
		addStep(json,level,step);
		json.append("}");
	}

	private static void addJsonChilds(StringBuilder json, Node node, int level,
			boolean hasAttributes, String step) {

		NodeList nl = node.getChildNodes();
		int t = 0;
		level++;

		Hashtable<String, Node> ht = new Hashtable<String, Node>();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof Element) {
				// System.out.println("####"+n.getNodeName());

				if (isArray(n)) {
					if (ht.get(n.getNodeName()) == null) {
						ht.put(n.getNodeName(), n);
						addArray(json, n, level, hasAttributes, step);
					}
				} else {
					if (t == 0 && hasAttributes)
						json.append(",");
					if (t > 0)
						json.append(",");
					addStep(json,level,step);
					addJsonAttribute(json, n, level, step);
					t++;
				}
			}
		}
	}

	private static void addArray(StringBuilder json, Node node, int level,
			boolean hasAttributes, String step) {
		Node parent = node.getParentNode();
		
		if (hasAttributes)
			json.append(",");
		addStep(json,level,step);
		
		json.append("\""+node.getNodeName() + "\":[");
		level++;
		
		NodeList nl = parent.getChildNodes();
		int c=0;
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeName().equals(node.getNodeName())){
				if (c > 0)
					json.append(",");
				addStep(json,level,step);
				addJsonBody(json, n, level, step);
				c++;
			}
		}
		level--;
		addStep(json,level,step);
		json.append("]");
	}

	private static boolean isArray(Node node) {
		Node parent = node.getParentNode();
		if (parent == null)
			return false;
		
		NodeList nl = parent.getChildNodes();
		int c = 0;
		for(int i=0;i<nl.getLength();i++){
			if(nl.item(i).getNodeName().equals(node.getNodeName()))
				c++;
		}
		
		return c>1;
	}
	
	private static void addStep(StringBuilder json,int level,String step){
		if(step==null)return;
		json.append("\n");
		for (int j = 0; j < level; j++) {
			json.append("    ");
		}
	}

	public static void main(String[] args) throws Exception {
		String pathroot = "netrees.xml";
		Document doc, doc1;
		try {
			doc = XmlUtil.getDocument(pathroot);
			doc1 = XmlUtil.getDocument(pathroot);
			if (doc == doc1) {
				System.out.println("they are  same objects!");
			} else {
				System.out.println("they are different!");
				OutputFormat format = new OutputFormat(doc); // serialize dom
				format.setEncoding("UTF-8");
				StringWriter stringout = new StringWriter(); // writer will be a
																// String
				XMLSerializer serial = new XMLSerializer(stringout, format);
				serial.asDOMSerializer(); // as a dom serializer
				serial.serialize(doc.getDocumentElement());
				String strxml = stringout.toString(); // spit out dom as a
														// String
				System.out.println(strxml);
			}
		} catch (Exception ex) {
			System.out.print("reading File\"" + pathroot + "\" error!");
			logger.error(ex.getMessage(),ex);
		}
	}

}
