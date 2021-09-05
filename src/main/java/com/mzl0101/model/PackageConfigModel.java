package com.mzl0101.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author mzl
 * @创建时间 2021/9/5 9:58
 * @描述
 */
@Data
public class PackageConfigModel {
    /**
     * 配置项目
     */
    private String configProject;
    /**
     * 配置class编译目录
     */
    private String configClassHomePath;
    /**
     * 配置输出目录
     */
    private String configOutputHomePath;
    /**
     * 配置项目路径
     */
    private String configProjectPath;

    @Override
    public int hashCode() {
        return configProject.hashCode() + configClassHomePath.hashCode() + configOutputHomePath.hashCode() + configProjectPath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PackageConfigModel that = (PackageConfigModel) o;
        return Objects.equals(configProject, that.configProject) && Objects.equals(configClassHomePath, that.configClassHomePath) && Objects.equals(configOutputHomePath, that.configOutputHomePath) && Objects.equals(configProjectPath, that.configProjectPath);
    }

    @Override
    public String toString(){
        return "PackageConfigModel [configProject="+configProject+", configClassHomePath="+configClassHomePath+", configOutputHomePath="+configOutputHomePath +",configProjectPath="+configProjectPath+"]";
    }
}