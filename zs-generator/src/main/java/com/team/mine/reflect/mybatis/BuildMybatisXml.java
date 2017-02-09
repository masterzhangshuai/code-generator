package com.team.mine.reflect.mybatis;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.kd.news.domain.UrlExplain;
import com.kd.news.service.UrlExplainService;

public class BuildMybatisXml {

	/**
	 * 测试
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
//		String entityPath="E:/workspace/dataimport-tools/target/classes/com/kd/dataimport/domain";
//		String servicePath="E:/workspace/dataimport-tools/target/classes/com/kd/dataimport/service";
//		Map<Class<?>, Class<?>> reflectMap = loadClasses(entityPath,servicePath);
		Map<Class<?>, Class<?>> reflectMap = new HashMap<Class<?>, Class<?>>();
		
		reflectMap.put(UrlExplainService.class,UrlExplain.class);
//		reflectMap.put(SuggestsService.class,Suggests.class);

		for (Map.Entry<Class<?>, Class<?>> entry : reflectMap.entrySet()) {
			System.out.println("=>" + entry.getKey().getName());
			String text = buildMybatisXMLReflectMethods(entry.getKey(), entry.getValue());
			FileUtils.write(new File("E:/buildMybatis/news/"
					+ entry.getValue().getSimpleName() + "Mapper.xml"), text, "UTF-8");
			System.out.println();
		}
		
	}

	/**
	 * 生成业务方法与实体对应的Mybatis映射XML
	 * 【注意】方法名命名规范：[insert|update|delete|select]MethodNameByParam1[And|Or|_]Param2...
	 * 
	 * @param serviceClass
	 * @param domainClass
	 * @return
	 * @throws Throwable
	 */
	public static String buildMybatisXMLReflectMethods(Class<?> serviceClass, Class<?> domainClass) throws Throwable {
		Method methods[] = serviceClass.getDeclaredMethods();
		StringBuilder schema = new StringBuilder();
		String resultMap = serviceClass.getSimpleName().replaceAll("([\\w\\W\\s_]{1,64})Service", "$1");

		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n";
		header += "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">";
		String body = "\r\n<mapper namespace=\"" + domainClass.getSimpleName() + "Mapper\">\r\n";
		schema.append(header + body);
		schema.append(GeneratorMySQL.buildResultMap(domainClass));
		String content = "";

		for (Method method : methods) {
			content = "";
			if (method.getName().toLowerCase().matches("^[insert|add|send]{3,6}.*")) {

				content = GeneratorMySQL.buildInsertSQL(domainClass);
				schema.append("\r\n\t<insert id=\"" + method.getName() + "\">" + content + "\t</insert>\r\n\r\n");

			} else if (method.getName().toLowerCase().matches("^[delete]{3,6}.*")) {

				content = GeneratorMySQL.buildDeleteSQL(domainClass);
				schema.append("\r\n\t<delete id=\"" + method.getName() + "\">\r\n" + content + "\t</delete>\r\n\r\n");

			} else if (method.getName().toLowerCase().matches("^[update|save|open|check]{3,6}.*")) {

				content = GeneratorMySQL.buildUpdateSQL(domainClass, true);
				schema.append("\r\n\t<update id=\"" + method.getName() + "\">" + content + "\t</update>\r\n\r\n");

			} else if (method.getName().toLowerCase().matches("^[find|query|select]{4,12}.*")) {

				String countMethod = method.getName().replaceAll("[find|query|select|exists]{4,12}([\\w\\W\\d]{1,128})",
						"count$1");
				String partParam = method.getName().contains("By") ? method.getName().split("By")[1] : "";
				partParam = partParam.trim().isEmpty() ? "id" : partParam;
				char[] tempPartParam = partParam.toCharArray();
				tempPartParam[0] = Character.toLowerCase(tempPartParam[0]);
				partParam = String.copyValueOf(tempPartParam);
				String params[] = partParam.matches("[and|or|_]{1,3}") ? partParam.toLowerCase().split("and|or|_")
						: new String[] { partParam };

				content = GeneratorMySQL.buildSelectSQL(domainClass, true, true, params);
				schema.append("\r\n\t<select id=\"" + countMethod + "\" resultType=\"java.lang.Integer\">" + content
						+ "\t</select>\r\n\r\n");

				content = GeneratorMySQL.buildSelectSQL(domainClass, true, false, params);
				schema.append("\r\n\t<select id=\"" + method.getName() + "\" resultMap=\"" + resultMap + "Map\">"
						+ content + "\t</select>\r\n\r\n");
			}
		}
		schema.append("\r\n</mapper>");
		// System.out.println(schema);
		return schema.toString();
	}
	
	
	/**
	 * 生成注解 *Mapper.java
	 * @param serviceClass
	 * @param domainClass
	 * @return
	 * @throws Throwable
	 */
	public static String buildMybatisAnoMapperReflectMethods(Class<?> serviceClass, Class<?> domainClass) throws Throwable {
		Method methods[] = serviceClass.getDeclaredMethods();
		StringBuilder schema = new StringBuilder();
		String resultMap = serviceClass.getSimpleName().replaceAll("([\\w\\W\\s_]{1,64})Service", "$1");

		String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n";
		header += "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">";
		String body = "\r\n<mapper namespace=\"" + domainClass.getSimpleName() + "Mapper\">\r\n";
		schema.append(header + body);
		schema.append(GeneratorMySQL.buildResultMap(domainClass));
		String content = "";

		for (Method method : methods) {
			content = "";
			if (method.getName().toLowerCase().matches("^[insert|add|send]{3,6}.*")) {

				content = GeneratorMySQL.buildInsertSQL(domainClass);
				schema.append("\r\n\t<insert id=\"" + method.getName() + "\">" + content + "\t</insert>\r\n\r\n");

			} else if (method.getName().toLowerCase().matches("^[delete]{3,6}.*")) {

				content = GeneratorMySQL.buildDeleteSQL(domainClass);
				schema.append("\r\n\t<delete id=\"" + method.getName() + "\">\r\n" + content + "\t</delete>\r\n\r\n");

			} else if (method.getName().toLowerCase().matches("^[update|save|open|check]{3,6}.*")) {

				content = GeneratorMySQL.buildUpdateSQL(domainClass, true);
				schema.append("\r\n\t<update id=\"" + method.getName() + "\">" + content + "\t</update>\r\n\r\n");

			} else if (method.getName().toLowerCase().matches("^[find|query|select]{4,12}.*")) {

				String countMethod = method.getName().replaceAll("[find|query|select|exists]{4,12}([\\w\\W\\d]{1,128})",
						"count$1");
				String partParam = method.getName().contains("By") ? method.getName().split("By")[1] : "";
				partParam = partParam.trim().isEmpty() ? "id" : partParam;
				char[] tempPartParam = partParam.toCharArray();
				tempPartParam[0] = Character.toLowerCase(tempPartParam[0]);
				partParam = String.copyValueOf(tempPartParam);
				String params[] = partParam.matches("[and|or|_]{1,3}") ? partParam.toLowerCase().split("and|or|_")
						: new String[] { partParam };

				content = GeneratorMySQL.buildSelectSQL(domainClass, true, true, params);
				schema.append("\r\n\t<select id=\"" + countMethod + "\" resultType=\"java.lang.Integer\">" + content
						+ "\t</select>\r\n\r\n");

				content = GeneratorMySQL.buildSelectSQL(domainClass, true, false, params);
				schema.append("\r\n\t<select id=\"" + method.getName() + "\" resultMap=\"" + resultMap + "Map\">"
						+ content + "\t</select>\r\n\r\n");
			}
		}
		schema.append("\r\n</mapper>");
		// System.out.println(schema);
		return schema.toString();
	}

	
	@SuppressWarnings({ "resource", "deprecation" })
	public static Map<Class<?>, Class<?>> loadClasses(String entityClassesPath,String serviceClassesPath) throws Exception {
		Map<Class<?>, Class<?>> resultMap = new HashMap<Class<?>, Class<?>>();
		File entityFilepath = new File(entityClassesPath);
		URL url = entityFilepath.toURL();
		
		File serviceFilepath = new File(serviceClassesPath);
		URL serviceurl = serviceFilepath.toURL();
		
		URL[] urls = new URL[] { url,serviceurl };
		ClassLoader cl = new URLClassLoader(urls);
		
		for (File file : entityFilepath.listFiles()) {
			String entityName=file.getName().split("\\.")[0];
			Class<?> entityClass = cl.loadClass(file.getAbsolutePath());
			for(File  serivceClassFiles:serviceFilepath.listFiles()){
				if(serviceClassesPath.contains(entityName)){
					Class<?> serviceClass = cl.loadClass(serivceClassFiles.getAbsolutePath());
					resultMap.put(entityClass, serviceClass);
				}
			}
		}
		return resultMap;
	}
}
