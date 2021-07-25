package com.mzl0101.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建人 mzl
 * @创建时间 2021/7/17 16:59
 * @描述
 */
public class FileUtil {

    /**
     * 删除目录及其目录下的文件方法
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 复制文件
     * @param srcPath
     * @param desPath
     */
    public static void copyFile(String srcPath,String desPath){
        try{
            File source = new File(srcPath);
            File dest = new File(desPath);
            if(!dest.getParentFile().exists()) {
                if(!dest.getParentFile().mkdirs()) {
                    System.out.println("创建目标文件所在目录失败！");
                    throw new RuntimeException("创建目标文件所在目录失败");
                }
            }
            long start = System.nanoTime();
            System.out.println("复制文件开始: "+ srcPath + " -> " + desPath);
            Files.copy(source.toPath(), dest.toPath());
            System.out.println("复制文件结束,用时: " + (System.nanoTime() - start)/1000 + "ms" );
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     *
     * @param filePath
     * @return
     */
    private static List<Map<String, String>> getClassFilePath(List<String> filePath) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        String realFilePath = filePath.get(0);
        String copyFilePath = filePath.get(1);
        String relativeFilePath = filePath.get(2);
        if (realFilePath.endsWith(".class")){
            File realFile = new File(realFilePath);
            File parentDir = realFile.getParentFile();
            if(!realFile.exists()){
                throw new RuntimeException(realFilePath+"文件不存在");
            }

            if(!parentDir.exists()){
                throw new RuntimeException(realFile.getParentFile().getAbsolutePath()+"目录不存在");
            }

            FilenameFilter fileNameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String realFileName = realFile.getName();
                    if(name.equals(realFileName)){
                        return true;
                    }
                    if(name.lastIndexOf(".")>0 && name.startsWith(realFileName.split("\\.")[0]+"$"))
                    {
                        return true;
                    }
                    return false;
                }
            };

            File copyFile = new File(copyFilePath);
            String copyFilePathNew = copyFile.getParentFile().getAbsolutePath();
            File[] files = parentDir.listFiles(fileNameFilter);
            for(File file: files){
                //计算相对路径
                String relativeFilePathNew = relativeFilePath.substring(0,relativeFilePath.lastIndexOf("/"));
                relativeFilePathNew += (("/"+file.getName()).replaceAll("\\.class","\\.java"));

                Map<String,String> classFilePaths = new HashMap<>();
                classFilePaths.put("realFilePath",file.getAbsolutePath());
                classFilePaths.put("copyFilePath",copyFilePathNew+"\\"+file.getName());
                classFilePaths.put("relativeFilePath",relativeFilePathNew);
                result.add(classFilePaths);
            }

        }else{
            Map<String,String> classFilePaths = new HashMap<>();
            classFilePaths.put("realFilePath",realFilePath);
            classFilePaths.put("copyFilePath",copyFilePath);
            classFilePaths.put("relativeFilePath",relativeFilePath);
            result.add(classFilePaths);
        }
        return result;
    }

}