package com.aofei.kettle.repository.controller;

import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.kettle.App;
import com.aofei.kettle.PluginFactory;
import com.aofei.kettle.base.GraphCodec;
import com.aofei.kettle.bean.RepositoryCheckNode;
import com.aofei.kettle.bean.RepositoryNode;
import com.aofei.kettle.cluster.SlaveServerCodec;
import com.aofei.kettle.core.database.DatabaseCodec;
import com.aofei.kettle.repository.beans.DirectoryVO;
import com.aofei.kettle.repository.beans.RepositoryCascaderVO;
import com.aofei.kettle.repository.beans.RepositoryNodeType;
import com.aofei.kettle.repository.beans.RepositoryObjectVO;
import com.aofei.kettle.utils.*;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.missing.MissingEntry;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.*;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.missing.MissingTrans;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import springfox.documentation.annotations.ApiIgnore;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/repository")
@Api(tags = "资源库接口api")
public class KettleRepositoryController extends BaseController {

	@ApiOperation(value = "创建一个资源库目录")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "dir", value = "父目录，如果不存在父目录就使用根目录", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "name", value = "新目录的名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/createDir")
	protected void createDir(@RequestParam String dir, @RequestParam String name,@ApiIgnore @CurrentUser CurrentUserResponse user) throws KettleException, IOException {
		dir = com.aofei.base.common.Const.getUserPath(user.getOrganizerId(),dir);
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface path = repository.findDirectory(dir);
		if(path == null)
			path = repository.getUserHomeDirectory();
		
		RepositoryDirectoryInterface child = path.findChild(name);
		if(child == null) {
			repository.createRepositoryDirectory(path, name.trim());
		} else {
			JsonUtils.fail("该目录已经存在，请重新输入！");
			return;
		}
		JsonUtils.success("目录创建成功！");
	}
	
	@ApiOperation(value = "在资源库中创建一个转换")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "dir", value = "父目录，如果不存在父目录就使用根目录", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "name", value = "转换名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/createTrans")
	protected void createTrans(@RequestParam String dir, @RequestParam String transName,@ApiIgnore @CurrentUser CurrentUserResponse user) throws KettleException, IOException {

		dir = com.aofei.base.common.Const.getUserPath(user.getOrganizerId(),dir);

		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();
		
		if(repository.exists(transName, directory, RepositoryObjectType.TRANSFORMATION)) {
			JsonUtils.fail("该转换已经存在，请重新输入！");
			return;
		}
		
		TransMeta transMeta = new TransMeta();
		transMeta.setRepository(App.getInstance().getRepository());
		transMeta.setMetaStore(App.getInstance().getMetaStore());
		transMeta.setName(transName);
		transMeta.setRepositoryDirectory(directory);
		
		repository.save(transMeta, "创建转换", null);
		
		String transPath = directory.getPath();
		if(!transPath.endsWith("/"))
			transPath = transPath + '/';
		transPath = transPath + transName;
		
		JsonUtils.success(transPath);
		
	}
	
	@ApiOperation(value = "在资源库中创建一个作业")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "dir", value = "父目录，如果不存在父目录就使用根目录", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "name", value = "作业名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/createJob")
	protected void createJob(@RequestParam String dir, @RequestParam String jobName) throws KettleException, IOException {
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();
		
		if(repository.exists(jobName, directory, RepositoryObjectType.JOB)) {
			JsonUtils.fail("该转换已经存在，请重新输入！");
			return;
		}
		
		JobMeta jobMeta = new JobMeta();
		jobMeta.setRepository(App.getInstance().getRepository());
		jobMeta.setMetaStore(App.getInstance().getMetaStore());
		jobMeta.setName(jobName);
		jobMeta.setRepositoryDirectory(directory);
		
		repository.save(jobMeta, "创建作业", null);
		
		String jobPath = directory.getPath();
		if(!jobPath.endsWith("/"))
			jobPath = jobPath + '/';
		jobPath = jobPath + jobName;
		
		JsonUtils.success(jobPath);
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/drop")
	protected void drop(@RequestParam String path, @RequestParam String type) throws KettleException, IOException {
		Repository repository = App.getInstance().getRepository();
		
		String dir = path.substring(0, path.lastIndexOf("/"));
		String name = path.substring(path.lastIndexOf("/") + 1);
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();
		
		if(RepositoryObjectType.TRANSFORMATION.getTypeDescription().equals(type)
				|| RepositoryObjectType.TRANSFORMATION.getExtension().equals(type)) {
			ObjectId id_transformation = repository.getTransformationID(name, directory);
			if(id_transformation != null) {
				repository.deleteTransformation(id_transformation);
			}
		} else if(RepositoryObjectType.JOB.getTypeDescription().equals(type)
				|| RepositoryObjectType.JOB.getExtension().equals(type)) {
			ObjectId id_job = repository.getJobId(name, directory);
			if(id_job != null) {
				repository.deleteJob(id_job);
			}
		} else if(StringUtils.isEmpty(type) || "dir".equalsIgnoreCase(type)) {
			directory = repository.findDirectory(path);
			if(repository.getJobAndTransformationObjects(directory.getObjectId(), true).size() > 0) {
				JsonUtils.fail("删除失败，该目录下存在子元素，请先移除他们！");
				return;
			}
			
			if(repository.getDirectoryNames(directory.getObjectId()).length > 0) {
				JsonUtils.fail("删除失败，该目录下存在子元素，请先移除他们！");
				return;
			}
			
			repository.deleteRepositoryDirectory(directory);
		}
		
		JsonUtils.success("操作成功");
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/open")
	protected void open(@RequestParam String path, @RequestParam String type,@CurrentUser CurrentUserResponse user) throws Exception {
		String dir = path.substring(0, path.lastIndexOf("/"));
		String name = path.substring(path.lastIndexOf("/") + 1);
		
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();
		
		if(RepositoryObjectType.TRANSFORMATION.getTypeDescription().equals(type)) {
			TransMeta transMeta = repository.loadTransformation(name, directory, null, true, null);
			transMeta.setRepositoryDirectory(directory);
	    	
			GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
			String graphXml = codec.encode(transMeta,user);
			
			JsonUtils.responseXml(StringEscapeHelper.encode(graphXml));
		} else if(RepositoryObjectType.JOB.getTypeDescription().equals(type)) {
			JobMeta jobMeta = repository.loadJob(name, directory, null, null);
	    	jobMeta.setRepositoryDirectory(directory);
	    	
	    	GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.JOB_CODEC);
			String graphXml = codec.encode(jobMeta,user);
			
			JsonUtils.responseXml(StringEscapeHelper.encode(graphXml));
		}
	}
	
	@ApiOperation(value = "加载资源库中的一个转换或作业", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "path", value = "对象路径", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "type", value = "对象类型：transformation or job", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/open2")
	protected void open2(@RequestParam String path, @RequestParam String type,@CurrentUser CurrentUserResponse user) throws Exception {
		String dir = path.substring(0, path.lastIndexOf("/"));
		String name = path.substring(path.lastIndexOf("/") + 1);
		
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();
		
		if(RepositoryObjectType.TRANSFORMATION.getTypeDescription().equals(type)) {
			TransMeta transMeta = repository.loadTransformation(name, directory, null, true, null);
			transMeta.setRepositoryDirectory(directory);
	    	
			GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
			String graphXml = codec.encode(transMeta, user);
			
			JsonUtils.success(StringEscapeHelper.encode(graphXml));
		} else if(RepositoryObjectType.JOB.getTypeDescription().equals(type)) {
			
			JobMeta jobMeta = repository.loadJob(name, directory, null, null);
	    	jobMeta.setRepositoryDirectory(directory);
	    	
	    	GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.JOB_CODEC);
			String graphXml = codec.encode(jobMeta, user);
			
			JsonUtils.success(StringEscapeHelper.encode(graphXml));
		}
	}

	@Authorization
	@RequestMapping(method=RequestMethod.POST, value="/listElements")
	protected  List<RepositoryObjectVO> listElements(@RequestParam String path,@ApiIgnore @CurrentUser CurrentUserResponse user) throws KettleException, IOException {
		ArrayList list = new ArrayList();
		String root = com.aofei.base.common.Const.getRootPath(user.getOrganizerId());
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface dir = null;
		if(StringUtils.hasText(path) && !"/".equalsIgnoreCase(path)){
			path = com.aofei.base.common.Const.getUserPath(user.getOrganizerId(),path);
			dir = repository.findDirectory(path);
		}
		else{
			dir = repository.findDirectory(root);
		}
		
		List<RepositoryDirectoryInterface> directorys = dir.getChildren();
		for(RepositoryDirectoryInterface child : directorys) {
			DirectoryVO directory = new DirectoryVO(child,root);
			list.add(directory);
		}
		
		String transPath = dir.getPath();
		List<RepositoryElementMetaInterface> elements = repository.getTransformationObjects(dir.getObjectId(), false);
		if(elements != null) {
			for(RepositoryElementMetaInterface e : elements) {
				RepositoryObjectVO ro = new RepositoryObjectVO(e,root);
				list.add(ro);
			}
		}
	
		elements = repository.getJobObjects(dir.getObjectId(), false);
		if(elements != null) {
			for(RepositoryElementMetaInterface e : elements) {
				RepositoryObjectVO ro = new RepositoryObjectVO(e,root);
				list.add(ro);
			}
		}
		
		return list;
	}
	
	/**
	 * 资源库浏览，生成树结构
	 * 
	 * @throws KettleException 
	 * @throws IOException 
	 */
	@RequestMapping(method=RequestMethod.POST, value="/explorer")
	protected @ResponseBody List explorer(@RequestParam String path, @RequestParam int loadElement) throws KettleException, IOException {
		ArrayList list = new ArrayList();
		
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface dir = null;
		if(StringUtils.hasText(path))
			dir = repository.findDirectory(path);
		else
			dir = repository.getUserHomeDirectory();
		
		List<RepositoryDirectoryInterface> directorys = dir.getChildren();
		for(RepositoryDirectoryInterface child : directorys) {
			list.add(RepositoryNode.initNode(child.getName(), child.getPath()));
		}
		
		if(RepositoryNodeType.includeTrans(loadElement)) {
			List<RepositoryElementMetaInterface> elements = repository.getTransformationObjects(dir.getObjectId(), false);
			if(elements != null) {
				for(RepositoryElementMetaInterface e : elements) {
					String transPath = dir.getPath();
					if(!transPath.endsWith("/"))
						transPath = transPath + '/';
					transPath = transPath + e.getName();
					
					list.add(RepositoryNode.initNode(e.getName(),  transPath, e.getObjectType()));
				}
			}
		}
		
		if(RepositoryNodeType.includeJob(loadElement)) {
			List<RepositoryElementMetaInterface> elements = repository.getJobObjects(dir.getObjectId(), false);
			if(elements != null) {
				for(RepositoryElementMetaInterface e : elements) {
					String transPath = dir.getPath();
					if(!transPath.endsWith("/"))
						transPath = transPath + '/';
					transPath = transPath + e.getName();
					
					list.add(RepositoryNode.initNode(e.getName(),  transPath, e.getObjectType()));
				}
			}
		}
		
		return list;
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/exp")
	protected @ResponseBody void exp(@RequestParam String data) throws KettleException, IOException {
		JSONArray jsonArray = JSONArray.fromObject(data);
		
		Repository repository = App.getInstance().getRepository();
		
		File file = new File("exp_" + repository.getName() +"_" + String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", new Date()) + ".zip");
		FileOutputStream fos = new FileOutputStream(file);
		ZipOutputStream out = new ZipOutputStream( fos );
		
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String path = jsonObject.optString("path");
			String entryPath = path.substring(1);
			String dir = path.substring(0, path.lastIndexOf("/"));
			String name = path.substring(path.lastIndexOf("/") + 1);
			
			RepositoryDirectoryInterface directory = repository.findDirectory(dir);
			if(RepositoryObjectType.TRANSFORMATION.getTypeDescription().equals(jsonObject.optString("type"))) {
				TransMeta transMeta = repository.loadTransformation(name, directory, null, true, null);
				String xml = XMLHandler.getXMLHeader() + "\n" + transMeta.getXML();
				out.putNextEntry(new ZipEntry(entryPath + RepositoryObjectType.TRANSFORMATION.getExtension()));	
				out.write(xml.getBytes(Const.XML_ENCODING));
			} else if(RepositoryObjectType.JOB.getTypeDescription().equals(jsonObject.optString("type"))) {
				JobMeta jobMeta = repository.loadJob(name, directory, null, null);
				String xml = XMLHandler.getXMLHeader() + "\n" + jobMeta.getXML();
				out.putNextEntry(new ZipEntry(entryPath + RepositoryObjectType.JOB.getExtension()));	
				out.write(xml.getBytes(Const.XML_ENCODING));
			}
			
		}
		
		out.close();
		fos.close();
		
		JsonUtils.success(StringEscapeHelper.encode(file.getAbsolutePath()));
	}
	
	private boolean singleImport(Repository repository, RepositoryDirectoryInterface parent, String fileName, InputStream is) {
		try {
			Document doc = XMLHandler.loadXMLFile(is);
			Element root = doc.getDocumentElement();
			
			if(fileName.endsWith(RepositoryObjectType.TRANSFORMATION.getExtension())) {
				
				TransMeta transMeta = new TransMeta();
				transMeta.loadXML(root, null, App.getInstance().getMetaStore(), repository, true, new Variables(), null);
				for(StepMeta stepMeta : transMeta.getSteps()) {
					if(stepMeta.getStepMetaInterface() instanceof MissingTrans) {
						System.out.println("......导入失败" + fileName + "，无法识别的转换组件：" + stepMeta.getName());
						return false;
					}
				}
				
				
				boolean flag = repository.exists(transMeta.getName(), parent, RepositoryObjectType.TRANSFORMATION);
			    if(flag) return false;
				
				transMeta.setRepositoryDirectory( parent );
			    transMeta.setTransstatus(-1);
			    transMeta.setModifiedDate(new Date());
			    
			    repository.save(transMeta, "初次导入", null);
			} else if(fileName.endsWith(RepositoryObjectType.JOB.getExtension())) {
				JobMeta jobMeta = new JobMeta();
				jobMeta.loadXML(root, repository, null);
				
				for(JobEntryCopy jobEntryCopy : jobMeta.getJobCopies()) {
					if(jobEntryCopy.getEntry() instanceof MissingEntry) {
						System.out.println("......导入失败" + fileName + "，无法识别的作业组件：" + jobEntryCopy.getName());
						return false;
					}
				}
				
				boolean flag = repository.exists(jobMeta.getName(), parent, RepositoryObjectType.JOB);
			    if(flag) return false;
				
				
				jobMeta.setRepositoryDirectory(parent);
				jobMeta.setJobstatus(-1);
				jobMeta.setModifiedDate(new Date());
				
				repository.save(jobMeta, "初次导入", null);
			}
		    
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}
		
		return false;
	}
	
	@ApiOperation(value = "多文件导入，可以一次导入多个ktr或kjb文件", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "repositoryCurrentDir", value = "当前资源库目录", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "filesPath", value = "文件路径，可以是多个，文件需要先调用上传接口", paramType="query", dataType = "string")
	})
	@RequestMapping("/multiImport")
	protected @ResponseBody void multiImport(@RequestParam String repositoryCurrentDir, @RequestParam String filesPath) throws KettleException, IOException {
		ArrayList list = new ArrayList();
		
		JSONArray jsonArray = JSONArray.fromObject(filesPath);
		for(int i=0; i<jsonArray.size(); i++) {
			String filePath = jsonArray.getString(i);
			
			File file = new File(filePath);
			if(file.isFile()) {
				Repository repository = App.getInstance().getRepository();
				RepositoryDirectoryInterface parent = repository.findDirectory(repositoryCurrentDir);
				if(parent != null) {
					if(!singleImport(repository, parent, file.getName(), FileUtils.openInputStream(file))) {
						list.add(file.getName() + " 导入失败！请检查本目录下是否已存在该对象！");
					}
				}
				
				file.delete();
			}
			
		}
		
		if(list.size() > 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("success", false);
			jsonObject.put("files", list);
			JsonUtils.response(jsonObject);
		} else {
			JsonUtils.success("导入成功！");
		}
		
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/imp")
	protected @ResponseBody void imp(@RequestParam String filePath) throws KettleException, IOException {
		File file = new File(filePath);
		ZipFile zip = new ZipFile(file);
		
		ArrayList list = new ArrayList();
		Repository repository = App.getInstance().getRepository();
		
		try {
			Enumeration<? extends ZipEntry> enumeration = zip.entries();
			while(enumeration.hasMoreElements()) {
				ZipEntry entry = enumeration.nextElement();
			
            	if(entry.isDirectory())
            		continue;
            	
                String entryFileName = entry.getName();
                String fileName = entryFileName;
                RepositoryDirectoryInterface parent = repository.getUserHomeDirectory();
                if(entryFileName.indexOf("/") > 0) {
                	List<String> paths = Lists.newArrayList(entryFileName.split("/"));
                	fileName = paths.remove(paths.size() - 1);
                	
                	for(String dir : paths) {
                		RepositoryDirectoryInterface child = parent.findChild(dir);
                		if(child == null) {
                			child = repository.createRepositoryDirectory(parent, dir);
                		}
                		parent = child;
                	}
                	
                }
                
    			if(!singleImport(repository, parent, fileName, zip.getInputStream(entry))) {
					list.add(entryFileName + " 导入失败！请检查资源库中是否已存在该对象！");
				}    
                
            }
		} finally {
			zip.close();
		}
		
		
		if(list.size() > 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("success", false);
			jsonObject.put("files", list);
			JsonUtils.response(jsonObject);
		} else {
			JsonUtils.success("导入成功！");
		}
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/imptree")
	protected @ResponseBody List imptree(@RequestParam String filePath) throws KettleException, IOException {
		FileInputStream fis = new FileInputStream(new File(filePath));
		ZipInputStream is = new ZipInputStream(fis);
		
		Repository repository = App.getInstance().getRepository();
		
		ArrayList<RepositoryCheckNode> list = new ArrayList<RepositoryCheckNode>();
		ZipFile zip = new ZipFile(new File(filePath));
		Enumeration<ZipEntry> iter = (Enumeration<ZipEntry>) zip.entries();
		while(iter.hasMoreElements()) {
			List<RepositoryCheckNode> temp = list;
			ZipEntry entry = iter.nextElement();
			
			if(entry.isDirectory())
				continue;
			
			String[] strings = entry.getName().split("/");
			String currentDir = "";
			for(int i=0; i<strings.length; i++) {
				currentDir += "/" + strings[i];
				
				boolean found = false;
				for(RepositoryCheckNode node : temp) {
					if(node.getText().equals(strings[i])) {
						temp = node.getChildren();
						found = true;
						break;
					}
				}
				
				if(!found) {
					RepositoryCheckNode node = null;
					if(i == (strings.length - 1)) {
						if(strings[i].endsWith(RepositoryObjectType.TRANSFORMATION.getExtension())) {
							node = RepositoryCheckNode.initNode(strings[i], currentDir, RepositoryObjectType.TRANSFORMATION, true);
							
							String parentDir = currentDir.substring(0, currentDir.lastIndexOf("/"));
							RepositoryDirectoryInterface dir = repository.findDirectory(parentDir);
							String name = strings[i].substring(0, strings[i].lastIndexOf("."));
							if(dir != null) {
								node.setRepoExist(repository.exists(name, dir, RepositoryObjectType.TRANSFORMATION));
								node.setChecked(true);
							} else {
								node.setRepoExist(false);
							}
							
						} else if(strings[i].endsWith(RepositoryObjectType.JOB.getExtension())) {
							node = RepositoryCheckNode.initNode(strings[i], currentDir, RepositoryObjectType.JOB, true);
							
							String parentDir = currentDir.substring(0, currentDir.lastIndexOf("/"));
							RepositoryDirectoryInterface dir = repository.findDirectory(parentDir);
							String name = strings[i].substring(0, strings[i].lastIndexOf("."));
							if(dir != null) {
								node.setRepoExist(repository.exists(name, dir, RepositoryObjectType.JOB));
							} else {
								node.setRepoExist(false);
							}
						}
					} else {
						node = RepositoryCheckNode.initNode(strings[i], currentDir);
					}
					temp.add(node);
					temp = node.getChildren();
				}
			}
			
		}
		
		is.close();
		fis.close();
		
		return list;
	}

	@RequestMapping(method=RequestMethod.POST, value="/cascader")
	protected Response<List<RepositoryCascaderVO>> cascader(@ApiIgnore @CurrentUser CurrentUserResponse user) throws KettleException, IOException {
		Repository repository = App.getInstance().getRepository();
		List<RepositoryCascaderVO> list = new ArrayList<>();

		String root = com.aofei.base.common.Const.getRootPath(user.getOrganizerId());
		RepositoryDirectoryInterface dir = repository.findDirectory(root);

		RepositoryCascaderVO repositoryCascaderVO = new RepositoryCascaderVO(user.getOrganizerName(),user.getOrganizerName());

		List<RepositoryCascaderVO> childs = getCascaderChildren(repository,dir);
		if(!childs.isEmpty()){
			repositoryCascaderVO.setChildren(childs);
			list.add(repositoryCascaderVO);
		}

		return Response.ok(list);

	}

	private List<RepositoryCascaderVO> getCascaderChildren(Repository repository, RepositoryDirectoryInterface dir) throws KettleException {
		List<RepositoryCascaderVO> list = new ArrayList<>();
		List<RepositoryElementMetaInterface> elements = repository.getTransformationObjects(dir.getObjectId(), false);
		List<RepositoryDirectoryInterface> directorys = dir.getChildren();
		if(elements != null) {
			for(RepositoryElementMetaInterface e : elements) {
				RepositoryCascaderVO ro = new RepositoryCascaderVO(e,".ktr");
				list.add(ro);
			}
		}

		elements = repository.getJobObjects(dir.getObjectId(), false);
		if(elements != null) {
			for(RepositoryElementMetaInterface e : elements) {
				RepositoryCascaderVO ro = new RepositoryCascaderVO(e,".kjb");
				list.add(ro);
			}
		}

		for(RepositoryDirectoryInterface child : directorys) {
			RepositoryCascaderVO ro = new RepositoryCascaderVO(child);
			List<RepositoryCascaderVO> childs = getCascaderChildren(repository,child);
			if(!childs.isEmpty()){
				ro.setChildren(childs);
				list.add(ro);
			}
		}

		return list;


	}

	
	@RequestMapping(method=RequestMethod.POST, value="/exptree")
	protected @ResponseBody List exptree(@RequestParam int loadElement) throws KettleException, IOException {
		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface dir = repository.getUserHomeDirectory();
		List list = browser(repository, dir, loadElement);
		return list;
	}
	
	private List browser(Repository repository, RepositoryDirectoryInterface dir, int loadElement) throws KettleException {
		ArrayList list = new ArrayList();
		
		List<RepositoryDirectoryInterface> directorys = dir.getChildren();
		for(RepositoryDirectoryInterface child : directorys) {
//			RepositoryCheckNode node = new RepositoryCheckNode(child.getName());
//			node.setChildren(browser(repository, child, loadElement));
//			node.setPath(child.getPath());
			list.add(RepositoryCheckNode.initNode(child.getName(), child.getPath(), browser(repository, child, loadElement)));
		}
		
		if(RepositoryNodeType.includeTrans(loadElement)) {
			List<RepositoryElementMetaInterface> elements = repository.getTransformationObjects(dir.getObjectId(), false);
			if(elements != null) {
				for(RepositoryElementMetaInterface e : elements) {
					String transPath = dir.getPath();
					if(!transPath.endsWith("/"))
						transPath = transPath + '/';
					transPath = transPath + e.getName();
					
					list.add(RepositoryCheckNode.initNode(e.getName(), transPath, e.getObjectType()));
					
				}
			}
		}
		
		if(RepositoryNodeType.includeJob(loadElement)) {
			List<RepositoryElementMetaInterface> elements = repository.getJobObjects(dir.getObjectId(), false);
			if(elements != null) {
				for(RepositoryElementMetaInterface e : elements) {
					String transPath = dir.getPath();
					if(!transPath.endsWith("/"))
						transPath = transPath + '/';
					transPath = transPath + e.getName();
					
					list.add(RepositoryCheckNode.initNode(e.getName(), transPath, e.getObjectType()));
				}
			}
		}
		
		return list;
	}
	
	@ApiOperation(value = "返回资源库中所有的子服务器信息", httpMethod = "POST")
	@ResponseBody
	@RequestMapping("/slaveservers")
	protected void slaveservers() throws IOException, KettleException {
		Repository repository = App.getInstance().getRepository();
		
		ObjectId[] slaveIDs = repository.getSlaveIDs(false);
		JSONArray jsonArray = new JSONArray();
		for(ObjectId id_slave: slaveIDs) {
			SlaveServer slaveServer = repository.loadSlaveServer(id_slave, null);
			jsonArray.add(SlaveServerCodec.encode(slaveServer));
		}
		
		JsonUtils.response(jsonArray);
	}
	
	@ApiOperation(value = "返回资源库中所有的数据库连接信息", httpMethod = "POST")
	@RequestMapping("/databases")
	protected @ResponseBody void databases() throws IOException, KettleException {
		Repository repository = App.getInstance().getRepository();
		
		ObjectId[] databaseIds = repository.getDatabaseIDs(false);
		JSONArray jsonArray = new JSONArray();
		for(ObjectId databaseId: databaseIds) {
			DatabaseMeta databaseMeta = repository.loadDatabaseMeta(databaseId, null);
			JSONObject jsonObject = DatabaseCodec.encode(databaseMeta);
			jsonObject.put("changedDate", XMLHandler.date2string(databaseMeta.getChangedDate()));
			jsonArray.add(jsonObject);
		}
		
		JsonUtils.response(jsonArray);
	}
	
	@ApiOperation(value = "返回资源库中所有的分区数据库连接信息", httpMethod = "POST")
	@RequestMapping("/partitionDatabases")
	protected @ResponseBody void partitionDatabases() throws IOException, KettleException {
		Repository repository = App.getInstance().getRepository();
		
		ObjectId[] databaseIds = repository.getDatabaseIDs(false);
		JSONArray jsonArray = new JSONArray();
		for(ObjectId databaseId: databaseIds) {
			DatabaseMeta databaseMeta = repository.loadDatabaseMeta(databaseId, null);
			
			if(databaseMeta.isPartitioned()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", databaseMeta.getName());
				jsonArray.add(jsonObject);
			}
			
		}
		
		JsonUtils.response(jsonArray);
	}
	
	@ApiOperation(value = "返回资源库中所有的数据库连接信息，包含连接是否可用的状态", httpMethod = "POST")
	@RequestMapping("/databaseStatus")
	protected @ResponseBody Collection databaseStatus() throws IOException, KettleException, InterruptedException, ExecutionException {
		Repository repository = App.getInstance().getRepository();
		
		ObjectId[] databaseIds = repository.getDatabaseIDs(false);
		ExecutorService executor = Executors.newCachedThreadPool();
		
		HashMap<String, JSONObject> result = new HashMap<String, JSONObject>();
		HashMap<String, Future<Integer>> dbStatus = new HashMap<String, Future<Integer>>();
		for(ObjectId databaseId: databaseIds) {
			DatabaseMeta databaseMeta = repository.loadDatabaseMeta(databaseId, null);
			JSONObject jsonObject = DatabaseCodec.encode(databaseMeta);
			result.put(databaseMeta.getName(), jsonObject);
			
			String port = databaseMeta.getDatabasePortNumberString();
			Future<Integer> f = executor.submit(new ServerChecker(databaseMeta.getHostname(), Integer.parseInt(port)));
			dbStatus.put(databaseMeta.getName(), f);
		}
		
		for(Map.Entry<String, Future<Integer>> entry : dbStatus.entrySet()) {
			Integer status = entry.getValue().get();
			result.get(entry.getKey()).put("status", status);
		}
		
		return result.values();
	}
	
	/**
	 * 
	 * @param loginInfo
	 * @throws IOException 
	 * @throws KettleException 
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/changeStatus")
	protected void changeStatus(String path, int status) throws IOException, KettleException {
		if(path.endsWith(RepositoryObjectType.TRANSFORMATION.getExtension())) {
			TransMeta transMeta = RepositoryUtils.readTrans(path);
			transMeta.setTransstatus(status);
			App.getInstance().getRepository().save(transMeta, "更新转换状态：" + status, null);
			
			JsonUtils.success("操作成功！");
		} else if(path.endsWith(RepositoryObjectType.JOB.getExtension())) {
			JobMeta jobMeta = RepositoryUtils.readJob(path);
			jobMeta.setJobstatus(status);
			App.getInstance().getRepository().save(jobMeta, "更新作业状态：" + status, null);
			
			JsonUtils.success("操作成功！");
		}
		
		
		JsonUtils.fail("无法识别的类型！");
	}
	
	/**
	 * 断开资源库
	 * 
	 * @param loginInfo
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/logout")
	protected void logout() throws IOException {
//		App.getInstance().selectRepository(App.getInstance().getDefaultRepository());
		JsonUtils.session().invalidate();
		JsonUtils.success("操作成功！");
	}
	
}
