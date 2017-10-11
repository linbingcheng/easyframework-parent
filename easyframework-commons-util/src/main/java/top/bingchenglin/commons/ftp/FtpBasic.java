package top.bingchenglin.commons.ftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;


public class FtpBasic implements FTPConstant {
    private static final Logger LOGGER = LogManager.getLogger(FtpBasic.class);
    private FTPClient ftpClient;
    private FtpInfos ftpInfos;
    private String hostName;
    private String projectName;
    private String zkServers;
    private String zkPath;

    public FtpBasic() {
    }

    protected FtpBasic(String zkServers, String zkPath) {
        this(zkServers, zkPath, "default", "jcoc");
    }

    protected FtpBasic(String zkServers, String zkPath, String hostName, String projectName) {
        this.zkServers = zkServers;
        this.zkPath = zkPath;
        this.hostName = hostName;
        this.projectName = projectName;

        if (StringUtils.isNotBlank(zkServers) && StringUtils.isNotBlank(zkPath) && StringUtils.isNotBlank(hostName) && StringUtils.isNotBlank(projectName)) {
            FtpZkUtils.init(zkServers, zkPath);
            this.ftpInfos = FtpClientFactory.getInstance().getFtpInfos(hostName, projectName);
        }
    }

    public FtpBasic(FtpInfos ftpInfos) {
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

    public String getProjectName() {
        return projectName;
    }

    public FtpBasic setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public FtpInfos getFtpInfos() {
        return ftpInfos;
    }

    public void setFtpInfos(FtpInfos ftpInfos) {
        this.ftpInfos = ftpInfos;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public FtpBasic connect() throws IOException {
        ftpClient = FtpClientFactory.getInstance().createFtpClient(ftpInfos);
        return this;
    }

    public void close() throws IOException {
        if (ftpClient != null && ftpClient.isConnected()) {

            ftpClient.logout();
            LOGGER.info("FTP logout success");

            ftpClient.disconnect();
            LOGGER.info("FTP disconnect success");
        }
    }

    public void uploadBasic(String fileName, BufferedInputStream bistream, String uploadPath) throws IOException {
        this.uploadBasic(fileName, bistream, uploadPath, true);
    }
    public void uploadBasic(String fileName, BufferedInputStream bistream, String uploadPath, boolean isPassive) throws IOException {
        Validate.notBlank(fileName, PARAM_NULL, "fileName");
        Validate.notNull(bistream, PARAM_NULL, "bistream");

        try {
            setFtpFileType(isPassive);
            chgRemoteUploadPath(uploadPath);

            ftpClient.storeFile(fileName, bistream);

            LOGGER.info(String.format("上传文件:[%s]成功", fileName));
        } catch (IOException e) {
            throw new FtpIOException(String.format(PARAM_UPLOAD_ERROR, fileName), e);
        } finally {
            if (bistream != null)
                bistream.close();
        }
    }

    public String[] listFiles(String path, String prefix, String suffix) throws IOException {
        Set<String> sets = new HashSet<String>();
        try {
            setFtpFileType();
            chgRemoteDownPath(path);

            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile f : ftpFiles) {
                if (f.isDirectory()
                        || (StringUtils.isNotBlank(prefix) && !f.getName().startsWith(prefix))
                        || (StringUtils.isNotBlank(suffix) && !f.getName().endsWith(suffix))
                        ) {
                    continue;
                }
                sets.add(f.getName());
            }

        } catch (IOException e) {
            throw new FtpIOException(String.format(PARAM_LISTFILE_ERROR, path), e);
        }
        return sets.toArray(new String[]{});
    }

    public boolean deleteFiles(String path, String fileName) throws IOException {
        Validate.notBlank(fileName, PARAM_NULL, "fileName");
        try {
            setFtpFileType();
            chgRemoteDownPath(path);

            return ftpClient.deleteFile(fileName);

        } catch (IOException e) {
            throw new FtpIOException(String.format(PARAM_LISTFILE_ERROR, path), e);
        }
    }


    private void setFtpFileType(boolean isPassive) throws IOException {
        try {
            if(isPassive) {
                ftpClient.enterLocalPassiveMode();
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            throw new FtpIOException(PARAM_UPLOADTYPE_ERROR, e);
        }
    }

    private void setFtpFileType() throws IOException {
        setFtpFileType(true);
    }

    private void chgRemoteUploadPath(String path) throws IOException {
        path = StringUtils.isBlank(path) ? ftpInfos.getUploadPath() : path;
        if (!ftpClient.changeWorkingDirectory(formatPath(path))) {
            throw new FtpIOException(String.format(PARAM_PATH_ERROR, path));
        }
    }

    private void chgRemoteDownPath(String path) throws IOException {
        path = StringUtils.isBlank(path) ? ftpInfos.getDownloadPath() : path;
        if (!ftpClient.changeWorkingDirectory(formatPath(path))) {
            throw new FtpIOException(String.format(PARAM_PATH_ERROR, path));
        }
    }

    private String formatPath(String path) {
        Validate.notBlank(path, PARAM_NULL, "path");
        return MessageFormat.format(path, this.getParameter());
    }

    protected String[] getParameter() {
        return null;
    }


    public void downloadbasic(String remoteFile, String localFile, BufferedOutputStream outStream, String remotepath) throws IOException {
        this.downloadbasic(remoteFile, localFile, outStream, remotepath, true);
    }
    public void downloadbasic(String remoteFile, String localFile, BufferedOutputStream outStream, String remotepath, boolean isPassive) throws IOException {

        Validate.notBlank(remoteFile, PARAM_NULL, "remoteFile");
        Validate.notNull(localFile, PARAM_NULL, "localFile");

        try {
            setFtpFileType(isPassive);

            chgRemoteDownPath(remotepath);

            ftpClient.retrieveFile(remoteFile, outStream);
            LOGGER.info(String.format("下载文件:[%s] 成功", remoteFile));

        } catch (IOException e) {
            throw new FtpIOException(String.format(PARAM_DOWNLOAD_ERROR, remoteFile), e);
        } finally {
            if (outStream != null)
                outStream.close();
        }
    }
}
