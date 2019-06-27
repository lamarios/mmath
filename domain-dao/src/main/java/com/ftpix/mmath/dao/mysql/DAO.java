package com.ftpix.mmath.dao.mysql;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class DAO<T, R> {
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());


    abstract void init();

    abstract T getById(R id);

    abstract List<T> getAll();

    abstract List<T> getBatch(int offset, int limiy);

    abstract R insert(T object);

    abstract boolean update(T object);

    abstract boolean deleteById(R id);


}
