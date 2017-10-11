package top.bingchenglin.easyframework.commons.ftp;


import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import top.bingchenglin.easyframework.commons.cfg.ConfigChangeSubscriber;
import top.bingchenglin.easyframework.commons.cfg.DynamicPropertiesHelperFactory;
import top.bingchenglin.easyframework.commons.cfg.ZkConfigChangeSubscriberImpl;
import top.bingchenglin.easyframework.commons.cfg.ZkUtils;

/**
 * @author sam
 * @version V1.0
 * @Title: com.asiainfo.gdm.jcoc.common.cfg.ZkPropertiesUtils
 * @Description: ZkPropertiesUtils
 * @date 2016/5/3 10:08
 */
public class FtpZkUtils {
    public static DynamicPropertiesHelperFactory helperFactory;
    private static String zkRootNode;
    private static String zkKey;
    private static int zkTimeout = 20000;
    private static String zkConfEncoding = "UTF-8";

    public static void init(String zkServers, String zkPath) {
        init(zkServers, zkPath, zkTimeout);
    }

    public static void init(String zkServers, String zkPath, int zkTimeout) {
        Validate.notBlank(zkServers, "入参%s不能为空", "zkServers");
        Validate.notBlank(zkPath, "入参%s不能为空", "zkPath");
        if(helperFactory == null) {
            synchronized (FtpZkUtils.class) {
                if(helperFactory == null) {
                    zkRootNode = zkPath.substring(0, zkPath.lastIndexOf("/"));
                    zkKey = zkPath.substring(zkPath.lastIndexOf("/"));

                    ZkSerializer zkSerializer = new ZkUtils.StringSerializer(zkConfEncoding);
                    ZkClient zkClient = new ZkClient(zkServers, zkTimeout);
                    zkClient.setZkSerializer(zkSerializer);
                    ConfigChangeSubscriber configChangeSubscriber = new ZkConfigChangeSubscriberImpl(zkClient, zkRootNode);
                    helperFactory = new DynamicPropertiesHelperFactory(configChangeSubscriber);
                }
            }
        }
    }

    public static String getProperty(String key) {
        return helperFactory.getHelper(zkKey).getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = helperFactory.getHelper(zkKey).getProperty(key);
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

}
