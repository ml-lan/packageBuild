package com.mzl0101.base;

import cn.hutool.core.util.ZipUtil;
import com.mzl0101.model.PathConfig;
import com.mzl0101.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @创建人 mzl
 * @创建时间 2021/7/17 17:27
 * @描述
 */
public class Pack {

    public static String PACKAGE_NAME; //任务号
    public static String CLASS_HOME_PATH; //项目对应tomcat路径
    public static String PROJECT_HOME_PATH; //项目路径
    public static String OUTPUT_HOME_PATH; //输出路径
    public static String CUSTOM_REPLACE_TEXT; //特殊字符处理
    public static List<String> FILES_LIST; //文件列表
    /**
     * 打包方法
     */
    public static String pack(PathConfig pathConfig) {
        //加载配置
        PACKAGE_NAME = pathConfig.getPackageName();
        CLASS_HOME_PATH = pathConfig.getClassHomePath();
        PROJECT_HOME_PATH = pathConfig.getProjectHomePath();
        OUTPUT_HOME_PATH = pathConfig.getOutputHomePath();
        CUSTOM_REPLACE_TEXT = pathConfig.getCustomReplaceText();
        FILES_LIST = pathConfig.getFilesList();
        String resultStr = "";
        try {
            //删除之前打包的打包目录
            String targetPath = OUTPUT_HOME_PATH+"\\"+PACKAGE_NAME;
            File outputDir = new File(targetPath);
            if (outputDir.exists()) {
                FileUtil.deleteDir(outputDir);
            }
            String fileList = "";
            int count = 0;
            for(String gitDiffFile: FILES_LIST){
                List<Map<String,String>> filePathList = getClassFilePath(getFilePath(gitDiffFile));
                for(Map<String,String> filePaths:filePathList){
                    String realFilePath = filePaths.get("realFilePath");
                    String copyFilePath = filePaths.get("copyFilePath");
                    String relativeFilePath = filePaths.get("relativeFilePath");
                    FileUtil.copyFile(realFilePath,copyFilePath);
                    fileList += (fileList==null||fileList.length()<=0)?relativeFilePath:(","+relativeFilePath);
                    count++;
                }
            }
            String fileListPath = OUTPUT_HOME_PATH+"\\"+PACKAGE_NAME+"\\fileList.txt";
            System.out.println("输出文件列表 >> "+fileListPath);
            Files.write(Paths.get(fileListPath), fileList.getBytes());
            System.out.println("打包成功，共计"+count+"个文件");
            resultStr = "打包成功，共计"+count+"个文件";
            //压缩打包文件
            ZipUtil.zip(targetPath);
            //删除已经打包的文件夹
            File delOutputDir = new File(targetPath);
            if (delOutputDir.exists()) {
                FileUtil.deleteDir(delOutputDir);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return resultStr;
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

    /**
     *
     * @param filePath
     * @return
     */
    private static List<String> getFilePath(String filePath){
        List<String> filePaths =  new ArrayList<>();
        if(filePath.contains("WebContent/")){
            filePath = filePath.substring(filePath.indexOf("WebContent/") + 11);
        }
        if(filePath.contains("src/")){
            filePath = filePath.substring(filePath.indexOf("src/") + 4);
            filePath = "WEB-INF/classes/"+ filePath;
        }
        if(CUSTOM_REPLACE_TEXT!=null&&CUSTOM_REPLACE_TEXT.length()>0){
            String matchText = CUSTOM_REPLACE_TEXT.split("-->")[0].trim();
            String replaceText = CUSTOM_REPLACE_TEXT.split("-->")[1].trim();
            if(filePath.indexOf(matchText)!=-1){
                filePath = filePath.replaceAll(matchText,replaceText);
            }
        }
        if(filePath.indexOf("SQL")!=-1||filePath.indexOf("sql")!=-1){
            filePaths.add(PROJECT_HOME_PATH +"\\"+ filePath);
            filePaths.add(OUTPUT_HOME_PATH+"\\"+PACKAGE_NAME +"\\"+ filePath);
            filePaths.add(filePath);
            return filePaths;
        }
        if(filePath.indexOf("js")!=-1){
            filePaths.add(CLASS_HOME_PATH +"\\"+ filePath);
            filePaths.add(OUTPUT_HOME_PATH +"\\"+PACKAGE_NAME +"\\"+ filePath);
            filePaths.add(filePath);
            return filePaths;
        }
        if(filePath.indexOf("jsp")!=-1){
            filePaths.add(CLASS_HOME_PATH +"\\"+ filePath);
            filePaths.add(OUTPUT_HOME_PATH +"\\"+PACKAGE_NAME+"\\"+ filePath);
            filePaths.add(filePath);
            return filePaths;
        }
        if(filePath.indexOf("java")!=-1){
            filePath = filePath.replaceAll("\\.java","\\.class");
            filePaths.add(CLASS_HOME_PATH +"\\"+ filePath);
            filePaths.add(OUTPUT_HOME_PATH +"\\"+PACKAGE_NAME +"\\"+ filePath);
            filePaths.add(filePath);
            return filePaths;
        }
        filePaths.add(CLASS_HOME_PATH +"\\"+ filePath);
        filePaths.add(OUTPUT_HOME_PATH +"\\"+PACKAGE_NAME+"\\"+ filePath);
        filePaths.add(filePath);
        return filePaths;
    }
}