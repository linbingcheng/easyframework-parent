package top.bingchenglin.easyframework.commons.cfg;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 动态配置文件辅助类
 */
public class DynamicPropertiesHelper {
    private ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();
    private ConcurrentHashMap<String, List<PropertyChangeListener>> propListeners = new ConcurrentHashMap<String, List<PropertyChangeListener>>();

    public DynamicPropertiesHelper(String initValue) {
        Properties props = parse(initValue);
        for (Map.Entry<Object, Object> propEn : props.entrySet()) {
            this.properties.put((String) propEn.getKey(), (String) propEn.getValue());
        }
    }

    private Properties parse(String value) {
        Properties props = new Properties();
        if (!StringUtils.isEmpty(value)) {
            try {
                props.load(new StringReader(value));
            } catch (IOException localIOException) {
            }
        }
        return props;
    }

    public synchronized void refresh(String propertiesAsStr) {
        Properties props = parse(propertiesAsStr);
        for (Map.Entry<Object, Object> propEn : props.entrySet()) {
            setValue((String) propEn.getKey(), (String) propEn.getValue());
        }
    }

    private void setValue(String key, String newValue) {
        String oldValue = this.properties.get(key);
        this.properties.put(key, newValue);
//        if (!Objects.equals(oldValue, newValue)) {
//            firePropertyChanged(key, oldValue, newValue);
//        }
        if (ObjectUtils.notEqual(oldValue, newValue)) {
            firePropertyChanged(key, oldValue, newValue);
        }
    }

    public boolean containsProperty(String key) {
        return this.properties.containsKey(key);
    }

    public String getProperty(String key) {
        return this.properties.get(key);
    }

    public String getProperty(String key, String defaultValue) {
        if (!containsProperty(key) || this.properties.get(key) == null) {
            return defaultValue;
        }
        return this.properties.get(key);
    }

    public Boolean getBooleanProperty(String key, Boolean defaultValue) {
        if (!containsProperty(key) || this.properties.get(key) == null) {
            return defaultValue;
        }
        return Boolean.valueOf(this.properties.get(key));
    }

    public Integer getIntProperty(String key, Integer defaultValue) {
        Integer retValue = defaultValue;
        try {
            retValue = Integer.parseInt(this.properties.get(key));
        } catch (NumberFormatException e) {
        }
        return retValue;
    }

    public Long getLongProperty(String key, Long defaultValue) {
        Long retValue = defaultValue;
        try {
            retValue = Long.parseLong(this.properties.get(key));
        } catch (NumberFormatException e) {
        }
        return retValue;
    }

    public Double getDoubleProperty(String key, Double defaultValue) {
        Double retValue = defaultValue;
        try {
            retValue = Double.parseDouble(this.properties.get(key));
        } catch (NumberFormatException e) {
        }
        return retValue;
    }

    public Enumeration<String> getPropertyKeys() {
        return this.properties.keys();
    }

    /**
     * @param key      listener名字
     * @param listener
     */
    public void registerListener(String key, PropertyChangeListener listener) {
        List<PropertyChangeListener> listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
        List<PropertyChangeListener> old = this.propListeners.putIfAbsent(key, listeners);
        if (old != null) {
            listeners = old;
        }
        listeners.add(listener);
    }

    private void firePropertyChanged(String key, String oldValue, String newValue) {
        List<PropertyChangeListener> listeners = this.propListeners.get(key);
        if ((listeners == null) || (listeners.size() == 0)) {
            return;
        }
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChanged(oldValue, newValue);
        }
    }

    public static interface PropertyChangeListener {
        void propertyChanged(String oldValue, String newValue);
    }
}