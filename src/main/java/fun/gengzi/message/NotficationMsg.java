package fun.gengzi.message;

import cn.hutool.core.util.StrUtil;
import com.intellij.notification.*;
import fun.gengzi.utils.I18nBundle;

/**
 * <h1>推送通知消息</h1>
 *
 * @author gengzi
 * @date 2022年2月1日11:59:58
 */
public class NotficationMsg {

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("imagePanel", NotificationDisplayType.BALLOON, true);


    /**
     * 路径是否为空
     * @param imgPath 图片路径
     * @return
     */
    public static boolean isBlank(String imgPath) {
        if (StrUtil.isBlank(imgPath)) {
            NotficationMsg.notifySelectImgMsg("");
            return true;
        }
        return false;
    }

    /**
     * 图片保存提示
     * @param imgPath 保存后的图片路径
     */
    public static void notifySaveImgMsg(String imgPath) {
        // 图片保存标题
        String NOTFICATIONMSG_SAVE_TITLE = I18nBundle.message(I18nBundle.Key.NOTFICATIONMSG_SAVE_TITLE);
        String NOTFICATIONMSG_SAVE_CONTENT = I18nBundle.message(I18nBundle.Key.NOTFICATIONMSG_SAVE_CONTENT);
        // 推送消息，告知保存位置以及名称
        Notification notification = NOTIFICATION_GROUP.createNotification(NOTFICATIONMSG_SAVE_TITLE, String.format(NOTFICATIONMSG_SAVE_CONTENT, imgPath),
                NotificationType.INFORMATION, null);
        //        notification.addAction(openPictureAction);
        Notifications.Bus.notify(notification);
    }

    /**
     * 图片选择提示
     * @param imgPath
     */
    public static void notifySelectImgMsg(String imgPath) {
        String NOTFICATIONMSG_CHOOSE_TITLE = I18nBundle.message(I18nBundle.Key.NOTFICATIONMSG_CHOOSE_TITLE);
        String NOTFICATIONMSG_CHOOSE_CONTENT = I18nBundle.message(I18nBundle.Key.NOTFICATIONMSG_CHOOSE_CONTENT);
        // 推送消息，告知保存位置以及名称
        Notification notification = NOTIFICATION_GROUP.createNotification(NOTFICATIONMSG_CHOOSE_TITLE, NOTFICATIONMSG_CHOOSE_CONTENT,
                NotificationType.INFORMATION, null);
        //        notification.addAction(openPictureAction);
        Notifications.Bus.notify(notification);
    }

}
