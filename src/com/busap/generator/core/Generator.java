package com.busap.generator.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.busap.generator.bean.Column;
import com.busap.generator.util.DBConnectionUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * 核心生成器
 * @author LiuYu
 */
public class Generator {
	
	private Configuration cfg;
	
	private Connection conn;
	
	/**
	 * 初始化方法
	 */
	private void initial(){
		
		try {
			if(null == cfg){
				cfg = new Configuration();
			}
			//加载freemark脚本的根目录
			cfg.setDirectoryForTemplateLoading(new File("src/com/busap/generator/template"));
			//打开连接
			conn = DBConnectionUtils.getJDBCConnection();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 指定如何查看数据模型 
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}
	
	/**
	 * 生成
	 * @throws Exception 
	 */
	public void generator(String bussiPackage, List<String> tableNames) throws Exception{
		initial();
		cfg.setDefaultEncoding("UTF-8");
		Template entityTemplate = cfg.getTemplate("entity.ftl");
		Template sqlTemplate = cfg.getTemplate("mybatisMapper.ftl");
		//获取数据库中所有表、字段与实体、属性之间的对应模板
		List<Map<String, Object>> templates = generatorTemplateData(bussiPackage, tableNames);
		
		for(Map<String, Object> o : templates){
//          Writer out = new OutputStreamWriter(System.out);//  
//          //打印到控制台  
//          Writer outFile = new OutputStreamWriter(new FileOutputStream(  
//                  "src/com/busap/generator/template/"+o.get("tableName")+".java"), "gb2312");  
//          entityTemplate.process(o, out);  
//          entityTemplate.process(o, outFile);  
//          out.flush();
			
			File beanfile = new File("src/com/busap/generator/template/entity/" + o.get("beanName") + ".java");
			Writer beanWriter = new FileWriter(beanfile);
			//freemark模板执行获取需要的map参数并写入到文件
			entityTemplate.process(o, beanWriter);
			beanWriter.close();
			
			File sqlFile = new File("src/com/busap/generator/template/mapper/" + o.get("beanName") + "Mapper.xml");
			Writer sqlWriter = new FileWriter(sqlFile);
			sqlTemplate.process(o, sqlWriter);
			sqlWriter.close();
			System.out.println("生成：" + o.get("beanName"));
		}
		
		
	}

	/**
	 * 组装模板中需要的数据（数据库表与实体的映射）
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> generatorTemplateData(String bussiPackage, List<String> tableNames) throws Exception {
		DatabaseMetaData dbmd = conn.getMetaData();
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		if(tableNames == null || tableNames.size() == 0){
			String[] tables = { "Table" };
			//获取对应数据库表的结果集
			ResultSet tableSet = dbmd.getTables(null, null, "%", tables);
			//循环结果集
			while(tableSet.next()){
				String tableName1 = tableSet.getString("TABLE_NAME");
				lists.add(this.tableTemplateData(bussiPackage, tableName1));
			}
		} else {
			for(int i=0; i<tableNames.size(); i++){
				String tableName = tableNames.get(i);
				lists.add(this.tableTemplateData(bussiPackage, tableName));
			}
		}
		return lists;
	}
	
	/**
	 * 装载table与实体名之间的映射关系
	 * @param bussiPackage
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private Map<String, Object> tableTemplateData(String bussiPackage, String tableName) throws SQLException{
		DatabaseMetaData dbmd = conn.getMetaData();
		Map<String, Object> map = new HashMap<String, Object>();
		//获取表中所有的字段
		ResultSet columnSet = dbmd.getColumns(null, "%", tableName, "%");
		
		List<Column> columns = new ArrayList<Column>();
		
		while(columnSet.next()){
			
			String columnName = columnSet.getString("COLUMN_NAME");
			String attributeName = handlerColumnName(columnName);
			String columnType = columnSet.getString("TYPE_NAME");
			String attributeType = handlerColumnType(columnType);
			
			Column column = new Column();
			column.setColumnName(columnName);
			column.setAttributeName(attributeName);
			column.setColumnType(columnType);
			column.setAttributeType(attributeType);
			columns.add(column);
		}
		map.put("bussiPackage", bussiPackage);
		map.put("tableName", tableName);
		map.put("beanName", handlerTableName(tableName));
		map.put("columns", columns);
		return map;
	}
	

	/**
	 * 将数据库列按规则转化成java属性的驼峰式命名规范
	 * @param oldName
	 * @return
	 */
	public static String handlerColumnName(String oldName) {
		String[] arrays = oldName.split("_");
		String newName = "";
		if(arrays.length > 0){
			newName = arrays[0];
		}
		for(int i = 1; i < arrays.length; i++){
			newName += (arrays[i].substring(0, 1).toUpperCase() + arrays[i].substring(1, arrays[i].length()));
		}
		
		return newName;
	}
	
	/**
	 * 将数据库表按规则转化成java中的类命名规范
	 * @param oldName
	 * @return
	 */
	public static String handlerTableName(String oldName){
		String[] arrays = oldName.split("_");
		String newName = "";
		for(int i = 0; i < arrays.length; i++){
			newName += (arrays[i].substring(0, 1).toUpperCase() + arrays[i].substring(1, arrays[i].length()));
		}
		return newName;
	}
	
	/**
	 * 将数据库表列对应的类型转换成java中的基本类型
	 * @param oldType
	 * @return
	 */
	public static String handlerColumnType(String oldType){
        String result = oldType;
		if (oldType.equalsIgnoreCase("varchar") || oldType.equalsIgnoreCase("char") || oldType.equalsIgnoreCase("TINYBLOB")) {
			result="String";
		}else if(oldType.equalsIgnoreCase("double")){
			result="Double";
		}else if (oldType.equalsIgnoreCase("INT") || oldType.equalsIgnoreCase("INT UNSIGNED") || oldType.equalsIgnoreCase("tinyint") || oldType.equalsIgnoreCase("bit")) {
			result="Integer";
		}else if (oldType.equalsIgnoreCase("Date")) {
			result="Date";
		}else if (oldType.equalsIgnoreCase("Datetime")) {
			result="Date";
		}else if (oldType.equalsIgnoreCase("decimal")){
			result="Bigdecimal";
		}else if (oldType.equalsIgnoreCase("text")){
			result="Text";
		}else if (oldType.equalsIgnoreCase("blob")){
			result="Blob";
		}else if (oldType.equalsIgnoreCase("bigint")){
			result="BigInteger";
		}else if (oldType.equalsIgnoreCase("timestamp")){
			result="Date";
		}
		return result;
	}
	
	
    public static void main(String[] args) throws Exception {  
        Generator gen = new Generator();  
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("activity_type");
        gen.generator("com.busap.generator", tableNames);  
    }  
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}


