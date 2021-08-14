package com.mzl0101.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.mzl0101.dialog.BasePackDialog;


/**
 * @创建人 mzl
 * @创建时间 2021/7/17 17:57
 * @描述
 */
public class BasePackAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = e.getProject();
        //项目路径
        String projectPath = project.getBasePath();
        //项目名称
        String projectName = project.getName();
        BasePackDialog dialog = new BasePackDialog(projectPath, projectName);
        dialog.pack();
        dialog.setVisible(true);
    }
}
