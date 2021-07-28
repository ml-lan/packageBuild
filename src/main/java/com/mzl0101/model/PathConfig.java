package com.mzl0101.model;

import java.util.List;

/**
 * @创建人 mzl
 * @创建时间 2021/7/17 17:28
 * @描述
 */
public class PathConfig {
    private String packageName; //任务号
    private String classHomePath; //项目对应tomcat路径
    private String projectHomePath; //项目路径
    private String outputHomePath; //输出路径
    private String customReplaceText; //特殊字符处理
    private List<String> filesList; //打包文件列表
    public List<String> getFilesList() {
        return filesList;
    }

    public void setFilesList(List<String> filesList) {
        this.filesList = filesList;
    }
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassHomePath() {
        return classHomePath;
    }

    public void setClassHomePath(String classHomePath) {
        this.classHomePath = classHomePath;
    }

    public String getProjectHomePath() {
        return projectHomePath;
    }

    public void setProjectHomePath(String projectHomePath) {
        this.projectHomePath = projectHomePath;
    }

    public String getOutputHomePath() {
        return outputHomePath;
    }

    public void setOutputHomePath(String outputHomePath) {
        this.outputHomePath = outputHomePath;
    }

    public String getCustomReplaceText() {
        return customReplaceText;
    }

    public void setCustomReplaceText(String customReplaceText) {
        this.customReplaceText = customReplaceText;
    }

}