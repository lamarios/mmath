package com.ftpix.mmath.model.persisters;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.DateTimeType;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimePersister extends DateTimeType {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final  ZonedDateTimePersister singleTon = new ZonedDateTimePersister();

    protected ZonedDateTimePersister() {
        super(SqlType.DATE, new Class<?>[]{ZonedDateTime.class});
    }


    public static ZonedDateTimePersister getSingleton() {
        return singleTon;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        ZonedDateTime dateTime = (ZonedDateTime) javaObject;
        if (dateTime != null) {
            return formatter.format(dateTime);
        } else {
            return null;
        }
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        return formatter.parse((CharSequence) sqlArg);
    }
}
