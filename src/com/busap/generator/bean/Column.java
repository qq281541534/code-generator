package com.busap.generator.bean;

/**
 * 数据库表的列与java中的字段对应的bean
 * @author LiuYu
 */
public class Column {
	
	/* 列名称 */
	private String columnName;
	
	/* 列类型 */
    private String columnType;
    
    /* 字段名称 */
    private String attributeName;
    
    /* 字段类型 */
    private String attributeType;
    
    
    public String getColumnName() {  
        return columnName;  
    }  
    public void setColumnName(String columnName) {  
        this.columnName = columnName;  
    }  
    public String getColumnType() {  
        return columnType;  
    }  
    public void setColumnType(String columnType) {  
        this.columnType = columnType;  
    }  
    public String getAttributeName() {  
        return attributeName;  
    }  
    public void setAttributeName(String attributeName) {  
        this.attributeName = attributeName;  
    }  
    public String getAttributeType() {  
        return attributeType;  
    }  
    public void setAttributeType(String attributeType) {  
        this.attributeType = attributeType;  
    }  
}


