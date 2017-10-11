package bingchenglin.top.commons.dao.db.router;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DSRoutingHolder.getDSFlag();
    }
}
