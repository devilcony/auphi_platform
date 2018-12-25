package com.aofei.datasource.service.impl;

import com.aofei.base.common.Const;
import com.aofei.base.exception.ApplicationException;
import com.aofei.datasource.exception.DiskError;
import com.aofei.datasource.model.request.DiskFileCreateRequest;
import com.aofei.datasource.model.request.DiskFileRequest;
import com.aofei.datasource.model.response.DiskFileResponse;
import com.aofei.datasource.service.IDiskFileService;

import com.aofei.utils.DiskFileUtil;
import com.aofei.utils.StringUtils;
import org.apache.commons.vfs2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @auther Tony
 * @create 2018-10-21 21:50
 */
@Service
public class DiskFileService implements IDiskFileService {

    private Logger logger = LoggerFactory.getLogger(DiskFileService.class);

    @Value("#{propertiesReader['disk.root.dir']}")
    private String rootDir  ; //磁盘根目录

    @Override
    public List<DiskFileResponse> getFileExplorer(DiskFileRequest request) {
        List<DiskFileResponse> list = new LinkedList<>();
        String path = request.getPath();
        if(StringUtils.isEmpty(path)){
            path = Const.getUserDir(request.getOrganizerId());
        }
        logger.info("path==>"+path);
        File[] files = new File(path).listFiles();
        for(File file : files) {
            DiskFileResponse response = null;
            if(file.isHidden())
                continue;
            if(file.isDirectory()) {
                response = new DiskFileResponse();
                response.setFilename(file.getName());
                response.setIsdir(Const.YES);
                response.setPath(file.getPath());
                response.setLastModified(file.lastModified());

            } else if(file.isFile() ){
                response = new DiskFileResponse();
                response.setFilename(file.getName());
                response.setIsdir(Const.NO);
                response.setPath(file.getPath());
                response.setSize(DiskFileUtil.getPrintSize(file.length()));
                response.setLastModified(file.lastModified());
            }

            list.add(response);
        }

        return list;
    }

    @Override
    public boolean mkdir(DiskFileCreateRequest request) {
        String path = request.getPath();
        if(StringUtils.isEmpty(path)){
            path = Const.getUserDir(request.getOrganizerId());
        }
        path = path+File.separator+request.getName();
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
            return true;
        }else{
            throw new ApplicationException(DiskError.DIR_EXISTS.getCode(),DiskError.DIR_EXISTS.getMessage());
        }

    }



    @Override
    public boolean deleteFile(String path) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        return fsManager.resolveFile(path).delete();
    }

    @Override
    public int deleteDirectory(String path) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        // 测试表明删除文件夹删除不了
        return fsManager.resolveFile(path).delete(getFileSelector(Type.FOLDER));
    }

    /**
     * 根据自定义类别获取FileSelector
     *
     * @param type 自己定义的类别，详见{@link:TestMonitorFile.Type}
     * @return
     * @auther <a href="mailto:leader1212@sina.com.cn">天涯</a>
     * Sep 1, 2010 3:59:30 PM
     */
    public static FileSelector getFileSelector (Type type) {
        return new FileTypeSelector(getFileType(type));
    }

    /**
     * 需要拷贝的文件类型
     * 实际测试中，FILE和FILE_OR_FOLDER效果一样。
     * @author ABBE
     *
     */
    public static enum Type {
        FILE, FOLDER, FILE_OR_FOLDER
    }

    /**
     * 根据自定义类别获取FileType
     *
     * @param type 自己定义的类别，详见{@link:TestMonitorFile.Type}
     * @return
     * @auther <a href="mailto:leader1212@sina.com.cn">天涯</a>
     * Sep 1, 2010 3:59:30 PM
     */
    public static FileType getFileType (Type type) {
        FileType fileType = null;
        if (type.equals(Type.FILE)) {
            fileType = FileType.FILE;
        } else if (type.equals(Type.FOLDER)) {
            fileType = FileType.FOLDER;
        } else if (type.equals(Type.FILE_OR_FOLDER)) {
            fileType = FileType.FILE_OR_FOLDER;
        }
        return fileType;
    }
}
