package com.alphacreators.noteguardian.NOTEUTILITY;

import androidx.room.TypeConverter;

import com.alphacreators.noteguardian.PRIORITY.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class TypeConvertor {

    @TypeConverter
    public static Date fromTimeStamp(Long value){
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimeStamp(Date value){
        return value == null ? null : value.getTime();
    }

    @TypeConverter
    public static String fromPriority(Priority priority){
        return priority == null ? null : priority.name();
    }

    @TypeConverter
    public static Priority toPriority(String priority){
        return priority == null ? null : Priority.valueOf(priority);
    }

    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    @TypeConverter
    public static String fromDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalDateTime fromRemainderString(String value){
        return value == null ? null : LocalDateTime.parse(value);
    }

    @TypeConverter
    public static String fromRemainderDate(LocalDateTime localDateTime){
        return localDateTime == null ? null : localDateTime.toString();
    }

}
