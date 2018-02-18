package com.ftpix.mmath.model.persisters;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.DateTimeType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDatePersister extends DateTimeType {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final LocalDatePersister singleTon = new LocalDatePersister();

    protected LocalDatePersister() {
        super(SqlType.DATE, new Class<?>[]{LocalDate.class});
    }


    public static LocalDatePersister getSingleton() {
        return singleTon;
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        LocalDate dateTime = (LocalDate) javaObject;
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
