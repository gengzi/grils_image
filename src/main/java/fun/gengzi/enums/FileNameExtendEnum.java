package fun.gengzi.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>图片命名枚举类</h1>
 *
 * @author gengzi
 * @date 2022年2月1日11:54:49
 */
@Getter
@AllArgsConstructor
public enum FileNameExtendEnum {
    // 灰色照片
    BLACKANDWHITE_EXTEND("%s_blackandwhite.%s"),
    // ASCII 照片
    ASCIIMG_EXTEND("%s_asciimg.%s"),
    // base64 图片
    BASE64_EXTEND("%s_base64.%s"),
    // 像素照片
    PIXELIMG_EXTEND("%s_pixelimg.%s");

    /**
     * 文件名称
     */
    private String fileName;
}
