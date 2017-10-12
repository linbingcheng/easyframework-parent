package top.bingchenglin.easyframework.commons.dao.nosql.mongodb;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

@NoRepositoryBean
public interface MongodbBaseOpt<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
}
