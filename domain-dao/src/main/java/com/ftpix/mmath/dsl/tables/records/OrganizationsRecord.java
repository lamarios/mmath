/*
 * This file is generated by jOOQ.
 */
package com.ftpix.mmath.dsl.tables.records;


import com.ftpix.mmath.dsl.tables.Organizations;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


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
public class OrganizationsRecord extends UpdatableRecordImpl<OrganizationsRecord> implements Record3<String, Timestamp, String> {

    private static final long serialVersionUID = -1196066474;

    /**
     * Setter for <code>mmath.organizations.sherdogUrl</code>.
     */
    public void setSherdogurl(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>mmath.organizations.sherdogUrl</code>.
     */
    public String getSherdogurl() {
        return (String) get(0);
    }

    /**
     * Setter for <code>mmath.organizations.lastUpdate</code>.
     */
    public void setLastupdate(Timestamp value) {
        set(1, value);
    }

    /**
     * Getter for <code>mmath.organizations.lastUpdate</code>.
     */
    public Timestamp getLastupdate() {
        return (Timestamp) get(1);
    }

    /**
     * Setter for <code>mmath.organizations.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>mmath.organizations.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, Timestamp, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<String, Timestamp, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Organizations.ORGANIZATIONS.SHERDOGURL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field2() {
        return Organizations.ORGANIZATIONS.LASTUPDATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Organizations.ORGANIZATIONS.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getSherdogurl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component2() {
        return getLastupdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getSherdogurl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value2() {
        return getLastupdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationsRecord value1(String value) {
        setSherdogurl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationsRecord value2(Timestamp value) {
        setLastupdate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationsRecord value3(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationsRecord values(String value1, Timestamp value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OrganizationsRecord
     */
    public OrganizationsRecord() {
        super(Organizations.ORGANIZATIONS);
    }

    /**
     * Create a detached, initialised OrganizationsRecord
     */
    public OrganizationsRecord(String sherdogurl, Timestamp lastupdate, String name) {
        super(Organizations.ORGANIZATIONS);

        set(0, sherdogurl);
        set(1, lastupdate);
        set(2, name);
    }
}
