package com.youarenotin.lib_orm_db.utils;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.youarenotin.lib_orm_db.SqliteUtility;
import com.youarenotin.lib_orm_db.annotation.TableName;
import com.youarenotin.lib_orm_db.extra.ColumnInfo;
import com.youarenotin.lib_orm_db.extra.TableInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lubo on 2016/6/9.
 * lubo_wen@126.com
 */
public class TableInfoUtils {
    private final static String TAG=TableInfoUtils.class.getSimpleName();

    public static HashMap<String, TableInfo> getTableInfoMap() {
        return tableInfoMap;
    }

    private static final HashMap<String, TableInfo> tableInfoMap;

    static {
        tableInfoMap = new HashMap<String, TableInfo>();
    }

    public static String getTableName(Class<?> clazz){
        TableName tableName = clazz.getAnnotation(TableName.class);
        if (tableName==null || tableName.toString().trim().length()<=0){
            //如果注解没有填写使用该类的名字作为表名
            return clazz.getName().replace(".","_");
        }
        return tableName.table();//如果有注解返回注解的值
    }

    /**
     * 所有数据库的表名全保存在 tableInfoMap 中
     * @param dbName
     * @param clazz
     * @return
     */
    public static boolean isExeitsTable(String dbName , Class<?> clazz){
        return  tableInfoMap.containsKey(dbName+"-"+getTableName(clazz));
    }

    /**
     * 给定数据库名字和数据库实例条件下创建新表
     * @param dbName
     * @param db
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>  TableInfo newTable(String dbName , SQLiteDatabase db ,Class<T> clazz){
        Cursor cursor=null ;
        TableInfo tableInfo =new TableInfo(clazz);
        try {
            tableInfoMap.put(dbName+"-"+getTableName(clazz),tableInfo);
            cursor = null;

            //检测该表是否在db中存在
            String sql  = "SELECT COUNT(*) AS P FROM sqlite_master WHERE type='table' AND name='"+getTableName(clazz)+"'";
            cursor = db.rawQuery(sql,null);
            if (cursor!=null && cursor.moveToNext()){
                int count = cursor.getInt(0);
                if (count>0){//如果表存在
                    DBLogger.d(SqliteUtility.TAG,"表 %s 已存在",tableInfo.getTableName());
                    //查看字段是否有更新
                    cursor = db.rawQuery("PRAGMA table_info ("+tableInfo.getTableName()+")",null);//获取表结构
                    List<String> columnNames  = new ArrayList<String>();//获取已存在表的列名集合
                    if (cursor!=null && cursor.moveToNext()){
                        columnNames.add(cursor.getString(cursor.getColumnIndex("name")));
                    }

                    List<String> properList = new ArrayList<String>();
                    for (ColumnInfo info : tableInfo.getColumns()){
                        properList.add(info.getColumnName());
                    }

                    List<String> newList = new ArrayList<String>();
                    for (String str : properList){
                        boolean isNew =true;
                            if (tableInfo.getPrimaryKey().getColumnName().equals(str))
                                continue;
                        for (String strBeta : columnNames){
                            if (str==strBeta){
                                isNew = false;
                            }
                        }
                        if (isNew){
                            newList.add(str);
                        }
                    }
                    //得到新字段集合newList
                    //修改表结构 创建新的字段
                    for (String newProper : newList){
                        db.execSQL(String.format("ALERT TABLE %S ADD %s TEXT",tableInfo.getTableName(),newProper));
                        DBLogger.d(SqliteUtility.TAG,"表 %s 添加字段 %s",tableInfo.getTableName(),newProper);
                    }
                    return  tableInfo;
                }
                else{//如果表不存在  创建新表
                    String createTableSql = SqlUtils.getCreateTableSql(tableInfo);
                    db.execSQL(createTableSql);
                    DBLogger.d(TAG,"创建一张新表"+tableInfo.getTableName());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            DBLogger.d(TAG,e.getMessage()+"");
        }
        finally {
            if (cursor!=null){
                cursor.close();
                cursor=null;
            }
        }
        return tableInfo;
    }


}
