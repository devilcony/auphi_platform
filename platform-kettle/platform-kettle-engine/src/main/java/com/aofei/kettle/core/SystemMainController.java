package com.aofei.kettle.core;

import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.PluginFactory;
import com.aofei.kettle.bean.Ext3Node;
import com.aofei.kettle.core.row.ValueMetaAndDataCodec;
import com.aofei.kettle.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.plugins.*;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.i18n.GlobalMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.checkdbconnection.JobEntryCheckDbConnections;
import org.pentaho.di.job.entries.delay.JobEntryDelay;
import org.pentaho.di.job.entries.deletefolders.JobEntryDeleteFolders;
import org.pentaho.di.job.entries.evaluatetablecontent.JobEntryEvalTableContent;
import org.pentaho.di.job.entries.ftpsget.FTPSConnection;
import org.pentaho.di.job.entries.sftp.SFTPClient;
import org.pentaho.di.job.entries.simpleeval.JobEntrySimpleEval;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.laf.BasePropertyHandler;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.StepPartitioningMeta;
import org.pentaho.di.trans.steps.denormaliser.DenormaliserTargetField;
import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;
import org.pentaho.di.trans.steps.exceloutput.ExcelOutputMeta;
import org.pentaho.di.trans.steps.excelwriter.ExcelWriterStepMeta;
import org.pentaho.di.trans.steps.multimerge.MultiMergeJoinMeta;
import org.pentaho.di.trans.steps.mysqlbulkloader.MySQLBulkLoaderMeta;
import org.pentaho.di.trans.steps.pgbulkloader.PGBulkLoaderMeta;
import org.pentaho.di.trans.steps.randomvalue.RandomValueMeta;
import org.pentaho.di.trans.steps.randomvalue.RandomValueMetaFunction;
import org.pentaho.di.trans.steps.setvariable.SetVariableMeta;
import org.pentaho.di.trans.steps.systemdata.SystemDataTypes;
import org.pentaho.di.trans.steps.textfileoutput.TextFileOutputMeta;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@RestController
@RequestMapping(value="/system")
@Api(tags = "Kettle字典接口api")
public class SystemMainController {

	@ApiOperation(value = "返回已实现的所有的转换步骤", httpMethod = "POST")
	@ResponseBody
	@RequestMapping("/steps2")
	protected void steps2() throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();

		PluginRegistry registry = PluginRegistry.getInstance();
		final List<PluginInterface> baseSteps = registry.getPlugins(StepPluginType.class);
		final List<String> baseCategories = registry.getCategories(StepPluginType.class);

		for (String baseCategory : baseCategories) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("text", baseCategory);
			JSONArray children = new JSONArray();

			List<PluginInterface> sortedCat = new ArrayList<PluginInterface>();
			for (PluginInterface baseStep : baseSteps) {
				if (baseStep.getCategory().equalsIgnoreCase(baseCategory)) {
					sortedCat.add(baseStep);
				}
			}
			Collections.sort(sortedCat, new Comparator<PluginInterface>() {
				public int compare(PluginInterface p1, PluginInterface p2) {
					return p1.getName().compareTo(p2.getName());
				}
			});
			for (PluginInterface p : sortedCat) {
				String pluginName = p.getName();
				String pluginDescription = p.getDescription();

				if(PluginFactory.containBean(p.getIds()[0])) {
					JSONObject child = new JSONObject();
					child.put("text", pluginName);
					child.put("pluginId", p.getIds()[0]);
					child.put("icon", SvgImageUrl.getMiddleUrl(p));
					child.put("dragIcon", SvgImageUrl.getUrl(p));
					child.put("qtip", pluginDescription);
					children.add(child);
				}
			}
			jsonObject.put("children", children);

