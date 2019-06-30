/*
 * This file is generated by jOOQ.
 */
package com.ftpix.mmath.dsl.tables;


import com.ftpix.mmath.dsl.Indexes;
import com.ftpix.mmath.dsl.Keys;
import com.ftpix.mmath.dsl.Mmath;
import com.ftpix.mmath.dsl.tables.records.StatsEntriesRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
public class StatsEntries extends TableImpl<StatsEntriesRecord> {

    private static final long serialVersionUID = -512260144;

    /**
     * The reference instance of <code>mmath.stats_entries</code>
     */
    public static final StatsEntries STATS_ENTRIES = new StatsEntries();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StatsEntriesRecord> getRecordType() {
        return StatsEntriesRecord.class;
    }

    /**
     * The column <code>mmath.stats_entries.id</code>.
     */
    public final TableField<StatsEntriesRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>mmath.stats_entries.category_id</code>.
     */
    public final TableField<StatsEntriesRecord, String> CATEGORY_ID = createField("category_id", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.stats_entries.fighter_id</code>.
     */
    public final TableField<StatsEntriesRecord, String> FIGHTER_ID = createField("fighter_id", org.jooq.impl.SQLDataType.VARCHAR(999), this, "");

    /**
     * The column <code>mmath.stats_entries.percent</code>.
     */
    public final TableField<StatsEntriesRecord, Integer> PERCENT = createField("percent", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mmath.stats_entries.rank</code>.
     */
    public final TableField<StatsEntriesRecord, Integer> RANK = createField("rank", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mmath.stats_entries.text_to_show</code>.
     */
    public final TableField<StatsEntriesRecord, String> TEXT_TO_SHOW = createField("text_to_show", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.stats_entries.details</code>.
     */
    public final TableField<StatsEntriesRecord, String> DETAILS = createField("details", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>mmath.stats_entries.lastUpdate</code>.
     */
    public final TableField<StatsEntriesRecord, Timestamp> LASTUPDATE = createField("lastUpdate", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * Create a <code>mmath.stats_entries</code> table reference
     */
    public StatsEntries() {
        this(DSL.name("stats_entries"), null);
    }

    /**
     * Create an aliased <code>mmath.stats_entries</code> table reference
     */
    public StatsEntries(String alias) {
        this(DSL.name(alias), STATS_ENTRIES);
    }

    /**
     * Create an aliased <code>mmath.stats_entries</code> table reference
     */
    public StatsEntries(Name alias) {
        this(alias, STATS_ENTRIES);
    }

    private StatsEntries(Name alias, Table<StatsEntriesRecord> aliased) {
        this(alias, aliased, null);
    }

    private StatsEntries(Name alias, Table<StatsEntriesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> StatsEntries(Table<O> child, ForeignKey<O, StatsEntriesRecord> key) {
        super(child, key, STATS_ENTRIES);
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
        return Arrays.<Index>asList(Indexes.STATS_ENTRIES_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<StatsEntriesRecord, Integer> getIdentity() {
        return Keys.IDENTITY_STATS_ENTRIES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<StatsEntriesRecord> getPrimaryKey() {
        return Keys.KEY_STATS_ENTRIES_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<StatsEntriesRecord>> getKeys() {
        return Arrays.<UniqueKey<StatsEntriesRecord>>asList(Keys.KEY_STATS_ENTRIES_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsEntries as(String alias) {
        return new StatsEntries(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsEntries as(Name alias) {
        return new StatsEntries(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public StatsEntries rename(String name) {
        return new StatsEntries(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public StatsEntries rename(Name name) {
        return new StatsEntries(name, null);
    }
}