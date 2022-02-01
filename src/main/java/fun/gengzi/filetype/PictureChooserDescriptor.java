package fun.gengzi.filetype;

import cn.hutool.core.lang.Singleton;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;

import java.util.Arrays;
import java.util.List;

/**
 * <H1>文件类型选择过滤描述</H1>
 * <p>
 * 支持选择 "jpg", "jpeg", "png", "bmp", "gif" 这些类型的文件
 *
 * @author gengzi
 * @date 2022年1月30日22:39:42
 */
public class PictureChooserDescriptor extends FileChooserDescriptor {
    /**
     * 支持的图片格式
     */
    private static final List<String> PICTURE_EXTENSION_LIST = Arrays.asList("jpg", "jpeg", "png", "bmp", "gif");


    /**
     * 获取实例对象
     *
     * @return PictureChooserDescriptor
     */
    public static PictureChooserDescriptor getInstance() {
        return Singleton.get(PictureChooserDescriptor.class, true, false, false, false, false, false);
    }


    /**
     * 私有构造，增加文件过滤器
     *
     * @param chooseFiles
     * @param chooseFolders
     * @param chooseJars
     * @param chooseJarsAsFiles
     * @param chooseJarContents
     * @param chooseMultiple
     */
    private PictureChooserDescriptor(boolean chooseFiles, boolean chooseFolders, boolean chooseJars, boolean chooseJarsAsFiles, boolean chooseJarContents, boolean chooseMultiple) {
        super(chooseFiles, chooseFolders, chooseJars, chooseJarsAsFiles, chooseJarContents, chooseMultiple);
        // 增加过滤
        super.withFileFilter(file ->
                file.getExtension() != null && PICTURE_EXTENSION_LIST.contains(file.getExtension().toLowerCase())
        );
    }


}
