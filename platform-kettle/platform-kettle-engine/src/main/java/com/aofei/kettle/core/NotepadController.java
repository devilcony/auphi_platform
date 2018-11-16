package com.aofei.kettle.core;

import com.aofei.kettle.utils.JsonUtils;
import com.mxgraph.util.mxUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.xml.XMLHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@RestController
@RequestMapping(value="/notepad")
@Api(tags = "备注接口api")
public class NotepadController {

	@ResponseBody
	@RequestMapping("/newNote")
	@ApiOperation(value = "新建备注", httpMethod = "POST")
	protected void newNote() throws Exception {

		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.NOTEPAD);

		NotePadMeta npm = new NotePadMeta();
		String style = NotePadCodec.encodeStyle(npm);
		e.setAttribute("__style__", style);
		JsonUtils.responseXml(XMLHandler.getXMLHeader() + mxUtils.getXml(e));


	}
}
