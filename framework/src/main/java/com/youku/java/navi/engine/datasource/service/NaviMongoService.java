package com.youku.java.navi.engine.datasource.service;

import com.mongodb.WriteResult;
import com.youku.java.navi.common.NaviError;
import com.youku.java.navi.common.exception.NaviSystemException;
import com.youku.java.navi.engine.core.INaviDB;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;

public class NaviMongoService extends AbstractNaviDataService implements INaviDB {

    @Setter @Getter
    private String databaseNm;
    private MongoTemplate mongoTempt;

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (databaseNm == null) {
            throw new NaviSystemException("dataSource or databaseNm is null", NaviError.SYSERROR);
        }

        mongoTempt = new NaviMongoTemplateFactory(dataSource).getMongoTemplate(databaseNm);
    }

    public <T> void insert(T t) {
        mongoTempt.insert(t);
    }

    public <T> void delete(T object) {
        mongoTempt.remove(object);
    }

    public void delete(Query query, String tableNm) {
        mongoTempt.remove(query, tableNm);
    }

    public <T> void delete(Query query, Class<T> entityClass) {
        mongoTempt.remove(query, entityClass);
    }

    public <T> T findOne(Query query, Class<T> entityClass) {
        return mongoTempt.findOne(query, entityClass);
    }

    public <T> List<T> find(Query query, Class<T> entityClass) {
        return mongoTempt.find(query, entityClass);
    }

    public <T> T findById(Object idObj, Class<T> entityClass) {
        return mongoTempt.findById(idObj, entityClass);
    }

    public <T> boolean upsert(Query query, Update update, Class<T> entityClass) {
        WriteResult wr = mongoTempt.upsert(query, update, entityClass);
        return wr.getN() > 0;
    }

    public <T> boolean updateFirst(Query query, Update update, Class<T> entityClass) {
        WriteResult wr = mongoTempt.updateFirst(query, update, entityClass);
        return wr.getN() > 0;
    }

    public <T> int updateMulti(Query query, Update update, Class<T> entityClass) {
        WriteResult wr = mongoTempt.updateMulti(query, update, entityClass);
        return wr.getN();
    }

    public <T> long count(Query query, Class<T> entityClass) {
        return mongoTempt.count(query, entityClass);
    }

    public <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
        return mongoTempt.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), entityClass);
    }

    public <T> T findAndRemove(Query query, Class<T> entityClass) {
        return mongoTempt.findAndRemove(query, entityClass);
    }

    public <T> void save(T t) {
        mongoTempt.save(t);
    }

    public <T> void insertAll(Collection<T> objectsToSave) {
        mongoTempt.insertAll(objectsToSave);
    }

    public <T> MongoPersistentProperty getIdProperties(Class<T> classNm) {
        return mongoTempt.getConverter().getMappingContext().getPersistentEntity(classNm).getIdProperty();
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return mongoTempt.findAll(entityClass);
    }

}