			if(children.size() > 0)
				jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "返回已实现的所有的作业步骤", httpMethod = "POST")
	@ResponseBody
	@RequestMapping("/jobentrys2")
	protected void jobentrys2() throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();

		PluginRegistry registry = PluginRegistry.getInstance();
		final List<PluginInterface> baseJobEntries = registry.getPlugins(JobEntryPluginType.class);
		final List<String> baseCategories = registry.getCategories(JobEntryPluginType.class);

		for (String baseCategory : baseCategories) {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("text", baseCategory);
			JSONArray children = new JSONArray();

			List<PluginInterface> sortedCat = new ArrayList<PluginInterface>();
			if ( baseCategory.equalsIgnoreCase( JobEntryPluginType.GENERAL_CATEGORY ) ) {
				JobEntryCopy startEntry = JobMeta.createStartEntry();
				JSONObject child = new JSONObject();
				child.put("text", startEntry.getName());
				child.put("pluginId", startEntry.getEntry().getPluginId());
				child.put("icon", SvgImageUrl.getMiddleUrl(BasePropertyHandler.getProperty( "STR_image" )));
				child.put("dragIcon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "STR_image" )));
				child.put("qtip", startEntry.getDescription());
				children.add(child);

				JobEntryCopy dummyEntry = JobMeta.createDummyEntry();
				child = new JSONObject();
				child.put("text", dummyEntry.getName());
				child.put("pluginId", dummyEntry.getEntry().getPluginId());
				child.put("icon", SvgImageUrl.getMiddleUrl(BasePropertyHandler.getProperty( "DUM_image" )));
				child.put("dragIcon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "DUM_image" )));
				child.put("qtip", dummyEntry.getDescription());
				children.add(child);
		    }
			for (PluginInterface baseJobEntry : baseJobEntries) {
				if ( baseJobEntry.getIds()[ 0 ].equals( JobMeta.STRING_SPECIAL ) )
					continue;

				if (baseJobEntry.getCategory().equalsIgnoreCase(baseCategory)) {
					sortedCat.add(baseJobEntry);
				}
			}
			Collections.sort(sortedCat, new Comparator<PluginInterface>() {
				public int compare(PluginInterface p1, PluginInterface p2) {
					return p1.getName().compareTo(p2.getName());
				}
			});
			for (PluginInterface p : sortedCat) {
				String pluginName = p.getName();
				String pluginDescription = p.getDescription();

				if(PluginFactory.containBean(p.getIds()[0])) {
					JSONObject child = new JSONObject();
					child.put("text", PluginFactory.containBean(p.getIds()[0]) ? pluginName : "<font color='red'>" + pluginName + "</font>");
					child.put("pluginId", p.getIds()[0]);
					child.put("icon", SvgImageUrl.getMiddleUrl(p));
					child.put("dragIcon", SvgImageUrl.getUrl(p));
					child.put("qtip", pluginDescription);
					children.add(child);
				}
			}
			jsonObject.put("children", children);

			if(children.size() > 0)
				jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "原EXTJS版本获取转换所有的组件步骤", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/steps")
	protected void steps() throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();

		PluginRegistry registry = PluginRegistry.getInstance();
		final List<PluginInterface> baseSteps = registry.getPlugins(StepPluginType.class);
		final List<String> baseCategories = registry.getCategories(StepPluginType.class);

		int i=0;
		for (String baseCategory : baseCategories) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", "category" + i++);
			jsonObject.put("text", baseCategory);
			jsonObject.put("icon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "Folder_image" )));
			jsonObject.put("cls", "core-folder");
			JSONArray children = new JSONArray();

			List<PluginInterface> sortedCat = new ArrayList<PluginInterface>();
			for (PluginInterface baseStep : baseSteps) {
				if (baseStep.getCategory().equalsIgnoreCase(baseCategory)) {
					sortedCat.add(baseStep);
				}
			}
			Collections.sort(sortedCat, new Comparator<PluginInterface>() {
				public int compare(PluginInterface p1, PluginInterface p2) {
					return p1.getName().compareTo(p2.getName());
				}
			});
			for (PluginInterface p : sortedCat) {
				String pluginName = p.getName();
				String pluginDescription = p.getDescription();

				if(PluginFactory.containBean(p.getIds()[0])) {
					JSONObject child = new JSONObject();
					child.put("id", "step" + i++);
					child.put("text", PluginFactory.containBean(p.getIds()[0]) ? pluginName : "<font color='red'>" + pluginName + "</font>");
					child.put("pluginId", p.getIds()[0]);
					child.put("icon", SvgImageUrl.getUrl(p));
					child.put("dragIcon", SvgImageUrl.getUrl(p));
					child.put("cls", "core-leaf");
					child.put("qtip", pluginDescription);
					child.put("leaf", true);
					children.add(child);
				}
				// if ( !filterMatch( pluginName ) && !filterMatch(
				// 	pluginDescription ) ) {
				// continue;
				// }
			}
			jsonObject.put("children", children);

			if(children.size() > 0)
				jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "原EXTJS版本获取作业所有的环节", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/jobentrys")
	protected void jobentrys() throws ServletException, IOException {
		JSONArray jsonArray = new JSONArray();

		PluginRegistry registry = PluginRegistry.getInstance();
		final List<PluginInterface> baseJobEntries = registry.getPlugins(JobEntryPluginType.class);
		final List<String> baseCategories = registry.getCategories(JobEntryPluginType.class);

		int i=0;
		for (String baseCategory : baseCategories) {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", "category" + i++);
			jsonObject.put("text", baseCategory);
			jsonObject.put("icon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "Folder_image" )));
			jsonObject.put("cls", "core-folder");
			JSONArray children = new JSONArray();

			List<PluginInterface> sortedCat = new ArrayList<PluginInterface>();
			if ( baseCategory.equalsIgnoreCase( JobEntryPluginType.GENERAL_CATEGORY ) ) {
				JobEntryCopy startEntry = JobMeta.createStartEntry();
				JSONObject child = new JSONObject();
				child.put("id", startEntry.getEntry().getPluginId());
				child.put("text", startEntry.getName());
				child.put("pluginId", startEntry.getEntry().getPluginId());
				child.put("icon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "STR_image" )));
				child.put("dragIcon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "STR_image" )));
				child.put("cls", "core-leaf");
				child.put("qtip", startEntry.getDescription());
				child.put("leaf", true);
				children.add(child);

				JobEntryCopy dummyEntry = JobMeta.createDummyEntry();
				child = new JSONObject();
				child.put("id", "step" + i++);
				child.put("text", dummyEntry.getName());
				child.put("pluginId", dummyEntry.getEntry().getPluginId());
				child.put("icon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "DUM_image" )));
				child.put("dragIcon", SvgImageUrl.getUrl(BasePropertyHandler.getProperty( "DUM_image" )));
				child.put("cls", "core-leaf");
				child.put("qtip", dummyEntry.getDescription());
				child.put("leaf", true);
				children.add(child);
		    }
			for (PluginInterface baseJobEntry : baseJobEntries) {
				if ( baseJobEntry.getIds()[ 0 ].equals( JobMeta.STRING_SPECIAL ) )
					continue;

				if (baseJobEntry.getCategory().equalsIgnoreCase(baseCategory)) {
					sortedCat.add(baseJobEntry);
				}
			}
			Collections.sort(sortedCat, new Comparator<PluginInterface>() {
				public int compare(PluginInterface p1, PluginInterface p2) {
					return p1.getName().compareTo(p2.getName());
				}
			});
			for (PluginInterface p : sortedCat) {
				String pluginName = p.getName();
				String pluginDescription = p.getDescription();

				if(PluginFactory.containBean(p.getIds()[0])) {
					JSONObject child = new JSONObject();
					child.put("id", "step" + i++);
					child.put("text", PluginFactory.containBean(p.getIds()[0]) ? pluginName : "<font color='red'>" + pluginName + "</font>");
					child.put("pluginId", p.getIds()[0]);
					child.put("icon", SvgImageUrl.getUrl(p));
					child.put("dragIcon", SvgImageUrl.getUrl(p));
					child.put("cls", "core-leaf");
					child.put("qtip", pluginDescription);
					child.put("leaf", true);
					children.add(child);
				}
				// if ( !filterMatch( pluginName ) && !filterMatch(
				// 	pluginDescription ) ) {
				// continue;
				// }
			}
			jsonObject.put("children", children);

			if(children.size() > 0)
				jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取kettle支持的所有的系统参数", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/systemDataTypes")
	protected void systemDataTypes() throws IOException {
		JSONArray jsonArray = new JSONArray();

		SystemDataTypes[] values = SystemDataTypes.values();
		for (SystemDataTypes value : values) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", value.getCode());
			jsonObject.put("descrp", value.getDescription());
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取kettle支持的随机值", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/randomValueFunc")
	protected void randomValueFunc() throws IOException {
		JSONArray jsonArray = new JSONArray();

		RandomValueMetaFunction[] values = RandomValueMeta.functions;
		for (RandomValueMetaFunction value : values) {
			if(value == null) continue;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", value.getType());
			jsonObject.put("code", value.getCode());
			jsonObject.put("descrp", value.getDescription());
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取kettle支持的所有的数据类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/valueMeta")
	protected void valueMeta() throws IOException {
		JSONArray jsonArray = new JSONArray();

		PluginRegistry pluginRegistry = PluginRegistry.getInstance();
		List<PluginInterface> plugins = pluginRegistry.getPlugins(ValueMetaPluginType.class);
		for (PluginInterface plugin : plugins) {
			int id = Integer.valueOf(plugin.getIds()[0]);
			if (id > 0 && id != ValueMetaInterface.TYPE_SERIALIZABLE) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);
				jsonObject.put("name", plugin.getName());
				jsonArray.add(jsonObject);
			}
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取kettle支持的所有的数据格式化", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "valueType", value = "数据类型，获取所有请传all", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/valueFormat")
	protected void valueFormat(@RequestParam String valueType) throws IOException {
		JSONArray jsonArray = new JSONArray();
		if("all".equalsIgnoreCase(valueType)) {
			for(String format : Const.getConversionFormats()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", format);
				jsonArray.add(jsonObject);
			}
		} else {
			int type = ValueMeta.getType(valueType);
			if(type == ValueMetaInterface.TYPE_INTEGER || type == ValueMetaInterface.TYPE_NUMBER) {
				String[] fmt = Const.getNumberFormats();
				for (String str : fmt) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", ValueMeta.getType(str));
					jsonObject.put("name", str);
					jsonArray.add(jsonObject);
				}
			} else if(type == ValueMetaInterface.TYPE_DATE || type == ValueMetaInterface.TYPE_TIMESTAMP) {
				String[] fmt = Const.getDateFormats();
				for (String str : fmt) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", ValueMeta.getType(str));
					jsonObject.put("name", str);
					jsonArray.add(jsonObject);
				}
			}
		}
		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/valueString")
	protected void valueString(HttpServletRequest request, HttpServletResponse response, @RequestParam String valueMeta) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject(valueMeta);

		ValueMetaAndData valueMetaAndData = ValueMetaAndDataCodec.decode(jsonObject);
		String value = valueMetaAndData.toString();

		response.setContentType("text/html; charset=utf-8");
		response.getWriter().write(value);
	}

	@ApiOperation(value = "获取数据过滤组件支持的操作类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/func")
	protected void func() throws Exception {

		JSONArray jsonArray = new JSONArray();
		for(int i=0; i<Condition.functions.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", Condition.functions[i]);
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取Kettle支持的日期时间格式化", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/datetimeformat")
	protected void datetimeformat() throws Exception {
		JSONArray jsonArray = new JSONArray();
		String[] dats = Const.getDateFormats();
	    for ( int x = 0; x < dats.length; x++ ) {
	      JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", dats[x]);
			jsonArray.add(jsonObject);
	    }

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "文本文件输出支持的行终止符", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/formatMapperLineTerminator")
	protected void formatMapperLineTerminator() throws Exception {
		JSONArray jsonArray = new JSONArray();
		String[] dats = TextFileOutputMeta.formatMapperLineTerminator;
	    for ( int x = 0; x < dats.length; x++ ) {
	      JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", dats[x]);
			jsonArray.add(jsonObject);
	    }

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取Kettle支持的压缩格式", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/compressionProviderNames")
	protected void compressionProviderNames() throws Exception {
		JSONArray jsonArray = new JSONArray();
		String[] dats = CompressionProviderFactory.getInstance().getCompressionProviderNames();
	    for ( int x = 0; x < dats.length; x++ ) {
	      JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", dats[x]);
			jsonArray.add(jsonObject);
	    }

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取系统支持的字符集", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/availableCharsets")
	protected void availableCharsets() throws Exception {
		JSONArray jsonArray = new JSONArray();
		Collection<Charset> dats = Charset.availableCharsets().values();
	    for (Charset charset : dats) {
	      JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", charset.displayName());
			jsonArray.add(jsonObject);
	    }

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取支持的国际化", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/locale")
	protected void locale() throws Exception {
		JSONArray jsonArray = new JSONArray();
	    for (int i=0; i<GlobalMessages.localeCodes.length; i++) {
	      JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", GlobalMessages.localeCodes[i]);
			jsonObject.put("desc", GlobalMessages.localeDescr[i]);
			jsonArray.add(jsonObject);
	    }

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "FTPS支持的连接类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/connectiontype")
	protected void connectiontype() throws Exception {
		JSONArray jsonArray = new JSONArray();
		String [] connection_type_Descs = FTPSConnection.connection_type_Desc;
	    for (String charset : connection_type_Descs) {
	      JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", charset );
			jsonArray.add(jsonObject);
	    }
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "SFTP支持的代理类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/proxyType")
	protected void proxyType() throws Exception {
		JSONArray jsonArray = new JSONArray();
		for (String str : new String[] { SFTPClient.PROXY_TYPE_HTTP, SFTPClient.PROXY_TYPE_SOCKS5 }) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", str);
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "多合并支持的连接类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/multijointype")
	protected void multijointype() throws Exception {
		JSONArray jsonArray = new JSONArray();
		for (String str : MultiMergeJoinMeta.join_types) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", str);
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取时间单位", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/timeunit")
	protected void timeunit() throws Exception {
		JSONArray jsonArray = new JSONArray();
		for (int i=0; i<JobEntryCheckDbConnections.unitTimeCode.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", i);
			jsonObject.put("desc", JobEntryCheckDbConnections.unitTimeDesc[i]);
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取时间单位2", httpMethod = "POST")
	@RequestMapping(method=RequestMethod.POST, value="/timeunit2")
	protected @ResponseBody List timeunit2() throws Exception {
		ArrayList list = new ArrayList();

		LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
		record.put("code", "0");
		record.put("desc", BaseMessages.getString( JobEntryDelay.class, "JobEntryDelay.SScaleTime.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("code", "1");
		record.put("desc", BaseMessages.getString( JobEntryDelay.class, "JobEntryDelay.MnScaleTime.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("code", "2");
		record.put("desc", BaseMessages.getString( JobEntryDelay.class, "JobEntryDelay.HrScaleTime.Label" ));
		list.add(record);

		return list;
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/successCondition")
	protected void successCondition() throws Exception {
		JSONArray jsonArray = new JSONArray();
		String [] successConditionsCode = JobEntryEvalTableContent.successConditionsCode;
		for (int i=0; i<successConditionsCode.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", successConditionsCode[i]);
			jsonObject.put("text", JobEntryEvalTableContent.getSuccessConditionDesc(i));
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取系统支持的所有日志级别", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/logLevel")
	protected void logLevel() throws Exception {
		JSONArray jsonArray = new JSONArray();
	    for (LogLevel level : new LogLevel[]{LogLevel.NOTHING, LogLevel.ERROR, LogLevel.MINIMAL,
	    		LogLevel.BASIC, LogLevel.DETAILED, LogLevel.DEBUG, LogLevel.ROWLEVEL}) {

	    	JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", level.getLevel());
			jsonObject.put("code", level.getCode());
			jsonObject.put("desc", level.getDescription());
			jsonArray.add(jsonObject);
	    }

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取变量支持的所有作用域", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/variableType")
	protected void variableType() throws Exception {
		JSONArray jsonArray = new JSONArray();
	    for (int i=0; i<SetVariableMeta.getVariableTypeDescriptions().length; i++) {

	    	JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", i);
			jsonObject.put("code", SetVariableMeta.getVariableTypeCode(i));
			jsonObject.put("desc", SetVariableMeta.getVariableTypeDescription(i));
			jsonArray.add(jsonObject);
	    }

		JsonUtils.response(jsonArray);
	}


	@ApiOperation(value = "支持的分区方式", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/partitionMethod")
	protected void partitionMethod() throws Exception {
		JSONArray jsonArray = new JSONArray();
	    for (int i=0; i<StepPartitioningMeta.methodCodes.length; i++) {

	    	JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", StepPartitioningMeta.methodCodes[i]);
			jsonObject.put("desc", StepPartitioningMeta.methodDescriptions[i]);
			jsonArray.add(jsonObject);
	    }

	    PluginRegistry registry = PluginRegistry.getInstance();
        List<PluginInterface> plugins = registry.getPlugins( PartitionerPluginType.class );
        for ( PluginInterface plugin : plugins ) {
          	JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", plugin.getIds()[ 0 ]);
			jsonObject.put("desc", plugin.getDescription());
			jsonArray.add(jsonObject);
        }

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取支持的文件扩展名", httpMethod = "POST")
	@RequestMapping(method=RequestMethod.POST, value="/filextension")
	protected @ResponseBody List filextension(@RequestParam int extension) throws Exception {
		return FileNodeType.toList(extension);
	}

	@Authorization
	@ApiOperation(value = "浏览文件系统文件", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "path", value = "文件系统路径", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "extension", value = "扩展名集合", paramType="query", dataType = "string")
	})
	@RequestMapping(method=RequestMethod.POST, value="/fileexplorer")
	protected @ResponseBody List fileexplorer(@RequestParam String path, @RequestParam int extension, @ApiIgnore @CurrentUser CurrentUserResponse user) throws Exception {
		if(StringUtils.isEmpty(path)){
			path = com.aofei.base.common.Const.getUserDir(user.getOrganizerId());
		}


		LinkedList directorys = new LinkedList();
		LinkedList leafs = new LinkedList();
		if(StringUtils.hasText(path)) {
			File[] files = new File(path).listFiles();
			if(files != null) {
				for(File file : files) {
					if(file.isHidden())
						continue;
					if(file.isDirectory()) {
						Ext3Node node = Ext3Node.initNode(file.getAbsolutePath(), file.getName());
						node.setShowName(com.aofei.base.common.Const.getUserFilePath(user.getOrganizerId(),file.getAbsolutePath()));
						directorys.addLast(node);
					} else if(file.isFile() && FileNodeType.match(FileNodeType.getExtension(file.getName()), extension)){
						Ext3Node node = Ext3Node.initNode(file.getAbsolutePath(), file.getName(), true);
						node.setShowName(com.aofei.base.common.Const.getUserFilePath(user.getOrganizerId(),file.getAbsolutePath()));
						leafs.addLast(node);
					}
				}
			}
		} else {
			File[] files = File.listRoots();
			for(File file : files) {
				if(file.isDirectory()) {
					directorys.addLast(Ext3Node.initNode(file.getAbsolutePath(), file.getCanonicalPath()));
				} else if(file.isFile() && FileNodeType.match(FileNodeType.getExtension(file.getName()), extension)){
					Ext3Node node = Ext3Node.initNode(file.getAbsolutePath(), file.getCanonicalPath(), true);
					node.setShowName(com.aofei.base.common.Const.getUserFilePath(user.getOrganizerId(),file.getAbsolutePath()));
					leafs.addLast(node);
				}
			}
		}

		directorys.addAll(leafs);
		return directorys;
	}

	@RequestMapping(method=RequestMethod.POST, value="/deleteFoldersSuccessCondition")
	protected @ResponseBody List deleteFoldersSuccessCondition() throws Exception{
		ArrayList list = new ArrayList();

		LinkedCaseInsensitiveMap record = new LinkedCaseInsensitiveMap();
		record.put("code", "success_when_at_least");
		record.put("desc", BaseMessages.getString( JobEntryDeleteFolders.class, "JobDeleteFolders.SuccessWhenAtLeat.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("code", "success_if_errors_less");
		record.put("desc", BaseMessages.getString( JobEntryDeleteFolders.class, "JobDeleteFolders.SuccessWhenErrorsLessThan.Label" ));
		list.add(record);

		record = new LinkedCaseInsensitiveMap();
		record.put("code", "success_if_no_errors");
		record.put("desc", BaseMessages.getString( JobEntryDeleteFolders.class, "JobDeleteFolders.SuccessWhenAllWorksFine.Label" ));
		list.add(record);

		return list;
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/successConditionForSimp")
	protected void successConditionForSimp() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<JobEntrySimpleEval.successConditionCode.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", JobEntrySimpleEval.successConditionCode[i]);
			jsonObject.put("text", JobEntrySimpleEval.successConditionDesc[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/successNumberCondition")
	protected void successNumberCondition() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<JobEntrySimpleEval.successNumberConditionCode.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", JobEntrySimpleEval.successNumberConditionCode[i]);
			jsonObject.put("text", JobEntrySimpleEval.successNumberConditionDesc[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "EXCEL输出支持的字体", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/fontname")
	protected void fontname() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<ExcelOutputMeta.font_name_desc.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", i);
			jsonObject.put("text", ExcelOutputMeta.font_name_desc[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "EXCEL输出支持的字体朝向", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/fontoriention")
	protected void fontoriention() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<ExcelOutputMeta.font_orientation_desc.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", i);
			jsonObject.put("text", ExcelOutputMeta.font_orientation_desc[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "EXCEL输出支持的字体对齐方式", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/fontalignment")
	protected void fontalignment() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<ExcelOutputMeta.font_alignment_desc.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", i);
			jsonObject.put("text", ExcelOutputMeta.font_alignment_desc[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "EXCEL输出支持的字体颜色", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/fontcolor")
	protected void fontcolor() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<ExcelOutputMeta.font_color_desc.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", i);
			jsonObject.put("text", ExcelOutputMeta.font_color_desc[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "EXCEL输出支持的下划线类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/fontunderline")
	protected void fontunderline() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<ExcelOutputMeta.font_underline_desc.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", i);
			jsonObject.put("text", ExcelOutputMeta.font_underline_desc[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取EXCEL文档写入模式", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/excelwritemethod")
	protected void excelwritemethod() throws Exception{
		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("value", ExcelWriterStepMeta.IF_FILE_EXISTS_CREATE_NEW);
		jsonObject.put("text", "覆盖原工作表");
		jsonArray.add(jsonObject);

		jsonObject = new JSONObject();
		jsonObject.put("value", ExcelWriterStepMeta.IF_FILE_EXISTS_REUSE);
		jsonObject.put("text", "输出至已存在的工作表中");
		jsonArray.add(jsonObject);

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取EXCEL行写入模式", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/excelrowwritemethod")
	protected void excelrowwritemethod() throws Exception{
		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("value", ExcelWriterStepMeta.ROW_WRITE_OVERWRITE);
		jsonObject.put("text", "覆盖已存在的单元格");
		jsonArray.add(jsonObject);

		jsonObject = new JSONObject();
		jsonObject.put("value", ExcelWriterStepMeta.ROW_WRITE_PUSH_DOWN);
		jsonObject.put("text", "下移已有单元格");
		jsonArray.add(jsonObject);

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取系统支持的EXCEL的sheet页类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/excelSheetType")
	protected void excelSheetType() throws Exception{
		JSONArray jsonArray = new JSONArray();
		for(int i=0;i<SpreadSheetType.values().length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", SpreadSheetType.values()[i].toString());
			jsonObject.put("desc", SpreadSheetType.values()[i].getDescription());
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取系统支持的EXCEL类型", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/excelType")
	protected void excelType() throws Exception{
		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("value", "xls");
		jsonObject.put("text", "xls [Excel 97 and above]");
		jsonArray.add(jsonObject);

		jsonObject = new JSONObject();
		jsonObject.put("value", "xlsx");
		jsonObject.put("text", "xlsx [Excel 2007 and above]");
		jsonArray.add(jsonObject);

		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/gploadMethod")
	protected void gploadMethod() throws Exception{
		JSONArray jsonArray = new JSONArray();

		PluginInterface sp = PluginRegistry.getInstance().getPlugin(StepPluginType.class, "GPLoad");
		if(sp != null) {
			StepMetaInterface stepMetaInterface = PluginRegistry.getInstance().loadClass(sp, StepMetaInterface.class);

			String METHOD_AUTO_END = ReflectUtils.getFieldString(stepMetaInterface, "METHOD_AUTO_END");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", METHOD_AUTO_END);
			jsonObject.put("text", BaseMessages.getString(stepMetaInterface.getClass(), "GPLoadDialog.AutoEndLoadMethod.Label", new String[0]));
			jsonArray.add(jsonObject);


			String METHOD_MANUAL = ReflectUtils.getFieldString(stepMetaInterface, "METHOD_MANUAL");
			jsonObject = new JSONObject();
			jsonObject.put("value", METHOD_MANUAL);
			jsonObject.put("text", BaseMessages.getString(stepMetaInterface.getClass(), "GPLoadDialog.ManualLoadMethod.Label", new String[0]));
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/gploadAction")
	protected void gploadAction() throws Exception{
		JSONArray jsonArray = new JSONArray();

		PluginInterface sp = PluginRegistry.getInstance().getPlugin(StepPluginType.class, "GPLoad");
		if(sp != null) {
			StepMetaInterface stepMetaInterface = PluginRegistry.getInstance().loadClass(sp, StepMetaInterface.class);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", "insert");
			jsonObject.put("text", BaseMessages.getString(stepMetaInterface.getClass(), "GPLoadDialog.InsertLoadAction.Label", new String[0]));
			jsonArray.add(jsonObject);


			jsonObject = new JSONObject();
			jsonObject.put("value", "update");
			jsonObject.put("text", BaseMessages.getString(stepMetaInterface.getClass(), "GPLoadDialog.UpdateLoadAction.Label", new String[0]));
			jsonArray.add(jsonObject);

			jsonObject = new JSONObject();
			jsonObject.put("value", "merge");
			jsonObject.put("text", BaseMessages.getString(stepMetaInterface.getClass(), "GPLoadDialog.MergeLoadAction.Label", new String[0]));
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/gploadDateMask")
	protected void gploadDateMask() throws Exception{
		JSONArray jsonArray = new JSONArray();

		PluginInterface sp = PluginRegistry.getInstance().getPlugin(StepPluginType.class, "GPLoad");
		if(sp != null) {
			StepMetaInterface stepMetaInterface = PluginRegistry.getInstance().loadClass(sp, StepMetaInterface.class);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", "DATE");
			jsonObject.put("text", BaseMessages.getString(stepMetaInterface.getClass(), "GPLoadDialog.DateMask.Label", new String[0]));
			jsonArray.add(jsonObject);


			jsonObject = new JSONObject();
			jsonObject.put("value", "DATETIME");
			jsonObject.put("text", BaseMessages.getString(stepMetaInterface.getClass(), "GPLoadDialog.DateTimeMask.Label", new String[0]));
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/fieldFormatType")
	protected void fieldFormatType() throws Exception{
		JSONArray jsonArray = new JSONArray();

		for(int i=0;i<MySQLBulkLoaderMeta.getFieldFormatTypeDescriptions().length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", i);
			jsonObject.put("text", MySQLBulkLoaderMeta.getFieldFormatTypeDescriptions()[i]);
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}


	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/pgbulkDateMask")
	protected void pgbulkDateMask() throws Exception{
		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("value", PGBulkLoaderMeta.DATE_MASK_DATE);
		jsonObject.put("text", BaseMessages.getString(PGBulkLoaderMeta.class, "PGBulkLoaderDialog.DateMask.Label", new String[0]));
		jsonArray.add(jsonObject);


		jsonObject = new JSONObject();
		jsonObject.put("value", PGBulkLoaderMeta.DATE_MASK_DATETIME);
		jsonObject.put("text", BaseMessages.getString(PGBulkLoaderMeta.class, "PGBulkLoaderDialog.DateTimeMask.Label", new String[0]));
		jsonArray.add(jsonObject);

		jsonObject = new JSONObject();
		jsonObject.put("value", PGBulkLoaderMeta.DATE_MASK_PASS_THROUGH);
		jsonObject.put("text", BaseMessages.getString(PGBulkLoaderMeta.class, "PGBulkLoaderDialog.PassThrough.Label", new String[0]));
		jsonArray.add(jsonObject);

		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/pgbulkLoadAction")
	protected void pgbulkLoadAction() throws Exception{
		JSONArray jsonArray = new JSONArray();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("value", PGBulkLoaderMeta.ACTION_INSERT);
		jsonObject.put("text", BaseMessages.getString(PGBulkLoaderMeta.class, "PGBulkLoaderDialog.InsertLoadAction.Label", new String[0]));
		jsonArray.add(jsonObject);


		jsonObject = new JSONObject();
		jsonObject.put("value", PGBulkLoaderMeta.ACTION_TRUNCATE);
		jsonObject.put("text", BaseMessages.getString(PGBulkLoaderMeta.class, "PGBulkLoaderDialog.TruncateLoadAction.Label", new String[0]));
		jsonArray.add(jsonObject);

		JsonUtils.response(jsonArray);
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/denormaliserAggr")
	protected void denormaliserAggr() throws Exception{
		JSONArray jsonArray = new JSONArray();

		for(int i=0;i<DenormaliserTargetField.typeAggrLongDesc.length;i++){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", i);
			jsonObject.put("text", DenormaliserTargetField.typeAggrLongDesc[i]);
			jsonArray.add(jsonObject);
		}

		JsonUtils.response(jsonArray);
	}
}
