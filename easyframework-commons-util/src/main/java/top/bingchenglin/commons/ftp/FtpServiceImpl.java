package top.bingchenglin.commons.ftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.jcoc.common.ftp.FtpServiceImpl
 * @Description: FtpServiceImpl
 * @date 2016/4/29 10:09
 */
public class FtpServiceImpl implements FtpService, FTPConstant {

    private static final Logger LOGGER = LogManager.getLogger(FtpServiceImpl.class);
    private String hostName = "default";
    private String projectName = "jcoc";
    private boolean usezkcfg = false;
    private String zkServers;
    private String zkPath;

    private FtpInfos ftpInfos;

    public FtpServiceImpl() {
    }

    public FtpServiceImpl(FtpInfos ftpInfos) {
        this.ftpInfos = ftpInfos;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public String getZkPath() {
        return zkPath;
    }

    public void setZkPath(String zkPath) {
        this.zkPath = zkPath;
    }

    public FtpInfos getFtpInfos() {
        return ftpInfos;
    }

    public void setFtpInfos(FtpInfos ftpInfos) {
        this.ftpInfos = ftpInfos;
    }

    public boolean isUsezkcfg() {
        return usezkcfg;
    }

    public void setUsezkcfg(boolean usezkcfg) {
        this.usezkcfg = usezkcfg;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    private BufferedOutputStream mkLocalPath(String localpath, String localFile, String defaultLocalPath) throws IOException {
        BufferedOutputStream bos;
        try {
            localpath = StringUtils.isBlank(localpath) ? defaultLocalPath : localpath;
            File file = new File(localpath);
            if (!file.exists()) {
                file.mkdirs();
            }

            String localFileTemp = localpath + localFile;
            LOGGER.info("下载文件到本地路径：" + localFileTemp);
            bos= new BufferedOutputStream(new FileOutputStream(localFileTemp));

        } catch (Exception e) {
            throw new FtpIOException(String.format(PARAM_MKDIR_ERROR, localpath), e);
        }
        return bos;
    }

    @Override
    public void upload(String fileName, BufferedInputStream bistream) throws IOException {
        this.upload(fileName, bistream, null);
    }

    @Override
    public void upload(String fileName, BufferedInputStream bistream, String remotepath) throws IOException {
        this.upload(Arrays.asList(fileName), Arrays.asList(bistream), remotepath);
    }

    @Override
    public void upload(List<String> fileNames, List<BufferedInputStream> bistreams) throws IOException {
        this.upload(fileNames, bistreams, null);
    }

    @Override
    public void upload(List<String> fileNames, List<BufferedInputStream> bistreams, String remotepath) throws IOException {

        FtpBasic fb = createFtpBaseic();

        Validate.notEmpty(fileNames, PARAM_NULL, "fileNames");
        Validate.notEmpty(bistreams, PARAM_NULL, "bistreams");
        Validate.isTrue(fileNames.size() == bistreams.size(), String.format(PARAM_SIZE_ERROR, "fileNames", "bistreams"));

        try {
            fb.connect();

            for (int i = 0; i < fileNames.size(); i++) {
                fb.uploadBasic(fileNames.get(i), bistreams.get(i), remotepath);
            }
        } finally {
            fb.close();
        }
    }

    @Override
    public void download(String remoteFile, String localFile) throws IOException {
        this.download(remoteFile, localFile, null, null);
    }

    @Override
    public void download(List<String> remoteFiles, List<String> localFiles) throws IOException {
        this.download(remoteFiles, localFiles, null, null);
    }

    @Override
    public void download(String remoteFile, String localFile, String remotepath, String localpath) throws IOException {
        this.download(Arrays.asList(remoteFile), Arrays.asList(localFile), remotepath, localpath);
    }

    @Override
    public void download(List<String> remoteFiles, List<String> localFiles, String localpath) throws IOException {
        this.download(remoteFiles, localFiles, null, localpath);
    }

    @Override
    public void download(List<String> remoteFiles, List<String> localFiles, String remotepath, String localpath) throws IOException {

        Validate.notEmpty(remoteFiles, PARAM_NULL, "remoteFiles");
        Validate.notEmpty(localFiles, PARAM_NULL, "localFiles");
        Validate.isTrue(remoteFiles.size() == localFiles.size(), String.format(PARAM_SIZE_ERROR, "remoteFiles", "localFiles"));

        FtpBasic fb = createFtpBaseic();
        try {
            fb.connect();

            for (int i = 0; i < remoteFiles.size(); i++) {

                BufferedOutputStream bos = mkLocalPath(localpath, localFiles.get(i), fb.getFtpInfos().getLocalPath());

                fb.downloadbasic(remoteFiles.get(i), localFiles.get(i), bos, remotepath);
            }

        } finally {
            fb.close();
        }
    }

    @Override
    public String[] listFiles() throws IOException {
        return this.listFiles(null, null, null);
    }

    @Override
    public String[] listFiles(String path) throws IOException {
        return this.listFiles(path, null, null);
    }

    @Override
    public String[] listFiles(String prefix, String suffix) throws IOException {
        return this.listFiles(null, prefix, suffix);
    }

    @Override
    public String[] listFiles(String path, String prefix, String suffix) throws IOException {
        String[] files = null;
        FtpBasic fb = createFtpBaseic();
        try {

            files = fb.connect().listFiles(path, prefix, suffix);

        } finally {
            fb.close();
        }
        return files;
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        return this.deleteFile(null, fileName);
    }

    @Override
    public boolean deleteFile(String path, String fileName) throws IOException {

        FtpBasic fb = createFtpBaseic();
        try {

            return fb.connect().deleteFiles(path, fileName);

        } finally {
            fb.close();
        }
    }

    private FtpBasic createFtpBaseic() throws IOException {
        if(!isUsezkcfg() && ftpInfos == null) {
            throw new FtpIOException("请配置FtpInfos信息");
        }
        return isUsezkcfg() ? new FtpBasic(getZkServers(), getZkPath(), getHostName(), getProjectName()) : new FtpBasic(ftpInfos);
    }
}
