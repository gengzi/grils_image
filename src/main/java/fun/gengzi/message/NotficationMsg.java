package fun.gengzi.message;

import com.intellij.notification.*;

/**
 * 用于推送消息
 */
public class NotficationMsg {

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("imagePanel", NotificationDisplayType.BALLOON, true);


    public static void notifySaveImgMsg(String imgPath) {
        // 推送消息，告知保存位置以及名称
        Notification notification = NOTIFICATION_GROUP.createNotification("图片保存提示", "转换的图片保存到" + imgPath + "下面了",
                NotificationType.INFORMATION, null);
        //        notification.addAction(openPictureAction);
        Notifications.Bus.notify(notification);
    }

    public static void notifySelectImgMsg(String imgPath) {
        // 推送消息，告知保存位置以及名称
        Notification notification = NOTIFICATION_GROUP.createNotification("图片选择提示", "请选择图片到路径中，切换面板即可预览",
                NotificationType.INFORMATION, null);
        //        notification.addAction(openPictureAction);
        Notifications.Bus.notify(notification);
    }

}
