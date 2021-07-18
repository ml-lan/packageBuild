package com.mzl0101.dialog;

import com.google.gson.Gson;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.mzl0101.base.Pack;
import com.mzl0101.config.GlobalConfSetting;
import com.mzl0101.config.GlobalProjectConfSetting;
import com.mzl0101.model.PathConfig;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.Map;

public class BasePackDialog extends JDialog {
    private JPanel contentPane;
    private JButton buildOk;
    private JButton buildCancel;
    private JTextField packageName;
    private JTextField classHomePath;
    private JTextField projectHomePath;
    private JList funcList;
    private JTextField outputHomePath;
    private JButton globalConf;
    private JTextArea confAllPath;
    private JButton clearGlobalConf;

    public BasePackDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buildOk);
        setLocation(400,200);
        buildOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buildCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        globalConf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onGlobalConf();
            }
        });

        clearGlobalConf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onClearGlobalConf();
            }
        });
        //列表事件
        funcList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                System.out.println(funcList.getSelectedIndex()+"---"+funcList.getSelectedValue());
                GlobalProjectConfSetting globalProjectConfSetting = ServiceManager.getService(GlobalProjectConfSetting.class);
                GlobalConfSetting globalConfSetting = ServiceManager.getService(GlobalConfSetting.class);
                Map<String,String> confPathMap = globalConfSetting.confPathMap;
                String projectNum = (String) funcList.getSelectedValue();
                globalProjectConfSetting.projectNum = projectNum;
                //设置值
                //如果项目编号不为空
                if(StringUtils.isNotBlank(projectNum)&&confPathMap!=null) {
                    for (Map.Entry<String, String> entry : confPathMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        System.out.println("key = " + key + ", value = " + value);
                        if ((projectNum + "_CLASS_HOME_PATH").equals(key)) {
                            classHomePath.setText(StringUtils.isNotBlank(value) ? value : "");
                        } else if ((projectNum + "_PROJECT_HOME_PATH").equals(key)) {
                            projectHomePath.setText(StringUtils.isNotBlank(value) ? value : "");
                        } else if ((projectNum + "_OUTPUT_HOME_PATH").equals(key)) {
                            outputHomePath.setText(StringUtils.isNotBlank(value) ? value : "");
                        }
                    }
                }
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        GlobalConfSetting globalConfSetting = ServiceManager.getService(GlobalConfSetting.class);
        GlobalProjectConfSetting globalProjectConfSetting = ServiceManager.getService(GlobalProjectConfSetting.class);
        Map<String,String> confPathMap = globalConfSetting.confPathMap;
        String projectNum = globalProjectConfSetting.projectNum;
        if(confPathMap!=null){
            Gson gson = new Gson();
            String confPathStr = gson.toJson(confPathMap);
            confAllPath.setText(confPathStr);
            //如果项目编号不为空
            if(StringUtils.isNotBlank(projectNum)){
                for (Map.Entry<String, String> entry : confPathMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    System.out.println("key = " + key + ", value = " + value);
                    if((projectNum+"_CLASS_HOME_PATH").equals(key)){
                        classHomePath.setText(StringUtils.isNotBlank(value)?value:"");
                    }
                    else if((projectNum+"_PROJECT_HOME_PATH").equals(key)){
                        projectHomePath.setText(StringUtils.isNotBlank(value)?value:"");
                    } else if((projectNum+"_OUTPUT_HOME_PATH").equals(key)){
                        outputHomePath.setText(StringUtils.isNotBlank(value)?value:"");
                    }
                }
            }else{
                //默认HRP项目配置
                String classHomPathText = confPathMap.get("YXT_HRP_CLASS_HOME_PATH");
                String projectHomePathText = confPathMap.get("YXT_HRP_PROJECT_HOME_PATH");
                String outputHomePathText = confPathMap.get("YXT_HRP_OUTPUT_HOME_PATH");
                classHomePath.setText(StringUtils.isNotBlank(classHomPathText)?classHomPathText:"");
                projectHomePath.setText(StringUtils.isNotBlank(projectHomePathText)?projectHomePathText:"");
                outputHomePath.setText(StringUtils.isNotBlank(outputHomePathText)?outputHomePathText:"");
            }
        }
        //设置默认值
        funcList.setSelectedIndex(0);
        if(StringUtils.isNotBlank(projectNum))
        {
            for(int i=0;i<funcList.getModel().getSize();i++){
                if(funcList.getModel().getElementAt(i).equals(projectNum)){
                    funcList.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void onOK() {
        //确定按钮执行方法
        String packageNameText = packageName.getText();//任务号
        String classHomePathText = classHomePath.getText(); //tomcat
        String projectHomePathText = projectHomePath.getText();//项目
        String outputHomePathText = outputHomePath.getText();//输出
        String funcNum = funcList.getSelectedValue().toString();
        System.out.println(funcNum);
        PathConfig pathConfig = new PathConfig();
        pathConfig.setPackageName(packageNameText);
        pathConfig.setClassHomePath(classHomePathText);
        pathConfig.setProjectHomePath(projectHomePathText);
        pathConfig.setOutputHomePath(outputHomePathText);
        pathConfig.setCustomReplaceText("");
        if(funcNum.equals("FEIYI_INTERFACE")){
            pathConfig.setCustomReplaceText("main/java/ --> ");
        }
        String result = Pack.pack(pathConfig);
        if(!"".equals(result)){
            JOptionPane.showMessageDialog(null,result,"提示",JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "程序异常","提示", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /**
     * 全局配置路径
     */
    private void onGlobalConf(){
        String confAllPathText = confAllPath.getText();
        Gson gson = new Gson();
        Map<String,String> confPathMap = gson.fromJson(confAllPathText, Map.class);
        GlobalConfSetting globalConfSetting = ServiceManager.getService(GlobalConfSetting.class);
        globalConfSetting.confPathMap = confPathMap;
        String funcNum = funcList.getSelectedValue().toString();
        if(StringUtils.isNotBlank(funcNum)){
            for (Map.Entry<String, String> entry : confPathMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println("key = " + key + ", value = " + value);
                if((funcNum+"_CLASS_HOME_PATH").equals(key)){
                    classHomePath.setText(StringUtils.isNotBlank(value)?value:"");
                }
                else if((funcNum+"_PROJECT_HOME_PATH").equals(key)){
                    projectHomePath.setText(StringUtils.isNotBlank(value)?value:"");
                } else if((funcNum+"_OUTPUT_HOME_PATH").equals(key)){
                    outputHomePath.setText(StringUtils.isNotBlank(value)?value:"");
                }
            }
        }
    }

    /**
     * 清除全局配置路径
     */
    private void onClearGlobalConf(){
        confAllPath.setText("");
        GlobalConfSetting globalConfSetting = ServiceManager.getService(GlobalConfSetting.class);
        globalConfSetting.confPathMap = null;
    }

    public static void main(String[] args) {
        BasePackDialog dialog = new BasePackDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
