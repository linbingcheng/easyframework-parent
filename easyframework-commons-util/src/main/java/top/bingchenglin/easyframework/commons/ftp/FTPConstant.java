package top.bingchenglin.easyframework.commons.ftp;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.jcoc.common.ftp.Constant
 * @Description: Constant
 * @date 2016/5/5 9:50
 */
public interface FTPConstant {
    String PARAM_NULL = "入参:[%s] 为空";
    String PARAM_SIZE_ERROR = "入参[%s]和入参[%s]长度不一致";
    String PARAM_PATH_ERROR = "更改FTP工作目录失败，请检查路径：[%s]是否存在";
    String PARAM_UPLOAD_ERROR = "文件名：[%s] 上传失败";
    String PARAM_UPLOADTYPE_ERROR = "上传文件方式设置失败";
    String PARAM_DOWNLOAD_ERROR = "下载文件:[%s] 失败";
    String PARAM_MKDIR_ERROR = "文件下载时目录: [%s] 创建失败";
    String PARAM_LISTFILE_ERROR = "列出目录: [%s] 所有文件时失败";
}
