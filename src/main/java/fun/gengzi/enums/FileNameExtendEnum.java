package fun.gengzi.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileNameExtendEnum {

    // 黑白照片
    BLACKANDWHITE_EXTEND("%s_blackandwhite.%s"),
    // ASCII 照片
    ASCIIMG_EXTEND("%s_asciimg.%s"),
    // ASCII 照片
    PIXELIMG_EXTEND("%s_pixelimg.%s");

    /**
     * 文件名称
     */
    private String fileName;
}
