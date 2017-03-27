package com.tsingye.mongo;

import com.mongodb.MongoClientURI;
import com.mongodb.WriteResult;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * simple test?
 * Created by tsingye on 16-12-20.
 */
public class MongoDaoTest {

    protected static MongoTemplate mongoTemplate;

    protected static MongoDao mongoDao;

    private static String mokeId = "000000000000000";

    private static String mockName = "zhangsan";

    private static int mockAge = 17;

    private static String mockRemark = "Here is a test entity";

    @BeforeClass
    public static void setUp() throws Exception {
        String uri = "mongodb://localhost:27017/anyTest";
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(clientURI);
        mongoTemplate = new MongoTemplate(mongoDbFactory);
        mongoDao = new MongoDao(mongoTemplate);
        mongoDao.getMongoTemplate().getDb().dropDatabase();
    }

    @Test
    public void testCRUD() {
        //save
        TestEntity objToSave = new TestEntity();
        objToSave.setId(mokeId);
        objToSave.setName(mockName);
        objToSave.setAge(mockAge);
        objToSave.setRemark(mockRemark);
        mongoDao.save(objToSave);
        //query
        Query query = new Query(Criteria.where("id").is(mokeId).and("name").is(mockName));
        TestEntity entity = mongoDao.getModel(query, TestEntity.class);
        Assert.assertEquals(mokeId, entity.getId());
        Assert.assertEquals(mockName, entity.getName());
        Assert.assertEquals(mockAge, entity.getAge());
        Assert.assertEquals(mockRemark, entity.getRemark());
        //update
        Update update = new Update().set("sex", false);
        mongoDao.updateFirst(query, update, TestEntity.class);
        Assert.assertEquals(false, mongoDao.getModel(query, TestEntity.class).isSex());
        //remove
        WriteResult result = mongoDao.remove(query, TestEntity.class);
        Assert.assertTrue(result.wasAcknowledged());
    }

    @AfterClass
    public static void tearDown() {
        mongoTemplate.getDb().dropDatabase();
    }

}

/**
 * next is a test entity
 */
@Document(collection = "testEntity")
class TestEntity {

    @Id
    private String id;

    private String name;

    private int age;

    private boolean sex;

    private String remark;

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getAge() {
        return age;
    }

    void setAge(int age) {
        this.age = age;
    }

    boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    String getRemark() {
        return remark;
    }

    void setRemark(String remark) {
        this.remark = remark;
    }
}
