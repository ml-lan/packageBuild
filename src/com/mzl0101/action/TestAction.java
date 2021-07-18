package com.mzl0101.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.mzl0101.dialog.BasePackDialog;

/**
 * @创建人 mzl
 * @创建时间 2021/7/17 15:38
 * @描述
 */
public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here

        /**
         * content :  通知内容
         * type  ：通知的类型，warning,info,error
         */
        //NotificationGroup notificationGroup = new NotificationGroup("testid", NotificationDisplayType.BALLOON, false);
        //Notification notification = notificationGroup.createNotification("测试通知111", MessageType.INFO);
        //Notifications.Bus.notify(notification);
        BasePackDialog dialog = new BasePackDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);

    }
}
