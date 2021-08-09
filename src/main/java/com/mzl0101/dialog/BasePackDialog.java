package com.mzl0101.dialog;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.ServiceManager;
import com.mzl0101.base.Pack;
import com.mzl0101.config.GlobalConfSetting;
import com.mzl0101.model.PathConfig;


import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BasePackDialog extends JDialog {
    private JPanel contentPane;
    private JButton buildOk; //打包
    private JButton buildCancel; //取消
    private JTextField packageName; //任务号
    private JTextField classHomePath; //class路径
    private JTextField outputHomePath; //输出目录
    private JTextArea filesJsonText; //文件列表JSON字符串
    private JButton parseButton; //解析按钮
    private JButton configButton; //配置按钮
    private JTextArea customizeFilesTexts; //文件列表
    private JTabbedPane tabbedPane1;
    private JTextArea configPathText; //配置文本
    private JButton gitGetButton;
    private JTextField projectText; //项目
    private String projectPath;
    public BasePackDialog(String projectPath) {
        this.projectPath = projectPath;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buildOk);
        setLocation(400,200);
        buildOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buildCancel.addActionListener(new ActionListener() {
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
        if(StrUtil.isNotBlank(projectPath)){
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
                customizeFilesTexts.setText(filesStr.toString());
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
        if(StrUtil.isNotBlank(originFilesStr)){
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
        String packageNameText = packageName.getText();//任务号
        String classHomePathText = classHomePath.getText(); //tomcat路径
        String projectHomePathText = this.projectPath;
        String outputHomePathText = outputHomePath.getText();//输出路径
        String projectTextValue = projectText.getText();//项目
        String filesStr = customizeFilesTexts.getText();//文件列表
        filesStr = filesStr.replaceAll("\n", "");
        List<String> filesList = Arrays.asList(filesStr.split(","));
        PathConfig pathConfig = new PathConfig();
        pathConfig.setPackageName(packageNameText);
        pathConfig.setClassHomePath(classHomePathText.replaceAll("/", "\\\\"));
        pathConfig.setProjectHomePath(projectHomePathText.replaceAll("/", "\\\\"));
        pathConfig.setOutputHomePath(outputHomePathText.replaceAll("/", "\\\\"));
        pathConfig.setCustomReplaceText("");
        pathConfig.setFilesList(filesList);
        if(projectTextValue.equals("feiyi_passages")){
            pathConfig.setCustomReplaceText("main/java/ --> ");
        }
        String result = Pack.pack(pathConfig);
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

    private static String toPrettyFormat(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    private void initText(){
        //初始化路径文本内容
        GlobalConfSetting globalConfSetting = ServiceManager.getService(GlobalConfSetting.class);
        Map<String,String> confPathMap = globalConfSetting.confPathMap;
        if(confPathMap!=null) {
            String feiyiHrpClassHomePath = confPathMap.get("FEIYI_HRP_CLASS_HOME_PATH");
            String yxtHrpClassHomePath = confPathMap.get("YXT_HRP_CLASS_HOME_PATH");
            String netplatClassHomePath = confPathMap.get("NETPLAT_CLASS_HOME_PATH");
            String feiyiInterfaceClassHomePath = confPathMap.get("FEIYI_INTERFACE_CLASS_HOME_PATH");
            String outputPath = confPathMap.get("OUTPUT_HOME_PATH");
            //供应链
            if(projectPath.endsWith("NetPlat")){
                classHomePath.setText(StrUtil.isNotBlank(netplatClassHomePath) ? netplatClassHomePath : "");
                projectText.setText("NetPlat");
            }else if(projectPath.endsWith("yxt")){
                classHomePath.setText(StrUtil.isNotBlank(yxtHrpClassHomePath) ? yxtHrpClassHomePath : "");
                projectText.setText("yxt");
            }else if(projectPath.endsWith("feiyi_wuzi")){
                classHomePath.setText(StrUtil.isNotBlank(feiyiHrpClassHomePath) ? feiyiHrpClassHomePath : "");
                projectText.setText("feiyi_wuzi");
            }else if(projectPath.endsWith("feiyi_passages")){
                classHomePath.setText(StrUtil.isNotBlank(feiyiInterfaceClassHomePath) ? feiyiInterfaceClassHomePath : "");
                projectText.setText("feiyi_passages");
            }
            outputHomePath.setText(StrUtil.isNotBlank(outputPath) ? outputPath : "");
            projectText.setText("");
            Gson gson = new Gson();
            String configMapStr = toPrettyFormat(gson.toJson(confPathMap));
            configPathText.setText(configMapStr);
            customizeFilesTexts.setText("");
        }
    }
}
