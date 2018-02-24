package com.ftpix.mmath.dao.mysql;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface DAO<T, R> {
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());


    String getCreateTableString();

    T getById(R id);

    List<T> getAll();

    R insert(T object);

    boolean update(T object);

    boolean deleteById(R id);


}
