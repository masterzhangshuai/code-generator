package com.team.mine.reflect.mybatis;

import java.lang.reflect.Field;

public class GeneratorMySQL {

	public static void main(String[] args) throws Throwable {
		Class<?> clazzs[] = {
				
		};
		for (Class<?> clazz : clazzs) {
//			System.out.println("\r\n=>" + clazz.getSimpleName());
			System.out.println( buildResultMap(clazz));
//			System.out.println( buildInsertSQL(clazz));
//			buildUpdateSQL(clazz, true);
//			System.out.println( buildUpdateSQL(clazz, true));
//			 buildDeleteSQL(clazz);
//			System.out.println( buildSelectSQL(clazz, true,false, "id"));
//			 buildSelectSQL(clazz, true,false, "id");
		}
	}

	private static String subFirst_(String tbName) {
		int _index = tbName.indexOf("_");
		return _index == 0 ? tbName.substring(_index + "_".length(), tbName.length()) : tbName;
	}

	/**
	 * 生成ResultMap 属性与列名 映射
	 * 
	 * @param clazz
	 */
	public static String buildResultMap(Class<?> clazz) {
		StringBuilder str=new StringBuilder();
		Field fields[] = clazz.getFields();
		String result = "";
		String mapHeader="<resultMap id=\""+clazz.getSimpleName()+"Map\" type=\""+clazz.getSimpleName()+"\">";
		str.append(mapHeader);
		for (Field field : fields) {
			result = field.getName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
			result="\r\n\t<result property=\"" + field.getName() + "\"\tcolumn=\"" + result + "\"/>";
//			System.out.println(result);
			str.append(result);
		}
		str.append("\r\n</resultMap>");
		return str.toString();
	}

	/**
	 * 生成 insert SQL
	 * 
	 * @param clazz
	 */
	public static String buildInsertSQL(Class<?> clazz) {
		StringBuilder str=new StringBuilder();
		Field fields[] = clazz.getFields();
		String sql = "insert into ";
		String tbName = clazz.getSimpleName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
		tbName = subFirst_(tbName);
		boolean isFirst = true;
		sql += tbName + "( ";
		for (Field field : fields) {
			field.setAccessible(true);
			sql += isFirst ? "" : ",";
			sql += field.getName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
			isFirst = false;
		}
		sql += ")";
		isFirst = true;
		sql += " values (";
		for (Field field : fields) {
			sql += isFirst ? "" : ",";
			sql += "#{" + field.getName() + "}";
			isFirst = false;
		}
		isFirst = true;
		sql += " )";
		sql += "\r\nON DUPLICATE KEY UPDATE \r\n";
		String fieldTemp = "";
		for (Field field : fields) {
			sql += isFirst ? "" : ",";
			fieldTemp = field.getName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
			sql += fieldTemp + "= values(" + fieldTemp + ")";
			isFirst = false;
		}
		str.append(sql);
		//System.out.println(sql);
		return str.toString();
	}

	/**
	 * 生成 update SQL
	 * 
	 * @param clazz
	 */
	public static String buildUpdateSQL(Class<?> clazz, boolean isMabatis) {
		StringBuilder str=new StringBuilder();
		Field fields[] = clazz.getFields();
		String sql = "update ";
		String tbName = clazz.getSimpleName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
		tbName = subFirst_(tbName);
		sql += tbName + "" + " set  ";
		String conditionsColumn = "";
		boolean isFirst = true;
		int hasConditions = 0;
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getName().toLowerCase().matches("[update_time|latest_update|create_time]{8,32}")) {
				++hasConditions;
				continue;
			}
			conditionsColumn = field.getName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();

			sql += isMabatis ? "\r\n\t<if test=\"" + field.getName() + "!=null\">" : "";

			sql += isFirst ? "" : " , ";
			sql += " "+conditionsColumn + " = #{" + field.getName() + "}";

			sql += isMabatis ? "</if>" : "";
			isFirst = false;
		}

		if (hasConditions > 0) {
			sql += "" + "\r\n where 1 ";
		} else {
			//System.out.println(sql);
			str.append(sql);
			return str.toString();
		}
		for (Field field : fields) {
			if (field.getName().toLowerCase().matches("^id$")) {
				sql += " and " + field.getName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
				sql += " = #{" + field.getName() + "}";
			}
		}

		//System.out.println(sql);
		str.append(sql);
		return str.toString();
	}

	/**
	 * 生成 delete SQL
	 * 
	 * @param clazz
	 */
	public static String buildDeleteSQL(Class<?> clazz) {
		StringBuilder str=new StringBuilder();
		String sql = "delete from ";
		String tbName = clazz.getSimpleName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
		tbName = subFirst_(tbName);
		sql += tbName + " ";
		sql += " where id = #{id}";

		//System.out.println(sql);
		str.append(sql);
		return str.toString();
	}

	/**
	 *  生成 select SQL
	 * @param clazz
	 * @param isMabatis
	 * @param isCount	是否count
	 * @param conditions  筛选 条件
	 * @return
	 * @throws Throwable
	 */
	public static String buildSelectSQL(Class<?> clazz, boolean isMabatis,boolean isCount, String... conditions) throws Throwable {
		StringBuilder str=new StringBuilder();
		String sql = "select ";
		sql+= isCount?" count(*) ":" * ";
		sql+=" from ";
		String tbName = clazz.getSimpleName().replaceAll("([A-Z]{1})", "_$1").toLowerCase();
		tbName = subFirst_(tbName);
		sql += tbName + " ";
		if (conditions != null && conditions.length > 0) {
			sql += " where 1 ";
		}
		
		String fieldName = "", column = "";
		for (String param : conditions) {
			try{
				//匹配 xxx[Set|List]  (idSet)
				if(param.toLowerCase().matches(".*[set|list]$")){
					fieldName = clazz.getField(param.toLowerCase().split("set|list")[0]).getName();
					sql+=isMabatis ? "\r\n<if test=\"" + param + "!=null\">" : "";
					sql+="\r\n\t and "+fieldName+" in ";
					sql+=isMabatis ? "\r\n\t<foreach collection=\"" + param + "\" item=\""+fieldName+"\" open=\"(\" separator=\",\"  close=\")\">" : "";
					sql+=isMabatis ? "\r\n\t\t#{"+fieldName+"}":"()";
					sql+=isMabatis ? "\r\n\t</foreach>\r\n":"";
					sql+=isMabatis ? "</if>":"";
					fieldName = clazz.getField(fieldName).getName();
				}else{
					Field tempField=clazz.getField(param);
					column = tempField==null ? "id" : tempField.getName().replaceAll("([A-Z]{1})", "_$1").toLowerCase(); 
					fieldName = tempField.getName();
					
					sql += isMabatis ? "<if test=\"" + fieldName + "!=null\">" : "";
					sql += " and "+ column + " = #{" + fieldName + "}";
					sql += isMabatis ? "</if>" : "";
				}
			}catch(Exception e){
				System.err.println(clazz.getName()+".buildSelectSQL(): "+param+" err "+e.getMessage());
			}
		}
		//System.out.println(sql);
		str.append(sql);
		return str.toString();
	}
	
}
