package com.mzl0101.base;

import com.mzl0101.model.PackageBuildConfig;
import com.mzl0101.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipOutputStream;

import static com.mzl0101.util.FileUtil.copyFile;

/**
 * @author mzl
 * @创建时间 2021/7/17 17:27
 * @描述 打包方法主类
 */
public class Pack {

    /**
     * 路径分隔符
     */
    public static String SEPARATOR = File.separator;
    public static String PACKAGE_NAME = "";
    public static String CLASS_HOME_PATH = "";
    public static String PROJECT_HOME_PATH = "";
    public static String OUTPUT_HOME_PATH = "";
    public static String PROJECT_NAME =  "";
    /**
     * 打包方法
     */
    public static String pack(PackageBuildConfig packageBuildConfig) {
        PACKAGE_NAME = packageBuildConfig.getPackageName();
        CLASS_HOME_PATH = packageBuildConfig.getClassHomePath();
        PROJECT_HOME_PATH = packageBuildConfig.getProjectHomePath();
        OUTPUT_HOME_PATH = packageBuildConfig.getOutputHomePath();
        PROJECT_NAME = packageBuildConfig.getProjectName();
        List<String> filesList = packageBuildConfig.getFilesList();
        String resultStr = "";
        try {
            String targetPackagePath = OUTPUT_HOME_PATH+SEPARATOR+PACKAGE_NAME;
            File targetPackagePathFile = new File(targetPackagePath);
            boolean success = targetPackagePathFile.mkdirs();
            String fileListPathText = OUTPUT_HOME_PATH+SEPARATOR+PACKAGE_NAME+SEPARATOR+"fileList.txt";
            File fileListFile = new File(fileListPathText);
            if(success)
            {
                if(!fileListFile.exists()){
                    fileListFile.createNewFile();
                }
            }
            BufferedWriter fileListWriter = new BufferedWriter(new FileWriter(fileListPathText));
            for(String fileStr: filesList){
                //写入fileList.txt 文件
                fileListWriter.write(fileStr + "\r\n");
                if(PROJECT_NAME.toUpperCase().contains("FEIYI_INTERFACE")){
                    replaceRuleFyInterface(fileStr);
                }else if((PROJECT_HOME_PATH.toUpperCase().contains("YXT")||PROJECT_HOME_PATH.toUpperCase().contains("FEIYI_WUZI"))){
                    replaceRuleFyOrYxt(fileStr);
                }else if(PROJECT_NAME.toUpperCase().contains("NETPLAT")){
                    replaceRuleNetPlat(fileStr);
                }
            }
            fileListWriter.close();
            resultStr = "打包成功，共计" +filesList.size() + "个文件";
            // 对文件夹进行压缩
            FileOutputStream fos = new FileOutputStream(OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME+".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME);
            //递归压缩文件夹
            FileUtil.zipFile(fileToZip, fileToZip.getName(), zipOut);
            //关闭输出流
            zipOut.close();
            fos.close();
            // 删除文件夹
            FileUtil.deleteDir(new File(OUTPUT_HOME_PATH+ SEPARATOR + PACKAGE_NAME));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return resultStr;
    }

    /**
     * Fy或者Yx项目替换复制方法
     * @param fileStr
     * @return
     */
    private static void replaceRuleFyOrYxt(String fileStr){
        String src="",dest="";
        String fileSuffix = fileStr.substring(fileStr.lastIndexOf("."));
        if(".java".equalsIgnoreCase(fileSuffix)||".js".equalsIgnoreCase(fileSuffix)||".jsp".equalsIgnoreCase(fileSuffix)){
            fileStr = fileStr.replace("src", "WEB-INF" + SEPARATOR + "classes");
            fileStr = fileStr.replace("WebContent", "");
        }
        //1. java文件
        if(".java".equals(fileSuffix)){
            fileStr= fileStr.substring(0, fileStr.lastIndexOf("."))+".class";
            //内部类
            String fileName = fileStr.substring(fileStr.lastIndexOf("/") + 1, fileStr.lastIndexOf("" +
                    ".")) + "$";
            File folder = new File(CLASS_HOME_PATH + fileStr.substring(0, fileStr.lastIndexOf("/")));
            List<File> innerFiles = FileUtil.searchFiles(folder, fileName);
            if (innerFiles!=null&&innerFiles.size()>0) {
                for (File innerFile : innerFiles) {
                    String fileDirPath = fileStr.substring(0, fileStr.lastIndexOf("/") + 1);
                    fileStr = fileDirPath + innerFile.getName();
                    src = innerFile.getPath();
                    dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
                    copyFile(src, dest);
                }
            }
            src = CLASS_HOME_PATH +SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
        //2. SQL脚本
        else if (".SQL".equalsIgnoreCase(fileSuffix)) {
            src = PROJECT_HOME_PATH + SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR+ "SQL" + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
        else{
            //复制文件
            src = CLASS_HOME_PATH +SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
    }

    /**
     * 接口项目替换复制方法
     * @param fileStr
     */
    private static void replaceRuleFyInterface(String fileStr){
        String src="",dest="";
        //文件后缀
        String fileSuffix = fileStr.substring(fileStr.lastIndexOf("."));
        //根据文件类型进行不同的处理
        //1. java文件
        if(".java".equals(fileSuffix)){
            fileStr = fileStr.replace("src", "WEB-INF" + SEPARATOR + "classes");
            fileStr = fileStr.replace("main/java/", "");
            fileStr= fileStr.substring(0, fileStr.lastIndexOf("."))+".class";
            //内部类
            String fileName = fileStr.substring(fileStr.lastIndexOf("/") + 1, fileStr.lastIndexOf("" +
                    ".")) + "$";
            File folder = new File(CLASS_HOME_PATH + fileStr.substring(0, fileStr.lastIndexOf("/")));
            List<File> innerFiles = FileUtil.searchFiles(folder, fileName);
            if (innerFiles!=null&&innerFiles.size()>0) {
                for (File innerFile : innerFiles) {
                    String fileDirPath = fileStr.substring(0, fileStr.lastIndexOf("/") + 1);
                    fileStr = fileDirPath + innerFile.getName();
                    src = innerFile.getPath();
                    dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
                    copyFile(src, dest);
                }
            }
            src = CLASS_HOME_PATH +SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
        //2. SQL脚本
        else if (".SQL".equalsIgnoreCase(fileSuffix)) {
            fileStr = fileStr.replace("src/main/webapp/", "");
            src = CLASS_HOME_PATH + SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + "SQL" + SEPARATOR +fileStr;
            copyFile(src, dest);
        }
        //其他
        else{
            src = CLASS_HOME_PATH +SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
    }

    /**
     * 供应链项目替换复制方法
     * @param fileStr
     */
    private static void replaceRuleNetPlat(String fileStr){
        String src="",dest="";
        String fileSuffix = fileStr.substring(fileStr.lastIndexOf("."));
        if(".java".equalsIgnoreCase(fileSuffix)||".js".equalsIgnoreCase(fileSuffix)||".jsp".equalsIgnoreCase(fileSuffix)){
            fileStr = fileStr.replace("src", "WEB-INF" + SEPARATOR + "classes");
            fileStr = fileStr.replace("WebContent", "");
        }
        //1. java文件
        if(".java".equals(fileSuffix)){
            fileStr= fileStr.substring(0, fileStr.lastIndexOf("."))+".class";
            //内部类
            String fileName = fileStr.substring(fileStr.lastIndexOf("/") + 1, fileStr.lastIndexOf("" +
                    ".")) + "$";
            File folder = new File(CLASS_HOME_PATH + fileStr.substring(0, fileStr.lastIndexOf("/")));
            List<File> innerFiles = FileUtil.searchFiles(folder, fileName);
            if (innerFiles!=null&&innerFiles.size()>0) {
                for (File innerFile : innerFiles) {
                    String fileDirPath = fileStr.substring(0, fileStr.lastIndexOf("/") + 1);
                    fileStr = fileDirPath + innerFile.getName();
                    src = innerFile.getPath();
                    dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
                    copyFile(src, dest);
                }
            }
            src = CLASS_HOME_PATH +SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
        //2. SQL脚本
        else if (".SQL".equalsIgnoreCase(fileSuffix)) {
            src = PROJECT_HOME_PATH + SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + "SQL" + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
        else{
            //复制文件
            src = CLASS_HOME_PATH +SEPARATOR + fileStr;
            dest = OUTPUT_HOME_PATH + SEPARATOR + PACKAGE_NAME + SEPARATOR + fileStr;
            copyFile(src, dest);
        }
    }

}