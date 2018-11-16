package com.aofei.kettle.trans.steps.checksum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.trans.steps.checksum.CheckSumMeta;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/checksum")
@Api(tags = "Transformation转换 - 添加校验列 - 接口api")
public class CheckSumController {

	@ApiOperation(value = "获取支持的校验算法", httpMethod = "POST")
	@RequestMapping(method=RequestMethod.POST, value="/types")
	protected @ResponseBody List types() throws IOException {
		ArrayList list = new ArrayList();

		for (String code : CheckSumMeta.checksumtypeCodes) {
			LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
			record.put("code", code);
			list.add(record);
		}

		return list;
	}

	@ApiOperation(value = "校验结果类型", httpMethod = "POST")
	@RequestMapping(method=RequestMethod.POST, value="/resulttype")
	protected @ResponseBody List resulttype() throws IOException {
		ArrayList list = new ArrayList();

		for (int i=0; i<CheckSumMeta.resultTypeCode.length; i++) {
			LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
			record.put("code", CheckSumMeta.resultTypeCode[i]);
			record.put("desc", CheckSumMeta.resultTypeCode[i]);
			list.add(record);
		}

		return list;
	}

}
