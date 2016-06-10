package com.youarenotin.lib_orm_db.utils;

import com.youarenotin.lib_orm_db.SqliteUtility;
import com.youarenotin.lib_orm_db.extra.AutoIncrementColumnInfo;
import com.youarenotin.lib_orm_db.extra.ColumnInfo;
import com.youarenotin.lib_orm_db.extra.Extra;
import com.youarenotin.lib_orm_db.extra.TableInfo;

/**
 * Created by lubo on 2016/6/10.
 * lubo_wen@126.com
 */
public class SqlUtils {
    public static String getCreateTableSql(TableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        ColumnInfo primaryKey = tableInfo.getPrimaryKey();
        sb.append(" CREATE TABLE IF NOT EXISTS ");
        sb.append(tableInfo.getTableName());
        sb.append(" ( ");
        //添加主键sql
        if (primaryKey instanceof AutoIncrementColumnInfo) {//两种列AutoIncrementColumnInfo和ColumnInfo
            sb.append(" ")
                    .append(primaryKey.getColumnName())
                    .append(" ")
                    .append("INTEGER PRIMARY KEY AUTOINCREMENT , ");
        } else {
            sb.append(" ")
                    .append(primaryKey.getColumnName())
                    .append(" ").append(primaryKey.getColumnType())
                    .append(" ").append("NOT NULL ,");
        }
        //添加常规列sql
        for (ColumnInfo c : tableInfo.getColumns()){
            sb.append(" ")
                    .append(c.getColumnName())
                    .append(" ").append(c.getColumnType())
                    .append(" , ");
        }
        sb.append(" ").append(FieldUtils.OWNER).append(" ").append("TEXT NOT NULL ,");
        sb.append(" ").append(FieldUtils.KEY).append(" ").append("TEXT NOT NULL ,");
        sb.append(" ").append(FieldUtils.CREATEAT).append(" ").append("INTEGER NOT NULL");
        if (primaryKey instanceof  ColumnInfo){
            sb.append(" PRIMARY KEY ( ").append(primaryKey.getColumnName()).append(" , ")
                    .append(FieldUtils.OWNER).append(" , ")
                    .append(FieldUtils.KEY).append(" )");
        }
        sb.append(" ) ");
        String sql =sb.toString();
        DBLogger.d(SqliteUtility.TAG,"create table ##sql## ="+sql);
        return sql;
    }

    public static String appenExtraWhereClause(Extra extra){

    }
}
