package com.aofei.kettle.trans.steps.mail;

import com.aofei.kettle.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.steps.mail.MailMeta;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(value="/mail")
@Api(tags = "Transformation转换 - 发送邮件 - 接口api")
public class MailController {

	@ApiOperation(value = "加载邮件消息优先级", httpMethod = "POST")
	@RequestMapping("/priorityAndImportance")
	protected @ResponseBody List priorityAndImportance() throws Exception{
		ArrayList list = new ArrayList();
		Class PKG = MailMeta.class;

		LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
		record.put("value", "low");
		record.put("text", BaseMessages.getString( PKG, "Mail.Priority.Low.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("value", "normal");
		record.put("text", BaseMessages.getString( PKG, "Mail.Priority.Normal.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("value", "high");
		record.put("text", BaseMessages.getString( PKG, "Mail.Priority.High.Label" ));
		list.add(record);

		return list;
	}

	@ApiOperation(value = "加载邮件敏感级别", httpMethod = "POST")
	@RequestMapping("/sensitivities")
	protected @ResponseBody List sensitivities() throws Exception{
		ArrayList list = new ArrayList();
		Class PKG = MailMeta.class;

		LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
		record.put("value", "personal");
		record.put("text", BaseMessages.getString( PKG, "Mail.Sensitivity.personal.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("value", "private");
		record.put("text", BaseMessages.getString( PKG, "Mail.Sensitivity.private.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("value", "company-confidential");
		record.put("text", BaseMessages.getString( PKG, "Mail.Sensitivity.confidential.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("value", "normal");
		record.put("text", BaseMessages.getString( PKG, "Mail.Sensitivity.normal.Label" ));
		list.add(record);

		return list;
	}

	@ApiOperation(value = "内嵌图片自动生成的内容ID", httpMethod = "POST")
	@RequestMapping("/contentId")
	protected @ResponseBody void contentId() throws Exception {
		Random randomgen = new Random();
		String contentId = Long.toString(Math.abs(randomgen.nextLong()), 32);
		JsonUtils.success(contentId);
	}

}
