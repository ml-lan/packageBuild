package com.mzl0101.util;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author mzl
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
                    throw new RuntimeException("创建目标文件所在目录失败");
                }
            }
            Files.copy(source.toPath(), dest.toPath());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * list写入文件
     * @param strings
     * @param path
     * @throws Exception
     */
    public static void writeFileContext(List<String>  strings, String path) throws Exception {
        File file = new File(path);
        //如果没有文件就创建
        if (!file.isFile()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        for (String l : strings) {
            writer.write(l + "\r\n");
        }
        writer.close();
    }

    /**
     * 根据关键字搜索文件目录下的文件
     * @param folder
     * @param keyword
     * @return
     */
    public static List<File> searchFiles(File folder, String keyword) {
        List<File> result = new ArrayList<>();
        if(folder.isFile()){
            result.add(folder);
        }
        File[] subFolders = folder.listFiles();
        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.getName().contains(keyword)) {
                    result.add(file);
                }
            }
        }
        return result;
    }

    /**
     * 将fileToZip文件夹及其子目录文件递归压缩到zip文件中
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @throws IOException
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        //不压缩隐藏文件夹
        if (fileToZip.isHidden()) {
            return;
        }
        //判断压缩对象如果是一个文件夹
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                //如果文件夹是以“/”结尾，将文件夹作为压缩箱放入zipOut压缩输出流
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                //如果文件夹不是以“/”结尾，将文件夹结尾加上“/”之后作为压缩箱放入zipOut压缩输出流
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            //遍历文件夹子目录，进行递归的zipFile
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            //如果当前递归对象是文件夹，加入ZipEntry之后就返回
            return;
        }
        //如果当前的fileToZip不是一个文件夹，是一个文件，将其以字节码形式压缩到压缩包里面
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}