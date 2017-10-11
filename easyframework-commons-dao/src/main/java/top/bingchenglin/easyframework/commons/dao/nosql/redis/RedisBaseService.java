package top.bingchenglin.easyframework.easyframwork.commons.dao.nosql.redis;


import top.bingchenglin.easyframework.easyframwork.commons.dao.param.DBQueryParam;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisBaseService<T, ID extends Serializable> {

	public void  save(ID id, T entity);

	public void  save(Map<ID, T> entitys);

	public void  save(ID id, T entity, long timeout, TimeUnit unit);

	public void  save(Map<ID, T> entitys, long timeout, TimeUnit unit);

	public void delete(ID id);

	public void delete(Iterable<ID> ids);

	public T findOne(ID id);

	public T findOne(ID id, DBQueryParam filterQueryParam) throws Exception;

	public List<T> findAll(Iterable<ID> ids);

	public List<T> findAll(Iterable<ID> ids, DBQueryParam filterQueryParam) throws Exception;

	public List<T> findAll(String idPattern);

	public List<T> findAll(String idPattern, DBQueryParam filterQueryParam) throws Exception;
}
