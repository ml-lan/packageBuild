package com.mzl0101.util;

import com.google.gson.Gson;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
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

    public static Map<String,String> getGlobalConfig(){
        //获取application容器
        Application application = ApplicationManager.getApplication();
        //获取application容器中的组件
        GlobalConfig globalConfig = application.getComponent(GlobalConfig.class);
        if(globalConfig!=null){
            return globalConfig.globalConfigMap;
        }
        return new HashMap<String,String>();
    }

    public static void setGlobalConfig(Map<String,String> globalConfigMap){
        //获取application容器
        Application application = ApplicationManager.getApplication();
        //获取application容器中的组件
        GlobalConfig globalConfig = application.getComponent(GlobalConfig.class);
        if(globalConfig!=null){
            globalConfig.globalConfigMap = globalConfigMap;
        }
    }

    /**
     * 检测是否存在
     * @param configProject
     * @return
     */
    public static boolean checkProjectExist(String configProject){
        Map<String,String> globalConfigMap = getGlobalConfig();
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
        Map<String,String> globalConfigMap = getGlobalConfig();
        if(globalConfigMap!=null&&!globalConfigMap.isEmpty()){
            globalConfigMap.get(configProject);
            Gson gson = new Gson();
            PackageConfigModel packageConfigModel = gson.fromJson(globalConfigMap.get(configProject), PackageConfigModel.class);
            return packageConfigModel;
        }
        return null;
    }

    /**
     * 增加、修改方法
     * @param packageConfigModel
     */
    public static void addOrUpdateGlobalConfig(PackageConfigModel packageConfigModel){
        Map<String,String> globalConfigMap = getGlobalConfig();
        if(globalConfigMap!=null&&!globalConfigMap.isEmpty()){
            Gson gson = new Gson();
            String packageConfigModelStr = gson.toJson(packageConfigModel);
            globalConfigMap.put(packageConfigModel.getConfigProject(),packageConfigModelStr);
            setGlobalConfig(globalConfigMap);
        }
    }

    /**
     * 移除方法
     * @param configProject
     */
    public static void removeGlobalConfig(String configProject){
        Map<String,String> globalConfigMap = getGlobalConfig();
        if(globalConfigMap!=null&&!globalConfigMap.isEmpty()){
            Iterator<String> iter = globalConfigMap.keySet().iterator();
            while(iter.hasNext()){
                String key = iter.next();
                if(configProject.equals(key)){
                    iter.remove();
                }
            }
            setGlobalConfig(globalConfigMap);
        }
    }

    /**
     * 转换为二维数组
     * @return
     */
    public static String [][] convertTwoGlobalConfig(){
        Map<String,String> globalConfigMap = getGlobalConfig();
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
        Map<String,String> globalConfigMap = getGlobalConfig();
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