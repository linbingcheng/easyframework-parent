package top.bingchenglin.easyframework.commons.ftp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.jcoc.common.ftp.FtpService
 * @Description: FtpService
 * @date 2016/4/29 9:52
 */
public interface FtpService {

    // 单个文件上传
    void upload(String fileName, BufferedInputStream bistream) throws IOException;

    // 单个文件上传至指定目录
    void upload(String fileName, BufferedInputStream bistream, String remotepath) throws IOException;

    // 多个文件上传
    void upload(List<String> fileNames, List<BufferedInputStream> bistreams) throws IOException;

    // 多个文件上传上传至指定目录
    void upload(List<String> fileNames, List<BufferedInputStream> bistreams, String remotepath) throws IOException;

    // 单个文件下载
    void download(String remoteFile, String localFile) throws IOException;

    // 多个文件下载
    void download(List<String> remoteFiles, List<String> localFiles) throws IOException;

    // 单个文件下载到指定目录
    void download(String remoteFile, String localFile, String remotepath, String localpath) throws IOException;

    // 多个文件下载到指定目录
    void download(List<String> remoteFiles, List<String> localFiles, String localpath) throws IOException;

    // 多个文件下载到指定目录
    void download(List<String> remoteFiles, List<String> localFiles, String remotepath, String localpath) throws IOException;

    // 列出目录文件
    String[] listFiles() throws IOException;

    String[] listFiles(String path) throws IOException;

    String[] listFiles(String prefix, String suffix) throws IOException;

    String[] listFiles(String path, String prefix, String suffix) throws IOException;

    boolean deleteFile(String fileName) throws IOException;

    boolean deleteFile(String path, String fileName) throws IOException;
}
