package com.team.mine.reflect.mybatis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class SqlPowerDesigner {

	public static String tableNameRegx="create[\\s\\S]{1,}table[\\s\\S]{1,}`([\\d\\w\\_]{1,})`[\\s\\S]{0,}\\(";
	
	public static String tableCommentRegx="engine=.*'(.*)';";
	
	public static void main(String[] args) throws Throwable {
		// TODO Auto-generated method stub
		List<String> lines=FileUtils.readLines(new File("C:\\Users\\zhangshuai\\Desktop\\create.sql"), "UTF-8");
		List<String> resultList=new ArrayList<String>();
		String name="",comment="" ,temp="";
		for(int i=0;i<lines.size();i++){
			String str=lines.get(i);
			
			if("".equals(str.trim()))continue;
			
			str=str.toLowerCase();
			Matcher match=Pattern.compile(tableNameRegx).matcher(str);
			while(match.find()){
				name=match.group(1);
			}
			
			if(!"".equals(name)){
				temp="alert table "+name+" ";
				System.out.print(temp);
			}
			
			match=Pattern.compile(tableCommentRegx).matcher(str);
			while(match.find()){
				comment=match.group(1);
			}
			if(!"".equals(comment)){
				str=str.substring(0,str.indexOf("comment"))+";";
			}
			
			resultList.add(str);
			if(!"".equals(comment)){
				comment="comment '"+comment+"';";
				System.out.println(comment);
				resultList.add(temp+comment);
			}
			name=comment="";
		}
		
		FileUtils.writeLines(new File("E:/resultList.sql"), "UTF-8", resultList);
	}

}
