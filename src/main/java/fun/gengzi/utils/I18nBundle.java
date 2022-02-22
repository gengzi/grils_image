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
        PLUGIN_NAME("plugin_name"),
        NOTFICATIONMSG_SAVE_TITLE("notficationMsg_save_title"),
        NOTFICATIONMSG_SAVE_CONTENT("notficationMsg_save_content"),
        NOTFICATIONMSG_CHOOSE_TITLE("notficationMsg_choose_title"),
        NOTFICATIONMSG_CHOOSE_CONTENT("notficationMsg_choose_content"),
        NOTFICATIONMSG_ERROR_TITLE("notficationMsg_error_title"),
        NOTFICATIONMSG_ERROR_CONTENT("notficationMsg_error_content"),
        TOOLTIPTEXT_IMAGEVIEW_CONTENT("tooltiptext_imageview_content"),
        PIXELIMAGEPANEL_IMGPATH_TOOLTIPTEXT("pixelimagepanel_imgPath_tooltiptext"),
        PIXELIMAGEPANEL_THRESHOLD("pixelimagepanel_threshold"),
        IMGTABBEDPANE_TAB_ASCIIMAGEPANEL("imgtabbedpane_tab_asciimagepanel"),
        IMGTABBEDPANE_TAB_BLACKANDWHITEIMAGEPANEL("imgtabbedpane_tab_blackandwhiteimagepanel"),
        IMGTABBEDPANE_TAB_GRILSIMAGEPANEL("imgtabbedpane_tab_grilsimagepanel"),
        IMGTABBEDPANE_TAB_PIXELIMAGEPANEL("imgtabbedpane_tab_pixelimagepanel"),
        IMGTABBEDPANE_TAB_IMAGESHOWPANEL("imgtabbedpane_tab_imageshowpanel"),
        IMGTABBEDPANE_TAB_BASE64IMAGEPANEL("imgtabbedpane_tab_base64imagepanel"),
        IMGTABBEDPANE_TAB_QRCODEIMAGEPANEL("imgtabbedpane_tab_qrcodeimagepanel"),
        BASE64IMAGEPANEL_TOBASE64BUTTON_TEXT("base64imagepanel_tobase64button_text"),
        BASE64IMAGEPANEL_SAVEASBUTTON_TEXT("base64imagepanel_saveAsButton_text"),
        BASE64IMAGEPANEL_SAVEAS_ERROR_TEXT("base64imagepanel_saveAs_error_text"),
        BASE64IMAGEPANEL_COPYLABEL_TEXT("base64imagepanel_copyLabel_text"),


        ASCIIMAGEPANEL_FONTSTYLELABEL_TEXT("asciimagepanel_fontStyleLabel_text"),
        ASCIIMAGEPANEL_SIZELABEL_TEXT("asciimagepanel_sizeLabel_text"),
        ASCIIMAGEPANEL_CHARLABEL_TEXT("asciimagepanel_charLabel_text"),
        ASCIIMAGEPANEL_FONTSTYLEVALUES_PLAIN("asciimagepanel_fontstylevalues_plain"),
        ASCIIMAGEPANEL_FONTSTYLEVALUES_BOLD("asciimagepanel_fontstylevalues_bold"),
        ASCIIMAGEPANEL_FONTSTYLEVALUES_ITALIC("asciimagepanel_fontstylevalues_italic"),

        HINTS_IMAGESHOWPANEL_TEXT_1("hints_ImageShowPanel_text_1"),
        HINTS_IMAGESHOWPANEL_TEXT_2("hints_ImageShowPanel_text_2"),
        HINTS_BASE64IMAGEPANEL_TEXT_1("hints_Base64ImagePanel_text_1"),


        QRCODEIMAGEPANEL_TOQRCODEBUTTON_TEXT("qrcodeimagepanel_toQrcodebutton_text"),
        QRCODEIMAGEPANEL_BASICLABEL_TEXT("qrcodeimagepanel_basicLabel_text"),
        QRCODEIMAGEPANEL_COLOUR_TEXT("qrcodeimagepanel_colour_text"),

        HINTS_QRCODEIMAGEPANEL_TEXT_1("hints_QRCodeImagePanel_text_1"),
        HINTS_QRCODEIMAGEPANEL_TEXT_2("hints_QRCodeImagePanel_text_2"),
        QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_TEXT("qrcodeimagepanel_errorcorrectionoptions_text"),
        QRCODEIMAGEPANEL_SIZE_TEXT("qrcodeimagepanel_size_text"),
        QRCODEIMAGEPANEL_SAVEASBUTTON_TEXT("qrcodeimagepanel_saveAsButton_text"),
        QRCODEIMAGEPANEL_SAVEAS_ERROR_TEXT("qrcodeimagepanel_saveAs_error_text"),

        QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_HIGH("qrcodeimagepanel_errorcorrectionoptions_high"),
        QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_MEDIUM("qrcodeimagepanel_errorcorrectionoptions_medium"),
        QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_LOW("qrcodeimagepanel_errorcorrectionoptions_Low"),
        QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_LOWEST("qrcodeimagepanel_errorcorrectionoptions_lowest"),

        ASCIIMAGEPANEL_FONTLABEL_TEXT("asciimagepanel_fontLabel_text");


        final String value;

        Key(String value) {
            this.value = value;
        }
    }
}
