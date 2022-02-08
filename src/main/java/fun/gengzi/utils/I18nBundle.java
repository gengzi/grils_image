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
        NOTFICATIONMSG_SAVE_TITLE("notficationMsg.save.title"),
        NOTFICATIONMSG_SAVE_CONTENT("notficationMsg.save.content"),
        NOTFICATIONMSG_CHOOSE_TITLE("notficationMsg.choose.title"),
        NOTFICATIONMSG_CHOOSE_CONTENT("notficationMsg.choose.content"),
        NOTFICATIONMSG_ERROR_TITLE("notficationMsg.error.title"),
        NOTFICATIONMSG_ERROR_CONTENT("notficationMsg.error.content"),
        TOOLTIPTEXT_IMAGEVIEW_CONTENT("tooltiptext.imageview.content"),
        PIXELIMAGEPANEL_IMGPATH_TOOLTIPTEXT("pixelimagepanel.imgPath.tooltiptext"),
        PIXELIMAGEPANEL_THRESHOLD("pixelimagepanel.threshold"),
        IMGTABBEDPANE_TAB_ASCIIMAGEPANEL("imgtabbedpane.tab.asciimagepanel"),
        IMGTABBEDPANE_TAB_BLACKANDWHITEIMAGEPANEL("imgtabbedpane.tab.blackandwhiteimagepanel"),
        IMGTABBEDPANE_TAB_GRILSIMAGEPANEL("imgtabbedpane.tab.grilsimagepanel"),
        IMGTABBEDPANE_TAB_PIXELIMAGEPANEL("imgtabbedpane.tab.pixelimagepanel"),
        IMGTABBEDPANE_TAB_IMAGESHOWPANEL("imgtabbedpane.tab.imageshowpanel"),
        IMGTABBEDPANE_TAB_BASE64IMAGEPANEL("imgtabbedpane.tab.base64imagepanel"),
        BASE64IMAGEPANEL_TOBASE64BUTTON_TEXT("base64imagepanel.tobase64button.text"),
        BASE64IMAGEPANEL_SAVEASBUTTON_TEXT("base64imagepanel.saveAsButton.text"),
        BASE64IMAGEPANEL_SAVEAS_ERROR_TEXT("base64imagepanel.saveAs.error.text"),
        BASE64IMAGEPANEL_COPYLABEL_TEXT("base64imagepanel.copyLabel.text"),


        ASCIIMAGEPANEL_FONTSTYLELABEL_TEXT("asciimagepanel.fontStyleLabel.text"),
        ASCIIMAGEPANEL_SIZELABEL_TEXT("asciimagepanel.sizeLabel.text"),
        ASCIIMAGEPANEL_CHARLABEL_TEXT("asciimagepanel.charLabel.text"),
        ASCIIMAGEPANEL_FONTSTYLEVALUES_PLAIN("asciimagepanel.fontstylevalues.plain"),
        ASCIIMAGEPANEL_FONTSTYLEVALUES_BOLD("asciimagepanel.fontstylevalues.bold"),
        ASCIIMAGEPANEL_FONTSTYLEVALUES_ITALIC("asciimagepanel.fontstylevalues.italic"),
        ASCIIMAGEPANEL_FONTLABEL_TEXT("asciimagepanel.fontLabel.text");


        final String value;

        Key(String value) {
            this.value = value;
        }
    }
}
