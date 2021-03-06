/*
 * This file is generated by jOOQ.
 */
package com.ftpix.mmath.dsl.tables;


import com.ftpix.mmath.dsl.Mmath;
import com.ftpix.mmath.dsl.tables.records.JdbcTestRecord;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JdbcTest extends TableImpl<JdbcTestRecord> {

    private static final long serialVersionUID = 1614230423;

    /**
     * The reference instance of <code>mmath.jdbc_test</code>
     */
    public static final JdbcTest JDBC_TEST = new JdbcTest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<JdbcTestRecord> getRecordType() {
        return JdbcTestRecord.class;
    }

    /**
     * The column <code>mmath.jdbc_test.a</code>.
     */
    public final TableField<JdbcTestRecord, String> A = createField("a", org.jooq.impl.SQLDataType.CHAR(1), this, "");

    /**
     * Create a <code>mmath.jdbc_test</code> table reference
     */
    public JdbcTest() {
        this(DSL.name("jdbc_test"), null);
    }

    /**
     * Create an aliased <code>mmath.jdbc_test</code> table reference
     */
    public JdbcTest(String alias) {
        this(DSL.name(alias), JDBC_TEST);
    }

    /**
     * Create an aliased <code>mmath.jdbc_test</code> table reference
     */
    public JdbcTest(Name alias) {
        this(alias, JDBC_TEST);
    }

    private JdbcTest(Name alias, Table<JdbcTestRecord> aliased) {
        this(alias, aliased, null);
    }

    private JdbcTest(Name alias, Table<JdbcTestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> JdbcTest(Table<O> child, ForeignKey<O, JdbcTestRecord> key) {
        super(child, key, JDBC_TEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Mmath.MMATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JdbcTest as(String alias) {
        return new JdbcTest(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JdbcTest as(Name alias) {
        return new JdbcTest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public JdbcTest rename(String name) {
        return new JdbcTest(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public JdbcTest rename(Name name) {
        return new JdbcTest(name, null);
    }
}
