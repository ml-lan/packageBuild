package com.mzl0101.util;

import com.google.gson.Gson;
import com.intellij.openapi.components.ServiceManager;
import com.mzl0101.config.GlobalConfig;
import com.mzl0101.model.PackageConfigModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author mzl
 * @创建时间 2021/9/5 9:56
 * @描述
 */
public class GlobalConfigUtil {


    /**
     * 检测是否存在
     * @param configProject
     * @return
     */
    public static boolean checkProjectExist(String configProject){
        GlobalConfig globalConfig = ServiceManager.getService(GlobalConfig.class);
        Map<String,String> globalConfigMap = globalConfig.globalConfigMap;
        if(globalConfigMap==null||globalConfigMap.isEmpty()){
            return false;
        }
        return globalConfigMap.containsKey(configProject);
    }
    /**
     * 获取方法
     * @param configProject
     * @return
     */
    public static PackageConfigModel getGlobalConfig(String configProject){
        GlobalConfig globalConfig = ServiceManager.getService(GlobalConfig.class);
        Map<String,String> globalConfigMap = globalConfig.globalConfigMap;
        globalConfigMap.get(configProject);
        Gson gson = new Gson();
        PackageConfigModel packageConfigModel = gson.fromJson(globalConfigMap.get(configProject), PackageConfigModel.class);
        return packageConfigModel;
    }

    /**
     * 增加、修改方法
     * @param packageConfigModel
     */
    public static void addOrUpdateGlobalConfig(PackageConfigModel packageConfigModel){
        GlobalConfig globalConfig = ServiceManager.getService(GlobalConfig.class);
        Map<String,String> globalConfigMap = globalConfig.globalConfigMap;
        if(globalConfigMap==null){
            globalConfigMap = new HashMap<>();
        }
        Gson gson = new Gson();
        String packageConfigModelStr = gson.toJson(packageConfigModel);
        globalConfigMap.put(packageConfigModel.getConfigProject(),packageConfigModelStr);
        globalConfig.globalConfigMap = globalConfigMap;
    }

    /**
     * 移除方法
     * @param configProject
     */
    public static void removeGlobalConfig(String configProject){
        GlobalConfig globalConfig = ServiceManager.getService(GlobalConfig.class);
        Map<String,String> globalConfigMap = globalConfig.globalConfigMap;
        Iterator<String> iter = globalConfigMap.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            if(configProject.equals(key)){
                iter.remove();
            }
        }
        globalConfig.globalConfigMap = globalConfigMap;
    }

    /**
     * 转换为二维数组
     * @return
     */
    public static String [][] convertTwoGlobalConfig(){
        GlobalConfig globalConfig = ServiceManager.getService(GlobalConfig.class);
        Map<String,String> globalConfigMap = globalConfig.globalConfigMap;
        if(globalConfigMap!=null && !globalConfigMap.isEmpty()){
            int size = globalConfigMap.size();
            String [][] result = new String[size][4];
            Iterator iterator = globalConfigMap.entrySet().iterator();
            for(int i=0;i<size;i++){
                Map.Entry entry = (Map.Entry) iterator.next();
                String value = (String) entry.getValue();
                Gson gson = new Gson();
                PackageConfigModel packageConfigModel = gson.fromJson(value, PackageConfigModel.class);
                if(packageConfigModel!=null){
                    result[i][0] = packageConfigModel.getConfigProject();
                    result[i][1] = packageConfigModel.getConfigProjectPath();
                    result[i][2] = packageConfigModel.getConfigClassHomePath();
                    result[i][3] = packageConfigModel.getConfigOutputHomePath();
                }
            }
            return result;
        }
        return new String[0][];
    }

    /**
     * 转换一维数组
     * @return
     */
    public static String [] convertOneGlobalConfig(){
        GlobalConfig globalConfig = ServiceManager.getService(GlobalConfig.class);
        Map<String,String> globalConfigMap = globalConfig.globalConfigMap;
        if(globalConfigMap!=null && !globalConfigMap.isEmpty()){
            int size = globalConfigMap.size();
            String [] result = new String[size];
            Iterator iterator = globalConfigMap.entrySet().iterator();
            for(int i=0;i<size;i++){
                Map.Entry entry = (Map.Entry) iterator.next();
                result[i] = (String) entry.getKey();
            }
            return result;
        }
        return new String[0];
    }
}