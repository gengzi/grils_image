package fun.gengzi.filetype;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;

import java.util.Arrays;
import java.util.List;

/**
 * <H1>图片选择描述</H1>
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
        return new PictureChooserDescriptor(true, false, false, false, false, false);
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
        super.withFileFilter(file ->
                file.getExtension() != null && PICTURE_EXTENSION_LIST.contains(file.getExtension().toLowerCase())
        );
    }


}
