package com.aofei.kettle.trans.steps.fileinput;

import com.aofei.kettle.PluginFactory;
import com.aofei.kettle.base.GraphCodec;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.compress.CompressionInputStream;
import org.pentaho.di.core.compress.CompressionProvider;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.fileinput.text.EncodingType;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputUtils;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/fileinput")
@Api(tags = "Transformation转换 - 文本文件 - 接口api")
public class TextFileInputController {

	@ApiOperation(value = "获取所有国际化名称", httpMethod = "POST")
	@RequestMapping(method = RequestMethod.POST, value = "/locale")
	protected @ResponseBody List locale() throws Exception {
		ArrayList list = new ArrayList();
		Locale[] locale = Locale.getAvailableLocales();
		for (int i = 0; i < locale.length; i++) {

			LinkedHashMap rec = new LinkedHashMap();
			rec.put("value", locale[i].toString());
			rec.put("text", locale[i].toString());
			list.add(rec);
		}

		return list;
	}

	@ApiOperation(value = "获取字段信息", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "环节名称", paramType="query", dataType = "string")
	})
	@RequestMapping(method=RequestMethod.POST, value="/fields")
	protected @ResponseBody List fields(@RequestParam String graphXml, @RequestParam String stepName) throws Exception {
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		StepMeta stepMeta = transMeta.findStep(stepName);
		TextFileInputMeta meta = (TextFileInputMeta) stepMeta.getStepMetaInterface();

		ArrayList list = new ArrayList();
		LogChannel log = new LogChannel( meta );
		if ("CSV".equalsIgnoreCase(meta.content.fileType)) {

			TextFileInputMeta previousMeta = (TextFileInputMeta) meta.clone();
		    FileInputList textFileList = meta.getFileInputList( transMeta );
		    InputStream fileInputStream;
		    CompressionInputStream inputStream = null;
		    StringBuilder lineStringBuilder = new StringBuilder( 256 );
		    int fileFormatType = meta.getFileFormatTypeNr();

		    String delimiter = transMeta.environmentSubstitute( meta.content.separator );
		    String enclosure = transMeta.environmentSubstitute( meta.content.enclosure );
		    String escapeCharacter = transMeta.environmentSubstitute( meta.content.escapeCharacter );

			if (textFileList.nrOfFiles() > 0) {
				FileObject fileObject = textFileList.getFile(0);
				fileInputStream = KettleVFS.getInputStream(fileObject);

				CompressionProvider provider = CompressionProviderFactory.getInstance()
						.createCompressionProviderInstance(meta.content.fileCompression);
				inputStream = provider.createInputStream(fileInputStream);

				InputStreamReader reader;
				if (meta.getEncoding() != null && meta.getEncoding().length() > 0) {
					reader = new InputStreamReader(inputStream, meta.getEncoding());
				} else {
					reader = new InputStreamReader(inputStream);
				}

				EncodingType encodingType = EncodingType.guessEncodingType(reader.getEncoding());

				String line = TextFileInputUtils.getLine(log, reader, encodingType, fileFormatType, lineStringBuilder);
				if (line != null) {
					String[] fields = TextFileInputUtils.guessStringsFromLine(transMeta, log, line, meta, delimiter, enclosure, escapeCharacter);
					for (int i = 0; i < fields.length; i++) {
						String field = fields[i];
						if (field == null || field.length() == 0 || !meta.content.header) {
							field = "Field" + (i + 1);
						} else {
							field = Const.trim(field);
							field = Const.replace(field, " ", "_");
							field = Const.replace(field, "-", "_");
						}

						LinkedHashMap rec = new LinkedHashMap();
						rec.put("name", field);
						rec.put("type", "String");
						list.add(rec);
					}
				}

			}

		} else {

		}
		return list;
	}
}
