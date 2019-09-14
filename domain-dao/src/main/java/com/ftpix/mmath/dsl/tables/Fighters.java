/*
 * This file is generated by jOOQ.
 */
package com.ftpix.mmath.dsl.tables;


import com.ftpix.mmath.dsl.Indexes;
import com.ftpix.mmath.dsl.Keys;
import com.ftpix.mmath.dsl.Mmath;
import com.ftpix.mmath.dsl.tables.records.FightersRecord;

import java.sql.Date;
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
public class Fighters extends TableImpl<FightersRecord> {

    private static final long serialVersionUID = -1362228772;

    /**
     * The reference instance of <code>mmath.fighters</code>
     */
    public static final Fighters FIGHTERS = new Fighters();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FightersRecord> getRecordType() {
        return FightersRecord.class;
    }

    /**
     * The column <code>mmath.fighters.sherdogUrl</code>.
     */
    public final TableField<FightersRecord, String> SHERDOGURL = createField("sherdogUrl", org.jooq.impl.SQLDataType.VARCHAR(1000).nullable(false), this, "");

    /**
     * The column <code>mmath.fighters.lastUpdate</code>.
     */
    public final TableField<FightersRecord, Timestamp> LASTUPDATE = createField("lastUpdate", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>mmath.fighters.name</code>.
     */
    public final TableField<FightersRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.fighters.birthday</code>.
     */
    public final TableField<FightersRecord, Date> BIRTHDAY = createField("birthday", org.jooq.impl.SQLDataType.DATE, this, "");

    /**
     * The column <code>mmath.fighters.draws</code>.
     */
    public final TableField<FightersRecord, Integer> DRAWS = createField("draws", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mmath.fighters.losses</code>.
     */
    public final TableField<FightersRecord, Integer> LOSSES = createField("losses", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mmath.fighters.wins</code>.
     */
    public final TableField<FightersRecord, Integer> WINS = createField("wins", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mmath.fighters.weight</code>.
     */
    public final TableField<FightersRecord, String> WEIGHT = createField("weight", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.fighters.height</code>.
     */
    public final TableField<FightersRecord, String> HEIGHT = createField("height", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.fighters.nickname</code>.
     */
    public final TableField<FightersRecord, String> NICKNAME = createField("nickname", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>mmath.fighters.nc</code>.
     */
    public final TableField<FightersRecord, Integer> NC = createField("nc", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>mmath.fighters.search_rank</code>.
     */
    public final TableField<FightersRecord, Integer> SEARCH_RANK = createField("search_rank", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("999999", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mmath.fighters.winKo</code>.
     */
    public final TableField<FightersRecord, Integer> WINKO = createField("winKo", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mmath.fighters.winSub</code>.
     */
    public final TableField<FightersRecord, Integer> WINSUB = createField("winSub", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mmath.fighters.winDec</code>.
     */
    public final TableField<FightersRecord, Integer> WINDEC = createField("winDec", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mmath.fighters.lossKo</code>.
     */
    public final TableField<FightersRecord, Integer> LOSSKO = createField("lossKo", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mmath.fighters.lossSub</code>.
     */
    public final TableField<FightersRecord, Integer> LOSSSUB = createField("lossSub", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>mmath.fighters.lossDec</code>.
     */
    public final TableField<FightersRecord, Integer> LOSSDEC = createField("lossDec", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * Create a <code>mmath.fighters</code> table reference
     */
    public Fighters() {
        this(DSL.name("fighters"), null);
    }

    /**
     * Create an aliased <code>mmath.fighters</code> table reference
     */
    public Fighters(String alias) {
        this(DSL.name(alias), FIGHTERS);
    }

    /**
     * Create an aliased <code>mmath.fighters</code> table reference
     */
    public Fighters(Name alias) {
        this(alias, FIGHTERS);
    }

    private Fighters(Name alias, Table<FightersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Fighters(Name alias, Table<FightersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Fighters(Table<O> child, ForeignKey<O, FightersRecord> key) {
        super(child, key, FIGHTERS);
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
        return Arrays.<Index>asList(Indexes.FIGHTERS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<FightersRecord> getPrimaryKey() {
        return Keys.KEY_FIGHTERS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<FightersRecord>> getKeys() {
        return Arrays.<UniqueKey<FightersRecord>>asList(Keys.KEY_FIGHTERS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fighters as(String alias) {
        return new Fighters(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fighters as(Name alias) {
        return new Fighters(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Fighters rename(String name) {
        return new Fighters(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Fighters rename(Name name) {
        return new Fighters(name, null);
    }
}
