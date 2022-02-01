package fun.gengzi.utils;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NotNull;

/**
 * 国际化服务
 *
 * <p>依赖于 {@link AbstractBundle} 组件实现</p>
 * 借鉴于：ycy-intellij-plugin
 */
public class I18nBundle extends AbstractBundle {
    private static final I18nBundle INSTANCE = new I18nBundle();

    private I18nBundle() {
        super("i18n.templates");
    }

    /**
     * 获取国际化值
     *
     * @param key    {@code template.properties} 中的键
     * @param params {@code template.properties} 中的值占位符
     * @return 国际化值
     * @see Key
     */
    public static String message(@NotNull Key key, @NotNull Object... params) {
        return INSTANCE.getMessage(key.value, params);
    }

    public enum Key {
        // 插件名称
        PLUGIN_NAME("plugin.name"),

        // 通知 - 保存图片文件
        NOTFICATIONMSG_SAVE_TITLE("notficationMsg.save.title"),
        NOTFICATIONMSG_SAVE_CONTENT("notficationMsg.save.content"),
        NOTFICATIONMSG_CHOOSE_TITLE("notficationMsg.choose.title"),
        NOTFICATIONMSG_CHOOSE_CONTENT("notficationMsg.choose.content"),
        TOOLTIPTEXT_IMAGEVIEW_CONTENT("tooltiptext.imageview.content"),
        PIXELIMAGEPANEL_IMGPATH_TOOLTIPTEXT("pixelimagepanel.imgPath.tooltiptext"),
        PIXELIMAGEPANEL_THRESHOLD("pixelimagepanel.threshold"),
        IMGTABBEDPANE_TAB_ASCIIMAGEPANEL("imgtabbedpane.tab.asciimagepanel "),
        IMGTABBEDPANE_TAB_BLACKANDWHITEIMAGEPANEL("imgtabbedpane.tab.blackandwhiteimagepanel "),
        IMGTABBEDPANE_TAB_GRILSIMAGEPANEL("imgtabbedpane.tab.grilsimagepanel "),
        IMGTABBEDPANE_TAB_PIXELIMAGEPANEL("imgtabbedpane.tab.pixelimagepanel ");


        final String value;

        Key(String value) {
            this.value = value;
        }
    }
}
