package com.aofei.mdm.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.mdm.model.request.ModelAttributeRequest;
import com.aofei.mdm.model.request.ModelRequest;
import com.aofei.mdm.model.response.ModelAttributeResponse;
import com.aofei.mdm.model.response.ModelResponse;
import com.aofei.mdm.service.IModelAttributeService;
import com.aofei.mdm.service.IModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags = { "主数据管理-数据模型属性" })
@RestController
@RequestMapping("/mdm/attribute")
public class ModelAttributeController extends BaseController {

    @Autowired
    IModelAttributeService modelAttributeService;

    /**
     * 数据模型列表
     * @param request
     * @return
     */
    @ApiOperation(value = "所有数据模型属性列表", notes = "所有数据模型属性列表", httpMethod = "GET")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<ModelAttributeResponse>> list(@ApiIgnore ModelAttributeRequest request)  {
        List<ModelAttributeResponse> list = modelAttributeService.getModelAttributes(request);
        return Response.ok(list) ;
    }
}
