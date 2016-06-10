package com.youarenotin.lib_orm_db.utils;

import com.youarenotin.lib_orm_db.extra.ColumnInfo;

/**
 * Created by lubo on 2016/6/10.
 * lubo_wen@126.com
 */
public class FieldUtils {
    public static final String OWNER = "com_m_common_owner";

    public static final String KEY = "com_m_common_key";

    public static final String CREATEAT = "com_m_common_createat";

    public static ColumnInfo getOwnerColumn() {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(OWNER);
        return column;
    }

    public static ColumnInfo getKeyColumn() {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(KEY);
        return column;
    }

    public static ColumnInfo getCreateAtColumn() {
        ColumnInfo column = new ColumnInfo();
        column.setColumnName(CREATEAT);
        return column;
    }
}
