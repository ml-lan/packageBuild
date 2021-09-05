package com.mzl0101.base;

import com.mzl0101.model.PackageBuildModel;
import com.mzl0101.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @author mzl
 * @创建时间 2021/7/17 17:27
 * @描述 打包方法主类
 */
public class BasePack {
    /**
     * 打包方法
     * @param packageBuildModel
     * @return
     */
    public static Map<String,Object> pack(PackageBuildModel packageBuildModel) {
        String packageName = packageBuildModel.getPackageName();
        String outputHomePath = packageBuildModel.getOutputHomePath();
        List<String> filePathsList = packageBuildModel.getFilePathsList();
        Map<String,Object> result = new HashMap<>();
        result.put("status", false);
        try {
            String targetPackagePath = outputHomePath+File.separator+packageName;
            File targetPackagePathFile = new File(targetPackagePath);
            boolean success = targetPackagePathFile.mkdirs();
            String fileListPathText = outputHomePath+File.separator+packageName+File.separator+"fileList.txt";
            File fileListFile = new File(fileListPathText);
            if(success)
            {
                if(!fileListFile.exists()){
                    fileListFile.createNewFile();
                }
            }
            BufferedWriter fileListWriter = new BufferedWriter(new FileWriter(fileListPathText));
            int length = filePathsList.size();
            for(int i=0; i<length; i++){
                if(length-i !=1){
                    fileListWriter.write(filePathsList.get(i) + "," + "\r\n");
                }else{
                    fileListWriter.write(filePathsList.get(i));
                }
                replaceRuleMethod(packageBuildModel, filePathsList.get(i));
            }
            fileListWriter.close();
            // 对文件夹进行压缩
            FileOutputStream fos = new FileOutputStream(outputHomePath + File.separator + packageName+".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(outputHomePath + File.separator + packageName);
            //递归压缩文件夹
            FileUtil.zipFile(fileToZip, fileToZip.getName(), zipOut);
            //关闭输出流
            zipOut.close();
            fos.close();
            // 删除文件夹
            FileUtil.deleteDir(new File(outputHomePath+ File.separator + packageName));
            result.put("status", true);
            result.put("msg", "打包成功，共计" + length + "个文件");
        }
        catch (Exception e){
            e.printStackTrace();
            result.put("msg", e.getMessage().substring(0,20));
        }
        return result;
    }

    /**
     * 复制替换方法
     * @param packageBuildModel
     * @param filePath
     */
    private static void replaceRuleMethod(PackageBuildModel packageBuildModel, String filePath){
        String packageName = packageBuildModel.getPackageName();
        String classHomePath = packageBuildModel.getClassHomePath();
        String projectHomePath  = packageBuildModel.getProjectHomePath();
        String outputHomePath = packageBuildModel.getOutputHomePath();
        String src="",dest="";
        String fileSuffix = filePath.substring(filePath.lastIndexOf("."));
        //替换规则
        //1. 项目编译后的class文件
        if(".java".equals(fileSuffix)){
            //fileStr = fileStr.replace("src", "WEB-INF" + File.separator + "classes");
            //fileStr = fileStr.replace("WebContent", "");
            String copyFilePathForJava = filePath;
            //替换文件路径
            if(copyFilePathForJava.indexOf("src/main/java")!=-1){
                copyFilePathForJava = copyFilePathForJava.replace("src/main/java", "WEB-INF" + File.separator + "classes");
            }else{
                copyFilePathForJava = copyFilePathForJava.replace("src", "WEB-INF" + File.separator + "classes");
            }
            //替换文件后缀
            copyFilePathForJava = copyFilePathForJava.substring(0, copyFilePathForJava.lastIndexOf("."))+".class";
            //内部类处理
            String fileName = copyFilePathForJava.substring(copyFilePathForJava.lastIndexOf("/") + 1, copyFilePathForJava.lastIndexOf(".")) + "$";
            File folder = new File(classHomePath + copyFilePathForJava.substring(0, copyFilePathForJava.lastIndexOf("/")));
            List<File> innerFiles = FileUtil.searchFiles(folder, fileName);
            if (innerFiles!=null&&innerFiles.size()>0) {
                for (File innerFile : innerFiles) {
                    String fileDirPath = copyFilePathForJava.substring(0, copyFilePathForJava.lastIndexOf("/") + 1);
                    copyFilePathForJava = fileDirPath + innerFile.getName();
                    src = innerFile.getPath();
                    dest = outputHomePath + File.separator + packageName + File.separator + copyFilePathForJava;
                    FileUtil.copyFile(src, dest);
                }
            }
            src = classHomePath +File.separator + copyFilePathForJava;
            dest = outputHomePath + File.separator + packageName + File.separator + copyFilePathForJava;
            FileUtil.copyFile(src, dest);
        }
        //2. cpt报表文件
        else if(".cpt".equals(fileSuffix)){
            String copyFilePathForCpt = filePath;
            copyFilePathForCpt = copyFilePathForCpt.replace("WebContent","");
            src = classHomePath +File.separator + copyFilePathForCpt;
            dest = outputHomePath + File.separator + packageName + File.separator + copyFilePathForCpt;
            FileUtil.copyFile(src, dest);
        }
        //3. 项目静态文件
        else{
            String copyFilePathForOthers = filePath;
            src = projectHomePath +File.separator + copyFilePathForOthers;
            dest = outputHomePath + File.separator + packageName + File.separator + copyFilePathForOthers.replace("WebContent","");
            FileUtil.copyFile(src, dest);
        }
    }

}