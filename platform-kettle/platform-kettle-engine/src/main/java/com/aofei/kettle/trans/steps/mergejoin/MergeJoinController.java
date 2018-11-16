package com.aofei.kettle.trans.steps.mergejoin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.trans.steps.mergejoin.MergeJoinMeta;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/mergejoin")
@Api(tags = "Transformation转换 - 合并连接 - 接口api")
public class MergeJoinController {

	@ApiOperation(value = "获取合并连接类型，左连接、右连接", httpMethod = "POST")
	@RequestMapping(method=RequestMethod.POST, value="/types")
	protected @ResponseBody List types() throws Exception{
		ArrayList list = new ArrayList();
		for(int i=0;i<MergeJoinMeta.join_types.length;i++){
			LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
			record.put("name", MergeJoinMeta.join_types[i]);
			list.add(record);
		}
		return list;
	}


}
