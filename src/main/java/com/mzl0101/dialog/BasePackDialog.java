package com.mzl0101.dialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mzl0101.base.BasePack;
import com.mzl0101.model.PackageBuildModel;
import com.mzl0101.model.PackageConfigModel;
import com.mzl0101.util.GlobalConfigUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    private JTabbedPane packageBuildTabbedPane;
    /**
     * 配置表格模型对象
     */
    private DefaultTableModel configTableModel;
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
    文件列表文本框
     */
    private JTextArea customizeFilesTexts;
    /**
     * git获取按钮
     */
    private JButton gitGetButton;

    private JScrollPane JscrollPane;
    /**
     * 配置table
     */
    private JTable configTable;
    /**
     * 配置项目
     */
    private JTextField configProject;
    /**
     * 配置class编译目录
     */
    private JTextField configClassHomePath;
    /**
     * 配置输出目录
     */
    private JTextField configOutputHomePath;
    /**
     * 配置增加按钮
     */
    private JButton configAddButtton;
    /**
     * 配置修改按钮
     */
    private JButton configUpdateButton;
    /**
     * 配置删除按钮
     */
    private JButton configDeleteButton;
    /**
     * 配置项目路径
     */
    private JTextField configProjectPath;
    /**
     * 项目下拉框选项
     */
    private JComboBox projectComboBox;
    /**
     * 项目路径
     */
    private JTextField projectPath;
    /**
     * 配置清除按钮
     */
    private JButton configClearButton;


    public BasePackDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buildOkButton);
        setLocation(400,200);
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

        initProjectComboBox();
        initConfigTable();
        initPackageBuildAction();
    }

    /**
     * 初始化事件
     */
    private void initPackageBuildAction(){
        /**
         * git获取按钮事件
         */
        gitGetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onGitGet();
            }
        });
        /**
         * 解析JSON字符串按钮事件
         */
        parseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onParse();
            }
        });
        /**
         * 确定打包按钮事件
         */
        buildOkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        /**
         * 取消打包按钮事件
         */
        buildCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
    }
    /**
     * 初始化项目下拉框
     */
    private void initProjectComboBox(){
        String [] projectComboBoxData = GlobalConfigUtil.convertOneGlobalConfig();
        ComboBoxModel comboBoxModel = new DefaultComboBoxModel(projectComboBoxData);
        projectComboBox.setModel(comboBoxModel);
        //项目下拉框事件
        projectComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if(itemEvent.getStateChange()==ItemEvent.SELECTED){
                    String project = (String) itemEvent.getItem();
                    PackageConfigModel packageConfigModel = GlobalConfigUtil.getGlobalConfig(project);
                    projectPath.setText(packageConfigModel.getConfigProjectPath());
                    classHomePath.setText(packageConfigModel.getConfigClassHomePath());
                    outputHomePath.setText(packageConfigModel.getConfigOutputHomePath());
                    filesJsonText.setText("");
                    filesJsonText.setText("");
                }
            }
        });
    }

    /**
     * 初始化项目下拉框
     */
    private void flushProjectComboBox(){
        String [] projectComboBoxData = GlobalConfigUtil.convertOneGlobalConfig();
        ComboBoxModel comboBoxModel = new DefaultComboBoxModel(projectComboBoxData);
        projectComboBox.setModel(comboBoxModel);
    }
    /**
     * 初始化配置表格
     */
    private void initConfigTable(){
        //列名
        String[] columnNames = {"项目","项目路径","项目输出路径","打包目录"};
        //默认数据
        String [][] tableValues= GlobalConfigUtil.convertTwoGlobalConfig();
        configTableModel = new DefaultTableModel(tableValues,columnNames);
        configTable.setModel(configTableModel);
        //单选
        configTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //鼠标事件
        configTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int selectedRow = configTable.getSelectedRow(); //获得选中行索引
                //"项目","项目路径","项目输出路径","打包目录"
                Object projectObj = configTableModel.getValueAt(selectedRow, 0);
                Object projectPathObj = configTableModel.getValueAt(selectedRow, 1);
                Object classHomePathObj = configTableModel.getValueAt(selectedRow, 2);
                Object outputHomePathObj = configTableModel.getValueAt(selectedRow, 3);
                //赋值
                configProject.setText(projectObj.toString());
                configProjectPath.setText(projectPathObj.toString());
                configClassHomePath.setText(classHomePathObj.toString());
                configOutputHomePath.setText(outputHomePathObj.toString());
            }
        });

        /**
         * 配置增加按钮事件
         */
        configAddButtton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(StringUtils.isNotBlank(configProject.getText())&&
                        StringUtils.isNotBlank(configProjectPath.getText())&&
                        StringUtils.isNotBlank(configClassHomePath.getText())&&
                        StringUtils.isNotBlank(configOutputHomePath.getText())){
                    if(GlobalConfigUtil.checkProjectExist(configProject.getText())){
                        JOptionPane.showMessageDialog(null, "该项目已存在！","提示", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String []rowValues = { configProject.getText(), configProjectPath.getText(), configClassHomePath.getText(), configOutputHomePath.getText() };
                    //添加一行
                    configTableModel.addRow(rowValues);
                    //添加至配置中
                    PackageConfigModel packageConfigModel = new PackageConfigModel();
                    packageConfigModel.setConfigProject(configProject.getText());
                    packageConfigModel.setConfigProjectPath(configProjectPath.getText());
                    packageConfigModel.setConfigClassHomePath(configClassHomePath.getText());
                    packageConfigModel.setConfigOutputHomePath(configOutputHomePath.getText());
                    GlobalConfigUtil.addOrUpdateGlobalConfig(packageConfigModel);
                    flushProjectComboBox();
                }
            }
        });
        /**
         * 配置删除按钮事件
         */
        configDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获得选中行的索引
                int selectedRow = configTable.getSelectedRow();
                //存在选中行
                if(selectedRow!=-1)
                {
                    Object projectObj = configTableModel.getValueAt(selectedRow, 0);
                    //删除行
                    configTableModel.removeRow(selectedRow);
                    //移除配置
                    GlobalConfigUtil.removeGlobalConfig(projectObj.toString());
                    flushProjectComboBox();
                }
            }
        });
        /**
         * 配置修改按钮事件
         */
        configUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获得选中行的索引
                int selectedRow = configTable.getSelectedRow();
                if(selectedRow!= -1)   //是否存在选中行
                {
                    //修改指定的值
                    configTableModel.setValueAt(configProject.getText(), selectedRow, 0);
                    configTableModel.setValueAt(configProjectPath.getText(), selectedRow, 1);
                    configTableModel.setValueAt(configClassHomePath.getText(), selectedRow, 2);
                    configTableModel.setValueAt(configOutputHomePath.getText(), selectedRow, 3);
                    //添加至配置中
                    PackageConfigModel packageConfigModel = new PackageConfigModel();
                    packageConfigModel.setConfigProject(configProject.getText());
                    packageConfigModel.setConfigProjectPath(configProjectPath.getText());
                    packageConfigModel.setConfigClassHomePath(configClassHomePath.getText());
                    packageConfigModel.setConfigOutputHomePath(configOutputHomePath.getText());
                    GlobalConfigUtil.addOrUpdateGlobalConfig(packageConfigModel);
                }
            }
        });
        /**
         * 配置清除按钮事件
         */
        configClearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configProject.setText("");
                configProjectPath.setText("");
                configClassHomePath.setText("");
                configOutputHomePath.setText("");
            }
        });
    }

    /**
     * git获取方法
     */
    private void onGitGet(){
        String tempProjectPath = projectPath.getText();
        if(StringUtils.isNotBlank(tempProjectPath)){
            try {
                List<String> gitDiffFileList = new ArrayList<>();
                String osName = System.getProperty("os.name").toLowerCase();
                java.lang.Process process;
                if (osName.contains("win")) {
                    String drives = tempProjectPath.substring(0, 2);
                    //git diff 获取文件列表
                    process = Runtime.getRuntime().exec("cmd /c " + drives + " && cd " + tempProjectPath + " && git diff --name-only HEAD~ HEAD");
                } else {
                    process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "cd " + tempProjectPath + " && git diff --name-only HEAD~ HEAD"});
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
                if(StringUtils.isNotBlank(originFilesStr)){
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
     * 解析方法
     */
    private void onParse(){
        String filesJson = filesJsonText.getText();
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String,Object> bitBucketRespMap = gson.fromJson(filesJson, type);
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
        if(StringUtils.isNotBlank(originFilesStr)){
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

        String packageNameText = packageName.getText().trim();
        if(StringUtils.isBlank(packageNameText)){
            JOptionPane.showMessageDialog(null, "任务号不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String projectHomePathText = projectPath.getText().trim();
        if(StringUtils.isBlank(projectHomePathText)){
            JOptionPane.showMessageDialog(null, "项目路径不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String classHomePathText = classHomePath.getText().trim();
        if(StringUtils.isBlank(classHomePathText)){
            JOptionPane.showMessageDialog(null, "项目输出路径不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String outputHomePathText = outputHomePath.getText().trim();
        if(StringUtils.isBlank(outputHomePathText)){
            JOptionPane.showMessageDialog(null, "输出目录不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String filesStr = customizeFilesTexts.getText().trim();
        if(StringUtils.isBlank(filesStr)){
            JOptionPane.showMessageDialog(null, "文件列表不能为空","提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //回车换行去除
        filesStr = filesStr.replaceAll("[\\t\\n\\r]", "");
        List<String> filePathsList = Arrays.asList(filesStr.split(","));

        classHomePathText = classHomePathText.replace("\\","/");
        PackageBuildModel packageBuildModel = new PackageBuildModel();
        packageBuildModel.setPackageName(packageNameText);
        packageBuildModel.setProjectHomePath(projectHomePathText);
        packageBuildModel.setClassHomePath(classHomePathText);
        packageBuildModel.setOutputHomePath(outputHomePathText);
        packageBuildModel.setFilePathsList(filePathsList);

        Map<String,Object> result = BasePack.pack(packageBuildModel);
        boolean status = (boolean) result.get("status");
        String msg = (String) result.get("msg");
        if(status){
            JOptionPane.showMessageDialog(null,msg,"提示",JOptionPane.INFORMATION_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, msg,"提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 取消方法
     */
    private void onCancel() {
        dispose();
    }
}
