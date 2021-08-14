package com.mzl0101.dialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.ServiceManager;
import com.mzl0101.base.Pack;
import com.mzl0101.config.GlobalConfSetting;
import com.mzl0101.model.PackageBuildConfig;


import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author mzl
 */
public class BasePackDialog extends JDialog {
    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    /**
     * 任务号
     */
    private JTextField packageName;
    /**
     * 项目编译后的class路径
     */
    private JTextField classHomePath;
    /**
     * 输出目录
     */
    private JTextField outputHomePath;
    /**
     * 文件列表JSON字符串
     */
    private JTextArea filesJsonText;
    /**
     * 确定按钮
     */
    private JButton buildOkButton;
    /**
     * 取消按钮
     */
    private JButton buildCancelButton;
    /**
     *  解析按钮
     */
    private JButton parseButton;
    /**
     * 配置按钮
     */
    private JButton configButton;
    /**
    文件列表文本框
     */
    private JTextArea customizeFilesTexts;
    /**
     * 配置文本框
     */
    private JTextArea configPathText;
    /**
     * git获取按钮
     */
    private JButton gitGetButton;
    /**
     * 项目文本框
     */
    private JTextField projectText;
    /**
     * 项目路径
     */
    private String projectPath;
    /**
     * 项目名称
     */
    private String projectName;
    public BasePackDialog(String projectPath,String projectName) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buildOkButton);
        setLocation(400,200);
        buildOkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buildCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        //初始化路径文本内容
        initText();
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //解析JSON字符串事件
        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onParse();
            }
        });
        //配置事件
        configButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onConfig();
            }
        });
        //git获取事件
        gitGetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onGitGet();
            }
        });
    }
    private void onGitGet(){
        String projectPath = this.projectPath;
        if(projectPath!=null&&projectPath.length()>0){
            try {
                List<String> gitDiffFileList = new ArrayList<>();
                String osName = System.getProperty("os.name").toLowerCase();
                java.lang.Process process;
                if (osName.contains("win")) {
                    String drives = projectPath.substring(0, 2);
                    //git diff 获取文件列表
                    process = Runtime.getRuntime().exec("cmd /c " + drives + " && cd " + projectPath + " && git diff --name-only HEAD~ HEAD");
                } else {
                    process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "cd " + projectPath + " && git diff --name-only HEAD~ HEAD"});
                }
                InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "gbk");
                BufferedReader br = new BufferedReader(inputStreamReader);
                String line = "";
                line = br.readLine();
                while (line != null) {
                    gitDiffFileList.add(line);
                    line = br.readLine();
                }
                StringBuilder filesStr = new StringBuilder();
                int gitDiffFileListSize = gitDiffFileList.size();
                for(int i=0;i<gitDiffFileListSize;i++){
                    String temp = gitDiffFileList.get(i);
                    filesStr.append(temp);
                    if (i != gitDiffFileListSize - 1) {
                        filesStr.append(",");
                    }
                    filesStr.append("\n");
                }
                //设置文件列表值
                String originFilesStr = customizeFilesTexts.getText();
                if(originFilesStr!=null&&originFilesStr.length()!=0){
                    originFilesStr +=",\n";
                    originFilesStr += filesStr.toString();
                }else{
                    originFilesStr = filesStr.toString();
                }
                customizeFilesTexts.setText(originFilesStr);
            }catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "程序异常","提示", JOptionPane.ERROR_MESSAGE);
            }
        }

    }
    /**
     * 配置
     */
    private void onConfig(){
        String configText = configPathText.getText();
        if(configText==null||configText.length()<=0){
            JOptionPane.showMessageDialog(null, "配置不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Gson gson = new Gson();
        Map<String,String> confPathMap = gson.fromJson(configText, Map.class);
        GlobalConfSetting globalConfSetting = ServiceManager.getService(GlobalConfSetting.class);
        globalConfSetting.confPathMap = confPathMap;
        initText();
        JOptionPane.showMessageDialog(null,"配置成功","提示",JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * 解析方法
     */
    private void onParse(){
        String filesJson = filesJsonText.getText();
        Gson gson = new Gson();
        Map<String,Object> bitBucketRespMap = gson.fromJson(filesJson, Map.class);
        ArrayList<Object> values = (ArrayList<Object>) bitBucketRespMap.get("values");
        StringBuilder filesStr = new StringBuilder();
        int valuesSize = values.size();
        for(int i=0;i<valuesSize;i++){
            Map<String, Object> path = (Map<String, Object>)((Map<String, Object>) values.get(i)).get("path");
            String filePath = (String) path.get("toString");
            filesStr.append(filePath);
            if (i != valuesSize - 1) {
                filesStr.append(",");
            }
            filesStr.append("\n");
        }
        String originFilesStr = customizeFilesTexts.getText();
        if(originFilesStr!=null&&originFilesStr.length()!=0){
            originFilesStr +=",\n";
            originFilesStr += filesStr.toString();
        }else{
            originFilesStr = filesStr.toString();
        }
        //设置文件列表值
        customizeFilesTexts.setText(originFilesStr);
    }
    /**
     * 确定方法
     */
    private void onOK() {
        //判断是否支持该项目
        boolean support = false;
        if(this.projectName.toUpperCase().contains("FEIYI_INTERFACE")|| this.projectPath.toUpperCase().contains("YXT")||
                this.projectPath.toUpperCase().contains("FEIYI_WUZI")|| this.projectName.toUpperCase().contains("NETPLAT")){
            support = true;
        }
        if(!support){
            JOptionPane.showMessageDialog(null, "该插件不支持该项目打包","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String packageNameText = packageName.getText();
        if(packageNameText==null||packageNameText.length()<=0){
            JOptionPane.showMessageDialog(null, "任务号不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String classHomePathText = classHomePath.getText();
        if(classHomePathText==null||classHomePathText.length()<=0){
            JOptionPane.showMessageDialog(null, "项目对应tomcat路径不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String projectHomePathText = this.projectPath;
        String outputHomePathText = outputHomePath.getText();
        if(outputHomePathText==null||outputHomePathText.length()<=0){
            JOptionPane.showMessageDialog(null, "输出目录不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String projectTextValue = projectText.getText();
        String filesStr = customizeFilesTexts.getText();
        if(filesStr==null||filesStr.length()<=0){
            JOptionPane.showMessageDialog(null, "文件列表不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //回车换行去除
        filesStr = filesStr.replaceAll("[\\t\\n\\r]", "");
        List<String> filesList = Arrays.asList(filesStr.split(","));
        PackageBuildConfig packageBuildConfig = new PackageBuildConfig();
        packageBuildConfig.setPackageName(packageNameText.trim());
        if(classHomePathText.endsWith("/")){
            classHomePathText = classHomePathText.substring(0,classHomePathText.lastIndexOf("/"));
        }
        packageBuildConfig.setClassHomePath(classHomePathText);
        if(projectHomePathText.endsWith("/")){
            projectHomePathText = projectHomePathText.substring(0,projectHomePathText.lastIndexOf("/"));
        }
        packageBuildConfig.setProjectHomePath(projectHomePathText);
        if(outputHomePathText.endsWith("/")){
            outputHomePathText = outputHomePathText.substring(0,outputHomePathText.lastIndexOf("/"));
        }
        packageBuildConfig.setOutputHomePath(outputHomePathText);
        packageBuildConfig.setFilesList(filesList);
        packageBuildConfig.setProjectName(this.projectName);
        String result = Pack.pack(packageBuildConfig);
        if(!"".equals(result)){
            JOptionPane.showMessageDialog(null,result,"提示",JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "程序异常","提示", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * 取消方法
     */
    private void onCancel() {
        dispose();
    }

    /**
     * 初始化界面文本框内容
     */
    private void initText(){
        GlobalConfSetting globalConfSetting = ServiceManager.getService(GlobalConfSetting.class);
        Map<String,String> confPathMap = globalConfSetting.confPathMap;
        if(confPathMap!=null) {
            String fyHrpClassHomePath = confPathMap.get("FY_HRP_CLASS_HOME_PATH");
            String yxtHrpClassHomePath = confPathMap.get("YXT_HRP_CLASS_HOME_PATH");
            String netPlatClassHomePath = confPathMap.get("NET_PLAT_CLASS_HOME_PATH");
            String fyInterfaceClassHomePath = confPathMap.get("FY_INTERFACE_CLASS_HOME_PATH");
            String outputPath = confPathMap.get("OUTPUT_HOME_PATH");
            if(this.projectName.toUpperCase().contains("NETPLAT")){
                classHomePath.setText(netPlatClassHomePath);
                projectText.setText("NetPlat");
            }else if(this.projectPath.toUpperCase().contains("YXT")){
                classHomePath.setText(yxtHrpClassHomePath);
                projectText.setText("yxt");
            }else if(this.projectPath.toUpperCase().contains("FEIYI_WUZI")){
                classHomePath.setText(fyHrpClassHomePath);
                projectText.setText("feiyi_wuzi");
            }else if(this.projectName.toUpperCase().contains("FEIYI_INTERFACE")){
                classHomePath.setText(fyInterfaceClassHomePath);
                projectText.setText("feiyi_passages");
            }else{
                projectText.setText("");
            }
            outputHomePath.setText(outputPath);
            Gson gson =  new GsonBuilder().setPrettyPrinting().create();
            String configMapStr = gson.toJson(confPathMap);
            configPathText.setText(configMapStr);
            customizeFilesTexts.setText("");
        }
    }
}
