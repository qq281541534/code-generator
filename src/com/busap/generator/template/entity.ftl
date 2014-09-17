package ${bussiPackage}.template.entity;

import java.util.Date;


/**
 * 对应表${tableName}.
 *
 * @author LiuYu
 */
public class ${beanName} implements java.io.Serializable {
	
	<#list columns as item>
	/** 对应表中${item.columnName} */
	private ${item.attributeType} ${item.attributeName};
	
	</#list>
	
	<#list columns as item>
	public ${item.attributeType} get${item.attributeName?cap_first}(){
		return ${item.attributeName};		
	}

	public void set${item.attributeName?cap_first}(${item.attributeType} ${item.attributeName}){
		this.${item.attributeName} = ${item.attributeName};
	}
	</#list>
} 
 