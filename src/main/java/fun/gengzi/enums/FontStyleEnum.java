package fun.gengzi.enums;

import com.intellij.util.ui.CollectionModelEditor;
import fun.gengzi.utils.I18nBundle;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum FontStyleEnum {

    PLAIN(I18nBundle.message(fun.gengzi.utils.I18nBundle.Key.ASCIIMAGEPANEL_FONTSTYLEVALUES_PLAIN), Font.PLAIN),
    BOLD(I18nBundle.message(fun.gengzi.utils.I18nBundle.Key.ASCIIMAGEPANEL_FONTSTYLEVALUES_BOLD), Font.BOLD),
    ITALIC(I18nBundle.message(fun.gengzi.utils.I18nBundle.Key.ASCIIMAGEPANEL_FONTSTYLEVALUES_ITALIC), Font.ITALIC);

    /**
     * 对外映射字符
     */
    private String name;

    /**
     * 字体样式
     */
    private Integer fontStyle;

    private static final Map<String, Integer> NAMETOSTYLE = Arrays.stream(values()).
            collect(Collectors.toMap(FontStyleEnum::getName, FontStyleEnum::getFontStyle, (oldValue, newValue) -> newValue));

    /**
     * 根据名称查询字体样式
     * @param name 对外映射名称
     * @return 字体样式
     */
    public static int getFontStyleByName(String name) {
        return NAMETOSTYLE.get(name);
    }


}
