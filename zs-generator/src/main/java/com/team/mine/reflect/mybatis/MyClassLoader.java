package com.team.mine.reflect.mybatis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
 
public class MyClassLoader extends ClassLoader {
    //类存放的路径
    private String path = "";
 
    public String getPath() {
        return path;
    }
 
    public void setPath(String path) {
        this.path = path;
    }
 
    
    
    MyClassLoader() {
    }
     
    MyClassLoader(ClassLoader parent) {
        super(parent);
    }
     
    /**
     * 重写findClass方法
     */
    @Override
    public Class<?> findClass(String name) {  
        System.out.println("loadClass:"+name);
        byte[] data = loadClassData(name);
        return this.defineClass(name, data, 0, data.length);
    }
    public byte[] loadClassData(String name) {
        try {
            name = name.replace(".", "//");
            FileInputStream is = new FileInputStream(new File(path + name + ".class"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b = 0;
            while ((b = is.read()) != -1) {
                baos.write(b);
            }
            is.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
}
