/*
 * This file is generated by jOOQ.
 */
package com.ftpix.mmath.dsl;


import com.ftpix.mmath.dsl.tables.Events;
import com.ftpix.mmath.dsl.tables.Fighters;
import com.ftpix.mmath.dsl.tables.Fights;
import com.ftpix.mmath.dsl.tables.FlywaySchemaHistory;
import com.ftpix.mmath.dsl.tables.HypeTrains;
import com.ftpix.mmath.dsl.tables.HypeTrainsStats;
import com.ftpix.mmath.dsl.tables.Organizations;
import com.ftpix.mmath.dsl.tables.StatsCategories;
import com.ftpix.mmath.dsl.tables.StatsEntries;
import com.ftpix.mmath.dsl.tables.records.EventsRecord;
import com.ftpix.mmath.dsl.tables.records.FightersRecord;
import com.ftpix.mmath.dsl.tables.records.FightsRecord;
import com.ftpix.mmath.dsl.tables.records.FlywaySchemaHistoryRecord;
import com.ftpix.mmath.dsl.tables.records.HypeTrainsRecord;
import com.ftpix.mmath.dsl.tables.records.HypeTrainsStatsRecord;
import com.ftpix.mmath.dsl.tables.records.OrganizationsRecord;
import com.ftpix.mmath.dsl.tables.records.StatsCategoriesRecord;
import com.ftpix.mmath.dsl.tables.records.StatsEntriesRecord;

import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>mmath</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<FightsRecord, Long> IDENTITY_FIGHTS = Identities0.IDENTITY_FIGHTS;
    public static final Identity<StatsEntriesRecord, Integer> IDENTITY_STATS_ENTRIES = Identities0.IDENTITY_STATS_ENTRIES;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<EventsRecord> KEY_EVENTS_PRIMARY = UniqueKeys0.KEY_EVENTS_PRIMARY;
    public static final UniqueKey<FightersRecord> KEY_FIGHTERS_PRIMARY = UniqueKeys0.KEY_FIGHTERS_PRIMARY;
    public static final UniqueKey<FightsRecord> KEY_FIGHTS_PRIMARY = UniqueKeys0.KEY_FIGHTS_PRIMARY;
    public static final UniqueKey<FightsRecord> KEY_FIGHTS_PRIMARY_KEY = UniqueKeys0.KEY_FIGHTS_PRIMARY_KEY;
    public static final UniqueKey<FlywaySchemaHistoryRecord> KEY_FLYWAY_SCHEMA_HISTORY_PRIMARY = UniqueKeys0.KEY_FLYWAY_SCHEMA_HISTORY_PRIMARY;
    public static final UniqueKey<HypeTrainsRecord> KEY_HYPE_TRAINS_PRIMARY = UniqueKeys0.KEY_HYPE_TRAINS_PRIMARY;
    public static final UniqueKey<HypeTrainsStatsRecord> KEY_HYPE_TRAINS_STATS_PRIMARY = UniqueKeys0.KEY_HYPE_TRAINS_STATS_PRIMARY;
    public static final UniqueKey<OrganizationsRecord> KEY_ORGANIZATIONS_PRIMARY = UniqueKeys0.KEY_ORGANIZATIONS_PRIMARY;
    public static final UniqueKey<StatsCategoriesRecord> KEY_STATS_CATEGORIES_PRIMARY = UniqueKeys0.KEY_STATS_CATEGORIES_PRIMARY;
    public static final UniqueKey<StatsEntriesRecord> KEY_STATS_ENTRIES_PRIMARY = UniqueKeys0.KEY_STATS_ENTRIES_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<FightsRecord, Long> IDENTITY_FIGHTS = Internal.createIdentity(Fights.FIGHTS, Fights.FIGHTS.ID);
        public static Identity<StatsEntriesRecord, Integer> IDENTITY_STATS_ENTRIES = Internal.createIdentity(StatsEntries.STATS_ENTRIES, StatsEntries.STATS_ENTRIES.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<EventsRecord> KEY_EVENTS_PRIMARY = Internal.createUniqueKey(Events.EVENTS, "KEY_events_PRIMARY", Events.EVENTS.SHERDOGURL);
        public static final UniqueKey<FightersRecord> KEY_FIGHTERS_PRIMARY = Internal.createUniqueKey(Fighters.FIGHTERS, "KEY_fighters_PRIMARY", Fighters.FIGHTERS.SHERDOGURL);
        public static final UniqueKey<FightsRecord> KEY_FIGHTS_PRIMARY = Internal.createUniqueKey(Fights.FIGHTS, "KEY_fights_PRIMARY", Fights.FIGHTS.ID);
        public static final UniqueKey<FightsRecord> KEY_FIGHTS_PRIMARY_KEY = Internal.createUniqueKey(Fights.FIGHTS, "KEY_fights_primary_key", Fights.FIGHTS.FIGHTER1_ID, Fights.FIGHTS.FIGHTER2_ID, Fights.FIGHTS.EVENT_ID);
        public static final UniqueKey<FlywaySchemaHistoryRecord> KEY_FLYWAY_SCHEMA_HISTORY_PRIMARY = Internal.createUniqueKey(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, "KEY_flyway_schema_history_PRIMARY", FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK);
        public static final UniqueKey<HypeTrainsRecord> KEY_HYPE_TRAINS_PRIMARY = Internal.createUniqueKey(HypeTrains.HYPE_TRAINS, "KEY_hype_trains_PRIMARY", HypeTrains.HYPE_TRAINS.USER, HypeTrains.HYPE_TRAINS.FIGHTER);
        public static final UniqueKey<HypeTrainsStatsRecord> KEY_HYPE_TRAINS_STATS_PRIMARY = Internal.createUniqueKey(HypeTrainsStats.HYPE_TRAINS_STATS, "KEY_hype_trains_stats_PRIMARY", HypeTrainsStats.HYPE_TRAINS_STATS.MONTH, HypeTrainsStats.HYPE_TRAINS_STATS.FIGHTER);
        public static final UniqueKey<OrganizationsRecord> KEY_ORGANIZATIONS_PRIMARY = Internal.createUniqueKey(Organizations.ORGANIZATIONS, "KEY_organizations_PRIMARY", Organizations.ORGANIZATIONS.SHERDOGURL);
        public static final UniqueKey<StatsCategoriesRecord> KEY_STATS_CATEGORIES_PRIMARY = Internal.createUniqueKey(StatsCategories.STATS_CATEGORIES, "KEY_stats_categories_PRIMARY", StatsCategories.STATS_CATEGORIES.ID);
        public static final UniqueKey<StatsEntriesRecord> KEY_STATS_ENTRIES_PRIMARY = Internal.createUniqueKey(StatsEntries.STATS_ENTRIES, "KEY_stats_entries_PRIMARY", StatsEntries.STATS_ENTRIES.ID);
    }
}
