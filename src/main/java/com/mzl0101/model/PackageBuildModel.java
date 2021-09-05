package com.mzl0101.model;


import lombok.Data;

import java.util.List;

/**
 * @author mzl
 * @创建时间 2021/7/17 17:28
 * @描述
 */
@Data
public class PackageBuildModel {
    /**
     * 任务号
     */
    private String packageName;
    /**
     * 项目对应tomcat路径
     */
    private String classHomePath;
    /**
     * 项目路径
     */
    private String projectHomePath;
    /**
     * 输出路径
     */
    private String outputHomePath;
    /**
     * 打包文件列表
     */
    private List<String> filePathsList;
}