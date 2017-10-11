package top.bingchenglin.easyframework.commons.ftp;


public class FtpInfos {
	
	private String host; //主机IP
	private int port;   //FTP端口
	private String username;  //用户名
	private String password;  //密码
	private String uploadPath;  //FTP服务器上传目录
	private String downloadPath; //FTP服务器下载目录
	private String localPath;   //本地目录
	private int timeout;

	public FtpInfos() {
	}

	public FtpInfos(String host, int port, String username, String password, String uploadPath, String downloadPath, String localPath) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.uploadPath = uploadPath;
		this.downloadPath = downloadPath;
		this.localPath = localPath;
	}

	public String getHost() {
		return host;
	}

	public FtpInfos setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	public FtpInfos setPort(int port) {
		this.port = port;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public FtpInfos setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public FtpInfos setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public FtpInfos setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
		return this;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public FtpInfos setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
		return this;
	}

	public String getLocalPath() {
		return localPath;
	}

	public FtpInfos setLocalPath(String localPath) {
		this.localPath = localPath;
		return this;
	}

	public int getTimeout() {
		return timeout;
	}

	public FtpInfos setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}
}
