package com.ftpix.mmath.dao.mysql;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class DAO<T, R> {
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    @Autowired
    protected JdbcTemplate template;

    @Autowired
    private DataSource source;

    abstract void init();

    abstract T getById(R id);

    abstract List<T> getAll();

    abstract List<T> getBatch(int offset, int limiy);

    abstract R insert(T object);

    abstract boolean update(T object);

    abstract boolean deleteById(R id);

    protected DSLContext getDsl() {
        return DSL.using(source, SQLDialect.MYSQL);
    }


}
