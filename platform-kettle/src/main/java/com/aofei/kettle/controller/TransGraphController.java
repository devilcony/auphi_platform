package com.aofei.kettle.controller;

import com.alibaba.fastjson.JSONObject;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.kettle.core.PluginFactory;
import com.aofei.kettle.core.base.GraphCodec;
import com.aofei.kettle.core.database.DatabaseCodec;
import io.swagger.annotations.Api;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.trans.TransMeta;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auther Tony
 * @create 2018-10-10 22:22
 */
@Api(tags = { "WEB设计器-转换" })
@RestController
@RequestMapping(value = "/kettle/trans", produces = {"application/json;charset=UTF-8"})
public class TransGraphController extends BaseController {

    /**
     * 获取转换的xml文件
     * @param request
     * @param response
     * @param graphXml
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(method= RequestMethod.POST, value="/engineXml")
    protected void engineXml(HttpServletRequest request, HttpServletResponse response, @RequestParam String graphXml) throws Exception {
        GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
        AbstractMeta transMeta = codec.decode(graphXml);
        String xml = XMLHandler.getXMLHeader() + transMeta.getXML();

        response.setContentType("text/html; charset=utf-8");
        response.getWriter().write(xml);
    }

    /**
     * 获取转换数据库连接信息
     * @param graphXml 转换的xml信息
     * @param name 数据库连名称
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(method=RequestMethod.POST, value="/database")
    protected Response<JSONObject> database(@RequestParam String graphXml, String name) throws Exception {
        GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
        TransMeta transMeta = (TransMeta) codec.decode(graphXml);

        DatabaseMeta databaseMeta = transMeta.findDatabase(name);
        if(databaseMeta == null)
            databaseMeta = new DatabaseMeta();

        JSONObject jsonObject = DatabaseCodec.encode(databaseMeta);

        return Response.ok(jsonObject);
    }
}
