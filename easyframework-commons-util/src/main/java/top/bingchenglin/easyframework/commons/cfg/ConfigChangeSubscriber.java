package top.bingchenglin.easyframework.commons.cfg;

import java.util.List;

/**
 * 配置改变的订阅者，在每一個zk文件上订阅一個监听器
 */
public interface ConfigChangeSubscriber {
    String getInitValue(String paramString);

    void subscribe(String key, ConfigChangeListener paramConfigChangeListener);

    List<String> listKeys();
}