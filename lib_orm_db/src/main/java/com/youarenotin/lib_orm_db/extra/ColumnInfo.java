package com.youarenotin.lib_orm_db.extra;

import java.lang.reflect.Field;

/**
 * Created by lubo on 2016/6/9.
 * lubo_wen@126.com
 */
public class ColumnInfo {
    private String dataType;
    private String columnName;
    private Field field;
    private String columnType;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
}
