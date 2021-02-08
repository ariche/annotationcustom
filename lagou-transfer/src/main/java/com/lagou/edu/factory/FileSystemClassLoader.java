package com.lagou.edu.factory;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjDoubleConsumer;

@Slf4j
public class FileSystemClassLoader extends ClassLoader {


    private HashMap<Object, File> fileMap;

    private static FileSystemClassLoader loader=new FileSystemClassLoader();

    private FileSystemClassLoader(){ }

    public static FileSystemClassLoader getInstance(String className,File file){
        HashMap<Object, File> objectObjectHashMap = new HashMap<>(4);
        objectObjectHashMap.put(className,file);
        loader.setFileMap(objectObjectHashMap);
        return loader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        if (null==fileMap.get(name)){
            return null;
        }
       // log.info("777777777777777777"+name+fileMap.get(name).getAbsolutePath()+Thread.currentThread().getId());
        byte[] classBytes = new byte[0];
        try {
            classBytes = getClassBytes(fileMap.get(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class<?> aClass = defineClass(name, classBytes, 0, classBytes.length);

        if (aClass!=null){
            return aClass;
        }

        return super.findClass(name);
    }


    private byte[] getClassBytes(File classFile) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(classFile);
        byte[] bytes = new byte[(int) classFile.length()];

        int read = fileInputStream.read(bytes);

        if (read!=0){
            return bytes;
        }
        return null;
    }


    private void setFileMap(HashMap<Object,File> fileMap) {
        this.fileMap = fileMap;
    }
}
