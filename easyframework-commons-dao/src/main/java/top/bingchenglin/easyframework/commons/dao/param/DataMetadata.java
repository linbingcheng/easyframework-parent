package top.bingchenglin.easyframework.commons.dao.param;

import java.io.Serializable;

public class DataMetadata implements Serializable {
    private long total;
    private int page;
    private int limit;

    public DataMetadata() {
    }

    public DataMetadata(long total, int page, int limit) {
        this.total = total;
        this.page = page;
        this.limit = limit;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
