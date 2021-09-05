package com.mzl0101.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
        BasePackDialog dialog = new BasePackDialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
