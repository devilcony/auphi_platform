package com.aofei.datasource.service;


import com.aofei.datasource.model.request.DiskFileCreateRequest;
import com.aofei.datasource.model.request.DiskFileRequest;
import com.aofei.datasource.model.response.DiskFileResponse;
import org.apache.commons.vfs2.FileSystemException;

import java.util.List;

public interface IDiskFileService  {
    List<DiskFileResponse> getFileExplorer(DiskFileRequest request);

    boolean mkdir(DiskFileCreateRequest request);

    String getRootPath(String organizerName);

    boolean deleteFile(String path) throws FileSystemException;

    int deleteDirectory(String path) throws FileSystemException;
}
