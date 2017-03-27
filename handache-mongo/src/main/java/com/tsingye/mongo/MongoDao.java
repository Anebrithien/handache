package com.tsingye.mongo;

import com.google.common.base.Strings;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.CloseableIterator;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Newly coded mongoDao, need Spring-data-mongodb
 * Created by tsingye on 16-12-20.
 */
@Component("mongoDao")
public class MongoDao {

    private final MongoTemplate mongoTemplate;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public MongoDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        logger.info("initialized mongoDao with mongoTemplate {}", mongoTemplate);
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    //====== methods below ======//

    public <T> T getModel(Object id, Class<T> clazz) {
        return mongoTemplate.findById(id, clazz);
    }

    public <T> T getModel(Query query, Class<T> clazz) {
        return mongoTemplate.findOne(query, clazz);
    }

    public <T> T getModel(String idKey, Object id, Class<T> clazz) {
        Query query = Query.query(Criteria.where(idKey).is(id));
        return mongoTemplate.findOne(query, clazz);
    }

    public <T, U> U getModel(String idKey, Object id, Function<T, U> converter, Class<T> clazz, String... fields) {
        Criteria criteria = Criteria.where(idKey).is(id);
        Query query = Query.query(criteria);
        if (fields != null && fields.length > 0) {
            Stream.of(fields)
                  .filter(s -> !Strings.isNullOrEmpty(s))
                  .forEach(s -> query.fields().include(s));
        }
        return converter.apply(getModel(query, clazz));
    }

    public <T, U> U getModel(Object id, Function<T, U> converter, Class<T> clazz, String... fields) {
        return getModel("id", id, converter, clazz, fields);
    }

    public long count(Query query, Class<?> clazz) {
        return mongoTemplate.count(query, clazz);
    }

    public boolean exists(Query query, Class<?> clazz) {
        return mongoTemplate.exists(query, clazz);
    }

    public <T> Stream<T> stream(Query query, boolean parallel, Class<T> clazz) {
        return buildStream2(query, parallel, clazz);
    }

    /**
     * This geo query works, but it has some limits. I would like I or someone could figure it.
     *
     * @param nearQuery the near query object, you must specify the {@link org.springframework.data.geo.Metrics}
     * @param parallel  if {@code true} then the returned stream is a parallel
     *                  stream; if {@code false} the returned stream is a sequential
     *                  stream.
     * @param clazz     the entityType you queried
     * @param <T>       return type
     * @return a new sequential or parallel {@code Stream}, the {@link GeoResult} has {@link org.springframework.data.geo.Distance} attribute
     */
    public <T> Stream<GeoResult<T>> streamNear(NearQuery nearQuery, boolean parallel, Class<T> clazz) {
        return StreamSupport.stream(mongoTemplate.geoNear(nearQuery, clazz).spliterator(), parallel);
    }

    /**
     * Well, works like a legency. We need this for using JDK8 {@link Stream}
     *
     * @param query    what and how do you want to query
     * @param parallel if {@code true} then the returned stream is a parallel
     *                 stream; if {@code false} the returned stream is a sequential
     *                 stream.
     * @param clazz    the entityType you queried
     * @param <T>      return type
     * @return a new sequential or parallel {@code Stream}
     */
    @Deprecated
    private <T> Stream<T> buildStream1(Query query, boolean parallel, Class<T> clazz) {
        DBCursor cursor = mongoTemplate.execute(clazz,
                collection -> collection.find(query.getQueryObject(), query.getFieldsObject()));
        cursor = prepareCursor(cursor, query);
        MongoConverter converter = mongoTemplate.getConverter();
        return StreamSupport.stream(cursor.spliterator(), parallel)
                            .map(o -> {
                                try {
                                    return converter.read(clazz, o);
                                } catch (Exception e) {
                                    logger.warn("Convert DBObject error {}", e);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull);
    }

    private DBCursor prepareCursor(DBCursor cursor, Query query) {
        if (query == null) {
            return cursor;
        }
        if (query.getSkip() <= 0
                && query.getLimit() <= 0
                && query.getSortObject() == null
                && !StringUtils.hasText(query.getHint())
                && !query.getMeta().hasValues()) {
            return cursor;
        }
        DBCursor cursorToUse = cursor.copy();
        if (query.getSkip() > 0) {
            cursorToUse = cursorToUse.skip(query.getSkip());
        }
        if (query.getLimit() > 0) {
            cursorToUse = cursorToUse.limit(query.getLimit());
        }
        if (query.getSortObject() != null) {
            cursorToUse = cursorToUse.sort(query.getSortObject());
        }
        if (StringUtils.hasText(query.getHint())) {
            cursorToUse = cursorToUse.hint(query.getHint());
        }
        if (query.getMeta().hasValues()) {
            for (Map.Entry<String, Object> entry : query.getMeta().values()) {
                cursorToUse = cursorToUse.addSpecial(entry.getKey(), entry.getValue());
            }
        }
        return cursorToUse;
    }

    /**
     * <p>I get some ideas from {@link StreamUtils#createStreamFromIterator(Iterator)}, so here this method
     * comes. I think it must be better.
     * <p>Try to use this one instead of {@link MongoDao#buildStream1(Query, boolean, Class)} now.
     *
     * @param query    what and how do you want to query
     * @param parallel if {@code true} then the returned stream is a parallel
     *                 stream; if {@code false} the returned stream is a sequential
     *                 stream.
     * @param clazz    the entityType you queried
     * @param <T>      return type
     * @return a new sequential or parallel {@code Stream}
     */
    private <T> Stream<T> buildStream2(Query query, boolean parallel, Class<T> clazz) {
        CloseableIterator<T> iterator = mongoTemplate.stream(query, clazz);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL), parallel)
                            .onClose(iterator::close);
    }

    public void updateFirst(Query query, Update update, Class<?> clazz) {
        mongoTemplate.updateFirst(query, update, clazz);
    }

    public void updateMulti(Query query, Update update, Class<?> clazz) {
        mongoTemplate.updateMulti(query, update, clazz);
    }

    public void upsert(Query query, Update update, Class<?> clazz) {
        mongoTemplate.upsert(query, update, clazz);
    }

    public void save(Object objectToSave) {
        mongoTemplate.save(objectToSave);
    }

    public void insert(Collection<?> batchToSave, Class<?> clazz) {
        mongoTemplate.insert(batchToSave, clazz);
    }

    public DBCollection getCollection(Class<?> clazz) {
        String collectionName = mongoTemplate.getCollectionName(clazz);
        return mongoTemplate.getCollection(collectionName);
    }

    public WriteResult remove(Object objectToRemove) {
        return mongoTemplate.remove(objectToRemove);
    }

    public WriteResult remove(Query query, Class<?> clazz) {
        return mongoTemplate.remove(query, clazz);
    }

    public List<?> distinct(String fieldName, Class<?> clazz) {
        return getCollection(clazz).distinct(fieldName);
    }

    public List<?> distinct(String fieldName, DBObject query, Class<?> clazz) {
        DBCollection collection = getCollection(clazz);
        return collection.distinct(fieldName, query);
    }

    public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) {
        return mongoTemplate.aggregate(aggregation, inputType, outputType);
    }

    public DBObject getDBObject(Query query, Class<?> clazz) {
        return mongoTemplate.execute(clazz,
                collection -> collection.findOne(query.getQueryObject(), query.getFieldsObject()));
    }

}
