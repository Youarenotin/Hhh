package com.youarenotin.lib_orm_db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.youarenotin.lib_orm_db.extra.ColumnInfo;
import com.youarenotin.lib_orm_db.extra.Extra;
import com.youarenotin.lib_orm_db.extra.TableInfo;
import com.youarenotin.lib_orm_db.utils.DBLogger;
import com.youarenotin.lib_orm_db.utils.SqlUtils;
import com.youarenotin.lib_orm_db.utils.TableInfoUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lubo on 2016/6/9.
 */
public class SqliteUtility {
    public static final String TAG = "SqliteUtility";
    private String dbName;
    private SQLiteDatabase db;
    private static HashMap<String, SqliteUtility> dbCache = new HashMap<String, SqliteUtility>();

    SqliteUtility(String dbName, SQLiteDatabase db) {
        this.dbName = dbName;
        this.db = db;
        dbCache.put(dbName, this);
        DBLogger.d(TAG, "将库 %s 放到缓存中", dbName);
    }

    public static SqliteUtility getInstance() {
        return getInstance(SqliteUtilityBuilder.DEFAULT_DB);
    }

    private static SqliteUtility getInstance(String dbName) {
        return dbCache.get(dbName);
    }

    /********************************** 开启select系列方法************************************/
    public <T> T selectById(Extra extra, Class<T> clazz, Object id) {
        try {
            if (checkTable(clazz) == null) {
                DBLogger.d(TAG, "select failed");
                return null;
            }
            TableInfo tableInfo = checkTable(clazz);
            //region selection
            String selection = String.format(" %s = ? ", tableInfo.getPrimaryKey().getColumnName());
            String extraSelection = SqlUtils.appenExtraWhereClause(extra);
            if (!TextUtils.isEmpty(extraSelection)) {
                selection = String.format(" %s AND  %s", selection, extraSelection);
            }
            //endregion

            //region selectionArgs
            String[] whereArgs = SqlUtils.appendExtraWhereArgs(extra);
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(id));//根据主键id
            if (whereArgs != null && whereArgs.length > 0)
                list.addAll(Arrays.asList(whereArgs));
            String[] selectionArgs = list.toArray(new String[0]);
            //endregion
            List<T> rlt = select(clazz, selection, selectionArgs, null, null, null, null);
            if (rlt.size() > 0) {
                return rlt.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> List<T> select(Extra extra, Class<T> clazz) {
        String selection = SqlUtils.appenExtraWhereClause(extra);
        String[] selectArgs = SqlUtils.appendExtraWhereArgs(extra);
        return select(clazz, selection, selectArgs, null, null, null, null);
    }

    public <T> List<T> select(Class<T> clazz, String selection, String[] selectArgs) {
        return select(clazz, selection, selectArgs, null, null, null, null);
    }

    private <T> List<T> select(Class<T> clazz, String selection, String[] selectArgs
            , String groupBy, String having, String orderBy, String limit) {
        if (checkTable(clazz) == null) {
            DBLogger.d(TAG, "select failed : 表不存在且创建失败　");
            return null;
        }
        TableInfo tableInfo = checkTable(clazz);
        List<T> list = new ArrayList<T>();
        DBLogger.d(TAG, "method[select],table[%s],selection[%s],selectArgs[%s],groupBy[%s],having[%s],orderBy[%s],limit[%s]"
                , tableInfo.getTableName(), selection, selectArgs, groupBy, having, orderBy, limit);
        List<String> columnInfoNameList = new ArrayList<>();
        columnInfoNameList.add(tableInfo.getPrimaryKey().getColumnName());
        for (ColumnInfo c : tableInfo.getColumns()) {
            columnInfoNameList.add(c.getColumnName());
        }
        List<T> rltList = new ArrayList<T>();
        long start = System.currentTimeMillis();
        Cursor cursor = db.query(this.dbName, columnInfoNameList.toArray(new String[0]), selection, selectArgs, groupBy, having, orderBy, limit);
        while (cursor.moveToNext()){
            try {
                T entity = clazz.newInstance();

                bindEntityField(entity,cursor,tableInfo.getPrimaryKey());//绑定entity中主键
                for (ColumnInfo c : tableInfo.getColumns()) {//绑定常规字段
                    bindEntityField(entity,cursor,c);
                }
                rltList.add(entity);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            finally {
                if (cursor!=null){
                    cursor.close();
                    cursor=null;
                }
            }
        }
        long end = System.currentTimeMillis();
        DBLogger.d(TAG,"table[ %s ] 设置数据完毕 , 耗时 %s",tableInfo.getTableName(),String.valueOf(end-start));
        DBLogger.d(TAG,"查询到 %s 条数据" , rltList.size());
        if (rltList.size()>0){
            return  rltList;
        }
        return null;
    }

    /**********************************开启insert系列方法***************************************/
    public <T> void insertOrReplace(Extra extra  ,T...entity ){
        if (entity==null || entity.length<=0){
            DBLogger.d(TAG,"method[ insertOrReplace(Extra extra  ,T...entity ) ] ,entity is null or empty ");
        }
        else {
            insert(extra,Arrays.asList(entity),"INSERT OR REPLACE INTO");
        }

    }

    private <T> void insert(Extra extra, List<T> entityList, String s) {
        TableInfo tableInfo = checkTable(entityList.get(0).getClass());
        if (tableInfo == null){
            DBLogger.d(TAG,"insert failed");
        }
        long start = System.currentTimeMillis();
        db.beginTransaction();


    }


    /**********************************tool method**********************************************/
    private <T> void bindEntityField(T entity, Cursor cursor, ColumnInfo columnInfo) {
        Field field = columnInfo.getField();
        field.setAccessible(true);
        try { 
        if (field.getType().getName().equals("int") || field.getType().getName().equals("java.lang.Integer")){
            
                field.set(entity,cursor.getInt(cursor.getColumnIndex(columnInfo.getColumnName())));
          
            }
        else if (field.getType().getName().equals("long") ||
                field.getType().getName().equals("java.lang.Long")) {
            field.set(entity, cursor.getLong(cursor.getColumnIndex(columnInfo.getColumnName())));
        }
        else if (field.getType().getName().equals("float") ||
                field.getType().getName().equals("java.lang.Float")) {
            field.set(entity, cursor.getFloat(cursor.getColumnIndex(columnInfo.getColumnName())));
        }
        else if (field.getType().getName().equals("double") ||
                field.getType().getName().equals("java.lang.Double")) {
            field.set(entity, cursor.getDouble(cursor.getColumnIndex(columnInfo.getColumnName())));
        }
        else if (field.getType().getName().equals("boolean") ||
                field.getType().getName().equals("java.lang.Boolean")) {
            field.set(entity, Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(columnInfo.getColumnName()))));
        }
        else if (field.getType().getName().equals("char") ||
                field.getType().getName().equals("java.lang.Character")) {
            field.set(entity, cursor.getString(cursor.getColumnIndex(columnInfo.getColumnName())).toCharArray()[0]);
        }
        else if (field.getType().getName().equals("byte") ||
                field.getType().getName().equals("java.lang.Byte")) {
            field.set(entity, (byte) cursor.getInt(cursor.getColumnIndex(columnInfo.getColumnName())));
        }
        else if (field.getType().getName().equals("short") ||
                field.getType().getName().equals("java.lang.Short")) {
            field.set(entity, cursor.getShort(cursor.getColumnIndex(columnInfo.getColumnName())));
        }
        else if (field.getType().getName().equals("java.lang.String")) {
            field.set(entity, cursor.getString(cursor.getColumnIndex(columnInfo.getColumnName())));
        }
        else if (field.getType().getName().equals("[B")) {
            field.set(entity, cursor.getBlob(cursor.getColumnIndex(columnInfo.getColumnName())));
        }
        else {
            String text = cursor.getString(cursor.getColumnIndex(columnInfo.getColumnName()));
            field.set(entity, JSON.parseObject(text, field.getGenericType()));
        }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    /**********************************tool method**********************************************/


    /**
     * 检测表是否存在 如果不存在 则创建表
     * 检测实体字段是否有增加 有则更新表
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> TableInfo checkTable(Class<T> clazz) {
        TableInfo tableInfo = null;
        if (TableInfoUtils.isExeitsTable(this.dbName, clazz)) {
            return TableInfoUtils.getTableInfoMap().get(this.dbName + "-" + TableInfoUtils.getTableName(clazz));
        } else {
            TableInfoUtils.newTable(this.dbName, this.db, clazz);
            if (TableInfoUtils.isExeitsTable(this.dbName, clazz)) {
                return TableInfoUtils.getTableInfoMap().get(this.dbName + "-" + TableInfoUtils.getTableName(clazz));
            }
            return null;
        }
    }

}
