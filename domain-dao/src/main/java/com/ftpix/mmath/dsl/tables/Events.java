/*
 * This file is generated by jOOQ.
 */
package com.ftpix.mmath.dsl.tables;


import com.ftpix.mmath.dsl.Indexes;
import com.ftpix.mmath.dsl.Keys;
import com.ftpix.mmath.dsl.Mmath;
import com.ftpix.mmath.dsl.tables.records.EventsRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
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
public class Events extends TableImpl<EventsRecord> {

    private static final long serialVersionUID = -601422901;

    /**
     * The reference instance of <code>mmath.events</code>
     */
    public static final Events EVENTS = new Events();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EventsRecord> getRecordType() {
        return EventsRecord.class;
    }

    /**
     * The column <code>mmath.events.sherdogUrl</code>.
     */
    public final TableField<EventsRecord, String> SHERDOGURL = createField("sherdogUrl", org.jooq.impl.SQLDataType.VARCHAR(1000).nullable(false), this, "");

    /**
     * The column <code>mmath.events.date</code>.
     */
    public final TableField<EventsRecord, Timestamp> DATE = createField("date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>mmath.events.organization_id</code>.
     */
    public final TableField<EventsRecord, String> ORGANIZATION_ID = createField("organization_id", org.jooq.impl.SQLDataType.VARCHAR(1000), this, "");

    /**
     * The column <code>mmath.events.name</code>.
     */
    public final TableField<EventsRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.events.location</code>.
     */
    public final TableField<EventsRecord, String> LOCATION = createField("location", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.events.lastUpdate</code>.
     */
    public final TableField<EventsRecord, Timestamp> LASTUPDATE = createField("lastUpdate", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * Create a <code>mmath.events</code> table reference
     */
    public Events() {
        this(DSL.name("events"), null);
    }

    /**
     * Create an aliased <code>mmath.events</code> table reference
     */
    public Events(String alias) {
        this(DSL.name(alias), EVENTS);
    }

    /**
     * Create an aliased <code>mmath.events</code> table reference
     */
    public Events(Name alias) {
        this(alias, EVENTS);
    }

    private Events(Name alias, Table<EventsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Events(Name alias, Table<EventsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Events(Table<O> child, ForeignKey<O, EventsRecord> key) {
        super(child, key, EVENTS);
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
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.EVENTS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<EventsRecord> getPrimaryKey() {
        return Keys.KEY_EVENTS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<EventsRecord>> getKeys() {
        return Arrays.<UniqueKey<EventsRecord>>asList(Keys.KEY_EVENTS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Events as(String alias) {
        return new Events(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Events as(Name alias) {
        return new Events(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Events rename(String name) {
        return new Events(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Events rename(Name name) {
        return new Events(name, null);
    }
}
