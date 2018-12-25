package com.aofei.datasource.controller;

import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.common.Const;
import com.aofei.base.controller.BaseController;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.datasource.exception.DiskError;
import com.aofei.datasource.model.request.DiskFileCreateRequest;
import com.aofei.datasource.model.request.DiskFileDeleteRequest;
import com.aofei.datasource.model.request.DiskFileRequest;
import com.aofei.datasource.model.response.DiskFileListResponse;
import com.aofei.datasource.model.response.DiskFileResponse;
import com.aofei.datasource.model.response.ResidualSpaceResponse;
import com.aofei.datasource.service.IDiskFileService;
import com.aofei.utils.DiskFileUtil;
import com.aofei.utils.StringUtils;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Log4j
@Api(tags = { "数据源管理-本地文件管理" })
@Authorization
@RestController
@RequestMapping(value = "/datasource/disk", produces = {"application/json;charset=UTF-8"})
public class DiskFileController extends BaseController {



    @Autowired
    private IDiskFileService diskFileService;

    /**
     * 服务器文件列表
     * @param request
     * @return
     */
    @ApiOperation(value = "服务器文件列表", notes = "服务器文件列表", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "目录", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/explorer", method = RequestMethod.POST)
    public Response<List<DiskFileListResponse>> explorer(
            @ApiIgnore DiskFileRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        request.setOrganizerName(user.getOrganizerName());

        List<DiskFileResponse> list = diskFileService.getFileExplorer(request);

        DiskFileListResponse response = new DiskFileListResponse();
        response.setList(list);
        response.setPath(request.getPath());
        return Response.ok(response) ;
    }


    /**
     * 服务器文件列表
     * @return
     */
    @ApiOperation(value = "磁盘剩余空间", notes = "磁盘剩余空间", httpMethod = "POST")
    @RequestMapping(value = "/residual", method = RequestMethod.POST)
    public Response<ResidualSpaceResponse> residual(
            @ApiIgnore @CurrentUser CurrentUserResponse user)  {

        String path =  Const.getUserDir(user.getOrganizerId());

        long total = DiskFileUtil.getTotalSizeOfFilesInDir(new File(path));


        ResidualSpaceResponse response = new ResidualSpaceResponse();
        response.setDiskSpace(DiskFileUtil.getPrintSize(user.getDiskSpace()));
        response.setResidualSpace(DiskFileUtil.getPrintSize(total));
        response.setPercentage(DiskFileUtil.getPercentage(total,user.getDiskSpace()));
       return Response.ok(response);
    }



    /**
     * 创建文件夹
     * @param request
     * @return
     */
    @ApiOperation(value = "创建文件夹", notes = "创建文件夹", httpMethod = "POST")
    @RequestMapping(value = "/mkdir", method = RequestMethod.POST)
    public Response<Boolean> mkdir(
            @RequestBody DiskFileCreateRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        request.setOrganizerName(user.getOrganizerName());
        return Response.ok(diskFileService.mkdir(request)) ;
    }

    /**
     * 上传文件
     * @param request
     * @return
     */
    @ApiOperation(value = "上传文件", notes = "上传文件", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 300001, message = "Insufficient disk space"),
            @ApiResponse(code = 200, message = "success")})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件", paramType = "form", required = true, dataType = "__file")
    })
    @PostMapping(value = "/upload",consumes = "multipart/*",headers = "content-type=multipart/form-data")
    public Response<Boolean> upload(
            HttpServletRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user) throws IOException {

        String userPath = Const.getUserDir(user.getOrganizerId());
        long total = DiskFileUtil.getTotalSizeOfFilesInDir(new File(userPath));

        String path = ServletRequestUtils.getStringParameter(request,"path", userPath);
        if(StringUtils.isEmpty(path) || "/".equalsIgnoreCase(path)){
            path = Const.getUserDir(user.getOrganizerId());
        }
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)) {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
            //获取multiRequest 中所有的文件名
            Iterator iter=multiRequest.getFileNames();
            while(iter.hasNext()) {
                //一次遍历所有文件
                MultipartFile file=multiRequest.getFile(iter.next().toString());

                long all = total+file.getSize();
                if(all > user.getDiskSpace()){
                    throw new ApplicationException(DiskError.INSUFFICIENT_DISK_SPACE.getCode(),DiskError.INSUFFICIENT_DISK_SPACE.getMessage());
                }
                if(file!=null) {
                    File filepath = new File(path,file.getOriginalFilename());
                    //上传
                    file.transferTo(filepath);
                }
            }
        }
        return Response.ok(true);
    }


    /**
     * 删除文件
     * @param request
     * @param user
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "删除文件", notes = "删除文件", httpMethod = "POST")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response<Integer> upload(
                @RequestBody DiskFileDeleteRequest request,
                @ApiIgnore @CurrentUser CurrentUserResponse user) throws IOException {

        File file = new File(request.getPath());
        if (!file.exists()) {
            new ApplicationException();
        } else {
            if (file.isFile()){
                boolean delete =  diskFileService.deleteFile(request.getPath());
                return Response.ok(1);
            }else{
                return Response.ok(diskFileService.deleteDirectory(request.getPath()));
            }

        }
        return Response.ok(0);
    }



}
