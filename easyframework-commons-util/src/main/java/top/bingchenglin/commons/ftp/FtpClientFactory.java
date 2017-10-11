package top.bingchenglin.commons.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.jcoc.common.ftp.FtpclientFactory
 * @Description: FtpclientFactory
 * @date 2016/4/29 10:47
 */
public class FtpClientFactory {
    private static final Logger LOGGER = LogManager.getLogger(FtpClientFactory.class);
    private static FtpClientFactory _INSTANCE;

    private String host = "%s.ftp.%s.host";
    private String port = "%s.ftp.%s.port";
    private String username = "%s.ftp.%s.username";
    private String password = "%s.ftp.%s.password";
    private String uploadpath = "%s.ftp.%s.uploadpath";
    private String downloadpath = "%s.ftp.%s.downloadpath";
    private String localpath = "%s.ftp.%s.localpath";
    private String timeout = "%s.ftp.%s.timeout";

    private FtpClientFactory() {
    }

    public static FtpClientFactory getInstance() {
        if (_INSTANCE == null) {
            synchronized (FtpClientFactory.class) {
                if (_INSTANCE == null) {
                    _INSTANCE = new FtpClientFactory();
                }
            }
        }
        return _INSTANCE;
    }

    public FtpInfos getFtpInfos(String hostName, String projectName) {

        String host = FtpZkUtils.getProperty(String.format(this.host, projectName, hostName), "127.0.0.1");
        String port = FtpZkUtils.getProperty(String.format(this.port, projectName, hostName), "21");
        String username = FtpZkUtils.getProperty(String.format(this.username, projectName, hostName));
        String password = FtpZkUtils.getProperty(String.format(this.password, projectName, hostName));
        String uploadpath = FtpZkUtils.getProperty(String.format(this.uploadpath, projectName, hostName), "/upload");
        String downloadpath = FtpZkUtils.getProperty(String.format(this.downloadpath, projectName, hostName), "/download");
        String localpath = FtpZkUtils.getProperty(String.format(this.localpath, projectName, hostName), "/localpath");
        String timeout = FtpZkUtils.getProperty(String.format(this.timeout, projectName, hostName), "10000");

        FtpInfos ftpInfos = new FtpInfos();
        ftpInfos.setHost(host);
        ftpInfos.setPort(Integer.parseInt(port));
        ftpInfos.setUsername(username);
        ftpInfos.setPassword(password);
        ftpInfos.setUploadPath(uploadpath);
        ftpInfos.setDownloadPath(downloadpath);
        ftpInfos.setLocalPath(localpath);
        ftpInfos.setTimeout(Integer.parseInt(timeout));
        return ftpInfos;
    }

    public FTPClient createFtpClient(FtpInfos ftpInfos) throws IOException {

        FTPClient ftpClient = new FTPClient();
        try {
            // 如下三行必须要，而且不能改变编码格式，否则不能下载中文文件
            ftpClient.setControlEncoding("GBK");
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
            conf.setServerLanguageCode("zh");

            ftpClient.connect(ftpInfos.getHost(), ftpInfos.getPort());
            ftpClient.login(ftpInfos.getUsername(), ftpInfos.getPassword());
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
                throw new FtpIOException("IP地址为" + ftpInfos.getHost() + "未连接到。用户名或密码错误");
            } else {
                LOGGER.info("FTP连接成功");
            }
        } catch (SocketException e) {
            throw new FtpIOException("FTP的IP[" + ftpInfos.getHost() + "]地址错误，请正确配置", e);
        } catch (IOException e) {
            throw new FtpIOException("FTP的端口[" + ftpInfos.getPort() + "]错误，请正确配置", e);
        }
        return ftpClient;
    }

}
