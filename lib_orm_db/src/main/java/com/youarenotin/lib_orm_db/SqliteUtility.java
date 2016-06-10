package com.youarenotin.lib_orm_db;

import android.database.sqlite.SQLiteDatabase;

import com.youarenotin.lib_orm_db.extra.Extra;
import com.youarenotin.lib_orm_db.extra.TableInfo;
import com.youarenotin.lib_orm_db.utils.DBLogger;
import com.youarenotin.lib_orm_db.utils.TableInfoUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lubo on 2016/6/9.
 */
public class SqliteUtility {
    public  static final String TAG = "SqliteUtility";
    private String dbName;
    private SQLiteDatabase db;
    private static HashMap<String, SqliteUtility> dbCache = new HashMap<String, SqliteUtility>();

    SqliteUtility(String dbName, SQLiteDatabase db) {
        this.dbName = dbName;
        this.db = db;
        dbCache.put(dbName, this);
        DBLogger.d(TAG,"将库 %s 放到缓存中",dbName);
    }

    public static SqliteUtility getInstance() {
        return getInstance(SqliteUtilityBuilder.DEFAULT_DB);
    }

    private static SqliteUtility getInstance(String dbName) {
        return dbCache.get(dbName);
    }

    /*********************************开启select系列方法************************************/
    public <T> T selectById(Extra extra , Class<?> clazz , Object id ){
        if (checkTable(clazz)==null){
            DBLogger.d(TAG,"select failed");
            return null;
        }

    }

    public <T> List<T> select(Extra extra , Class<T> clazz){

    }
    /**
     * 检测表是否存在 如果不存在 则创建表
     * 检测实体字段是否有增加 有则更新表
     * @param clazz
     * @param <T>
     * @return
     */
    private <T>  TableInfo checkTable(Class<T> clazz){
        TableInfo tableInfo = null;
        if(TableInfoUtils.isExeitsTable(this.dbName,clazz)){
           return  TableInfoUtils.getTableInfoMap().get(this.dbName+"-"+TableInfoUtils.getTableName(clazz));
        }
        else{
            TableInfoUtils.newTable(this.dbName,this.db,clazz);
            if(TableInfoUtils.isExeitsTable(this.dbName,clazz)){
                return  TableInfoUtils.getTableInfoMap().get(this.dbName+"-"+TableInfoUtils.getTableName(clazz));
            }
            return null;
        }
    }

}
