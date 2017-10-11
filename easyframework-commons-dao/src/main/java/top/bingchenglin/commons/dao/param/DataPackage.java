package top.bingchenglin.commons.dao.param;


import top.bingchenglin.commons.dao.util.EntityUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataPackage implements Serializable {
    private DataMetadata meta;
    private List data;

    public DataMetadata getMeta() {
        return meta;
    }

    public void setMeta(DataMetadata meta) {
        this.meta = meta;
    }

    public void setMeta(long total, int page, int limit) {
        this.meta = new DataMetadata(total, page, limit);
    }

    public List getData() {
        return data;
    }

    public <T> List<T> getData(Class<T> cl) throws IllegalAccessException, InstantiationException {
        List<T> ts = new ArrayList<T>();
        for (Object o : data) {
            T t = cl.newInstance();
            EntityUtil.copyProperties(t, o);
            ts.add(t);
        }
        return ts;
    }

    public void setData(List data) {
        this.data = data;
    }

    @JsonIgnore
    public long getTotal() {
        return meta != null ? meta.getTotal() : 0L;
    }

    @JsonIgnore
    public int getTotalPages() {
        if (meta == null) {
            return 1;
        }
        return getTotalPages(meta.getTotal(), meta.getLimit());
    }

    public int getTotalPages(long total, int limit) {
        if (limit <= 0 || total <= 0L) {
            return 1;
        }
        return (int) Math.ceil((double) total / limit);
    }
}
