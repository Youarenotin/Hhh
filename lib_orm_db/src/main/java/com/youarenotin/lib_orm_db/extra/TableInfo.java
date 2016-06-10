package com.youarenotin.lib_orm_db.extra;


import com.youarenotin.lib_orm_db.annotation.AutoIncrementPrimaryKey;
import com.youarenotin.lib_orm_db.annotation.PrimaryKey;
import com.youarenotin.lib_orm_db.utils.TableInfoUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lubo on 2016/6/9.
 * lubo_wen@126.com
 */
public class TableInfo {
    public List<ColumnInfo> getColumns() {
        return columns;
    }

    private List<ColumnInfo>  columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private String tableName;
    private ColumnInfo primaryKey;
    private Class<?>  clazz;

    public TableInfo(Class<?> clazz) {
        this.clazz = clazz;
        setInit();
    }

    private void setInit() {
        columns = new ArrayList<ColumnInfo>();
        //设置表名
        setTableName();
        //设置每一列
        setColumns(clazz);
    }

    private void setColumns(Class<?> clazz) {
        if (clazz==null || "Object".equalsIgnoreCase(clazz.getSimpleName())){
            return ;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            if (primaryKey==null){
                PrimaryKey annotation = field.getAnnotation(PrimaryKey.class);
                //如果注解了该字段为主键
                if (annotation!=null){
                    primaryKey = new ColumnInfo();
                    primaryKey.setColumnName(annotation.column());
                    setColumn(field,primaryKey);
                    continue;
                }
                AutoIncrementPrimaryKey annotation1 = field.getAnnotation(AutoIncrementPrimaryKey.class);
                //如果注解了该字段为自增主键
                if (annotation1!=null){
                    primaryKey = new ColumnInfo();
                    primaryKey.setColumnName(annotation1.column());
                    setColumn(field,primaryKey);
                    continue;
                }
            }
            //排除序列化字段
            if (field.getName().equals("serialVersionUID"))
                continue;
            //添加其它正常字段
            ColumnInfo column = new ColumnInfo();
            column.setColumnName(field.getName());
            setColumn(field, column);

            columns.add(column);
        }
        setColumns(clazz.getSuperclass());
    }

    private void setColumn(Field field, ColumnInfo column) {
        column.setField(field);
        if (field.getType().getName().equals("int")
                || field.getType().getName().equals("java.lang.Integer")){
            column.setDataType("int");
            column.setColumnType("Integer");
        }
        else if ( field.getType().getName().equals("long")
                || field.getType().getName().equals("java.lang.Long")){
            column.setDataType("long");
            column.setColumnType("INTEGER");
        }
        else if (field.getType().getName().equals("float")
                || field.getType().getName().equals("java.lang.Float")){
            column.setDataType("float");
            column.setColumnType("REAL");
        }
        else if (field.getType().getName().equals("double")
                || field.getType().getName().equals("java.lang.Double")){
            column.setDataType("double");
            column.setColumnType("REAL");
        }
        else if (field.getType().getName().equals("boolean")
                || field.getType().getName().equals("java.lang.Boolean")){
            column.setDataType("boolean");
            column.setColumnType("TEXT");
        }
        else if (field.getType().getName().equals("char")
                || field.getType().getName().equals("java.lang.Character")){
            column.setDataType("char");
            column.setColumnType("TEXT");
        }
        else if(field.getType().getName().equals("byte")
                || field.getType().getName().equals("java.lang.Byte")){
            column.setDataType("byte");
            column.setColumnType("INTEGER");
        }
        else if (field.getType().getName().equals("short") ||
                field.getType().getName().equals("java.lang.Short")) {
            column.setDataType("short");
            column.setColumnType("TEXT");
        }
        else if (field.getType().getName().equals("java.lang.String")) {
            column.setDataType("string");
            column.setColumnType("TEXT");
        }
        else if (field.getType().getName().equals("[B")) {
            column.setDataType("blob");
            column.setColumnType("BLOB");
        }
        else {
            column.setDataType("object");
            column.setColumnType("TEXT");
        }
    }


    private void setTableName() {
        TableInfoUtils.getTableName(clazz);
    }

    public ColumnInfo getPrimaryKey() {
        return primaryKey;
    }
}
