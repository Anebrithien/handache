package com.tsingye.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by tsingye on 16-6-21.
 */
public class JsonData extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 3727722387481308026L;

    public JsonData() {
    }

    public JsonData(final int size) {
        super(size);
    }

    public JsonData(String key, Object value) {
        put(key, value);
    }

    public static JsonData of(String key, Object value) {
        return new JsonData(key, value);
    }

    /**
     * Creates a DBObject from a map.
     *
     * @param m
     */
    @SuppressWarnings("unchecked")
    public JsonData(Map m) {
        super(m);
    }

    public Map<String, Object> toMap() {
        return new LinkedHashMap<>(this);
    }

    public JsonData removeField(String key) {
        remove(key);
        return this;
    }

    public boolean containsField(String field) {
        return super.containsKey(field);
    }

    public <T> T get(String key) {
        //noinspection unchecked
        return (T)super.get(key);
    }

    public Object get(String key, Object defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : o;
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : (Boolean) o;
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : (Integer) o;
    }

    public Long getLong(String key, Long defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : (Long) o;
    }

    public Double getDouble(String key, Double defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : (Double) o;
    }

    public String getString(String key, String defaultValue) {
        Object o = get(key);
        return o == null ? defaultValue : o.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putAll(final Map m) {
        for (Map.Entry entry : (Set<Map.Entry>) m.entrySet()) {
            put(entry.getKey().toString(), entry.getValue());
        }
    }

    public void putAll(final JsonData o) {
        if (o == null) {
            return;
        }
        for (String k : o.keySet()) {
            put(k, o.get(k));
        }
    }

    public JsonData append(String key, Object val) {
        put(key, val);
        return this;
    }
}
