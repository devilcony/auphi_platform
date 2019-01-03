package com.aofei.kettle.core;

import com.aofei.kettle.utils.JsonUtils;
import com.aofei.kettle.utils.StringEscapeHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping(value = "/attachment")
@Api(tags = "文件上传下载接口api，用于资源库导入和导出")
public class AttachmentController {

	static LinkedList<File> files = new LinkedList<File>();
	static Timer deleter = new Timer();

	static {
		deleter.schedule(new TimerTask() {
			@Override
			public void run() {
				for(int i=0; i<files.size(); i++) {
					if(files.get(i).delete()) {
						files.remove(i);
						break;
					}
				}
			}
		}, 5000, 60 * 1000);
	}

	@ApiOperation(value = "文件下载 ")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "filePath", value = "文件路径", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "remove", value = "下载完后是否删除", paramType="query", dataType = "remove")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET, value="/download")
	protected void download(@RequestParam String filePath, @RequestParam boolean remove) throws KettleException, IOException {
		File file = new File(StringEscapeHelper.decode(filePath));
		if(file.isFile()) {
			JsonUtils.download(file);
			if(remove) {
				files.add(file);
			}
		}
	}

	@ApiOperation(value = "文件上传 ", httpMethod = "POST")
	@RequestMapping("/upload")
	protected @ResponseBody void upload(@RequestParam(value="file") MultipartFile file) throws KettleException, IOException {
		File dir = new File("upload");
		dir.mkdirs();
		dir = new File(dir, String.format("%1$tY%1$tm%1$td", new Date()));
		dir.mkdirs();
		File f = new File(dir, file.getOriginalFilename());
		System.out.println("===upload to:===" + f.getAbsolutePath());

		OutputStream os = null;
		InputStream is = null;
		try {
			os = FileUtils.openOutputStream(f);
			is = file.getInputStream();

			FileCopyUtils.copy(is, os);
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}

		JsonUtils.success(f.getAbsolutePath());
	}
}
