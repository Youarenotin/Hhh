package com.youarenotin.lib_orm_db.utils;

import android.nfc.Tag;
import android.text.TextUtils;

import com.youarenotin.lib_orm_db.SqliteUtility;
import com.youarenotin.lib_orm_db.extra.AutoIncrementColumnInfo;
import com.youarenotin.lib_orm_db.extra.ColumnInfo;
import com.youarenotin.lib_orm_db.extra.Extra;
import com.youarenotin.lib_orm_db.extra.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lubo on 2016/6/10.
 * lubo_wen@126.com
 */
public class SqlUtils {
    /**
     * 得到插入数据sql
     * @param insertInto
     * @param tableInfo
     * @return
     */
    public static String getInsertsql(String insertInto , TableInfo tableInfo){
        if(tableInfo==null){
            DBLogger.d(SqliteUtility.TAG,"tableInfo is null ");
            return "";
        }
        List<String> columns = new ArrayList<String>();
        if (tableInfo.getPrimaryKey() instanceof AutoIncrementColumnInfo)
            ;
        else
            columns.add(tableInfo.getPrimaryKey().getColumnName());
        for (ColumnInfo c : tableInfo.getColumns()){
            columns.add(c.getColumnName());
        }
        StringBuilder sb  = new StringBuilder(insertInto);
        sb.append(" ").append(tableInfo.getTableName()).append(" ");
        sb.append("VALUES ( ");
        for (String s : columns){
            sb.append(" ").append(s).append(" , ");
        }
        sb.append(" ) ");

    }

    /**
     * 得到创建表sql
     * @param tableInfo
     * @return
     */
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

    /**
     *
     * @param extra
     * @return
     */
    public static String appenExtraWhereClause(Extra extra){
        StringBuilder sb = new StringBuilder();
        if (extra==null || (TextUtils.isEmpty(extra.getOwner()) && TextUtils.isEmpty(extra.getKey()))){
            sb.append("");
        }
        if (!TextUtils.isEmpty(extra.getKey()) && !TextUtils.isEmpty(extra.getOwner())){
            sb.append(" ")
                    .append(FieldUtils.OWNER).append(" = ? ")
                    .append(" AND　")
                    .append(FieldUtils.KEY).append(" = ? ");
        }
        if (!TextUtils.isEmpty(extra.getOwner())){
            sb.append(" ").append(FieldUtils.OWNER)
                    .append(" = ? ");
        }
        if(!TextUtils.isEmpty(extra.getKey())){
            sb.append(" ").append(FieldUtils.KEY)
                    .append(" = ? ");
        }
        return sb.toString();
    }

    /**
     *
     * @param extra
     * @return
     */
    public static String[] appendExtraWhereArgs(Extra extra){
        List<String> args = new ArrayList<String>();
        if (!TextUtils.isEmpty(extra.getKey())){
            args.add(extra.getKey());
        }
        if (!TextUtils.isEmpty(extra.getOwner())){
            args.add(extra.getOwner());
        }
        return args.toArray(new String[0]);
    }
}
