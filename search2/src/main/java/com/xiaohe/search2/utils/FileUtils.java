package com.xiaohe.search2.utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-05-02 18:06
 */
public class FileUtils {
    /**
     * 将MultipartFile转为File
     * @param mfile
     * @return
     * @throws IOException
     */
    public static File multipartFileToFile(MultipartFile mfile) throws IOException {
        File file = new File(mfile.getOriginalFilename());
        org.apache.commons.io.FileUtils.copyInputStreamToFile(mfile.getInputStream(), file);
        return file;
    }



    /**
     * 从前端的请求中拿到文件
     * @param request
     * @return
     *
     */
    public static Map<String, File> getFilesFromRequest(HttpServletRequest request) throws IOException {
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        Map<String, File> files = new HashMap<>();
        //判断 request 是否有文件上传,即多部分请求
        if(multipartResolver.isMultipart(request)){
            //转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            List<MultipartFile> file1 = multiRequest.getFiles("file");
            for (MultipartFile f : file1) {
                String path = f.getOriginalFilename();
                File file = multipartFileToFile(f);
                files.put(path, file);
            }
        }
        return files;
    }
}
