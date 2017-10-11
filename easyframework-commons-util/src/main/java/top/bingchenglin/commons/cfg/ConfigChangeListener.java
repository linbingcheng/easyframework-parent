package top.bingchenglin.commons.cfg;

/**
 * 监听器，监听配置的改变
 */
public interface ConfigChangeListener {
	void configChanged(String key, String value);
}
