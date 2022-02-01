package fun.gengzi.swing;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ArrayUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.io.PathUtil;
import com.intellij.ui.FontComboBox;
import fun.gengzi.asciimg.image.AsciiImgCache;
import fun.gengzi.asciimg.image.character_fit_strategy.ColorSquareErrorFitStrategy;
import fun.gengzi.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;
import fun.gengzi.asciimg.image.converter.AsciiToImageConverter;
import fun.gengzi.asciimg.image.converter.AsciiToStringConverter;
import fun.gengzi.enums.FileNameExtendEnum;
import fun.gengzi.enums.FontStyleEnum;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.utils.I18nBundle;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * <h1>Asci图片</h1>
 *
 * @author gengzi
 * @date 2022年1月30日22:59:20
 */
@Getter
public class AsciImagePanel extends JXPanel implements ImageFilePathProcess {
    private JXImageView jxImageView;
    private JXPanel jxPanel;
    private JXPanel lastPanel;
    private JXLabel fontLabel;
    private JXLabel fontStyleLabel;
    private JXLabel sizeLabel;
    private JXLabel charLabel;
    private ComboBox<String> fontStyleComboBox;
    private ComboBox<Integer> sizeComboBox;
    private FontComboBox fontComboBox;
    private JXButton okButton;
    private JTextField charsTextField;
    private static final Logger LOG = Logger.getInstance(AsciImagePanel.class);
    private String imgPath;

    // 字体类型取值
    private static final String[] FONTSTYLEVALUES = {
            I18nBundle.message(I18nBundle.Key.ASCIIMAGEPANEL_FONTSTYLEVALUES_PLAIN),
            I18nBundle.message(I18nBundle.Key.ASCIIMAGEPANEL_FONTSTYLEVALUES_BOLD),
            I18nBundle.message(I18nBundle.Key.ASCIIMAGEPANEL_FONTSTYLEVALUES_ITALIC)};
    // 字体大小取值
    private static final int[] SIZEVALUES = ArrayUtil.range(5, 70, 1);

    private Integer[] arr = {};

    /**
     * 初始化面板
     */
    @SneakyThrows
    public AsciImagePanel() {
        this.jxPanel = this;
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        // 初始化面板组件
        initCompant();
        // 监听器
        addAllActionListener();
    }

    private void initCompant() throws IOException {
        okButton = new JXButton();
        okButton.setText("ok");
        lastPanel = new JXPanel();
        fontLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.ASCIIMAGEPANEL_FONTLABEL_TEXT));
        fontStyleLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.ASCIIMAGEPANEL_FONTSTYLELABEL_TEXT));
        sizeLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.ASCIIMAGEPANEL_SIZELABEL_TEXT));
        charLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.ASCIIMAGEPANEL_CHARLABEL_TEXT));
        charsTextField = new JTextField();
        charsTextField.setMaximumSize(new Dimension(200,100));
        fontStyleComboBox = new ComboBox(FONTSTYLEVALUES);
        // 默认选中第一个
        fontStyleComboBox.setSelectedIndex(0);
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < SIZEVALUES.length; i++) {
            integers.add(Integer.valueOf(SIZEVALUES[i]));
        }
        Integer[] sizeValues = integers.toArray(arr);
        sizeComboBox = new ComboBox(sizeValues);
        sizeComboBox.setSelectedIndex(5);
        fontComboBox = new FontComboBox();
        lastPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        lastPanel.add(fontLabel);
        lastPanel.add(fontComboBox);
        lastPanel.add(fontStyleLabel);
        lastPanel.add(fontStyleComboBox);
        lastPanel.add(sizeLabel);
        lastPanel.add(sizeComboBox);
        lastPanel.add(charLabel);
        lastPanel.add(charsTextField);
        lastPanel.add(okButton);
        lastPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        jxImageView = new JXImageView();
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.add(jxImageView, BorderLayout.CENTER);
        this.add(lastPanel, BorderLayout.PAGE_END);
    }

    private void addAllActionListener() {
        okButton.addActionListener(e -> {
            this.process(imgPath);
        });
    }


    /**
     * 根据图片路径处理图片
     *
     * @param imgPath 图片路径
     */
    @SneakyThrows
    @Override
    public void process(String imgPath) {
        boolean blank = NotficationMsg.isBlank(imgPath);
        if (blank) {
            return;
        }
        this.imgPath = imgPath;
        File file = FileUtil.file(imgPath);
        String parent = PathUtil.getParent(imgPath);
        // 解析文件名称
        String name = FileNameUtil.mainName(file);
        String extName = FileNameUtil.extName(file);
        String newName = String.format(FileNameExtendEnum.ASCIIMG_EXTEND.getFileName(), name, extName);
        String newImagePath = FileUtil.getAbsolutePath(parent + File.separator + newName);
        LOG.info("图片绝对路径：" + newImagePath);
        imgToAsci(imgPath, newImagePath);
        jxImageView.setImage(new File(newImagePath));
        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth(), this.jxPanel.getHeight()));
        jxImageView.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        NotficationMsg.notifySaveImgMsg(newImagePath);
    }


    @SneakyThrows
    private void imgToAsci(String imgPath, String newImgPath) {
        String fontStyleItem = (String) fontStyleComboBox.getSelectedItem();
        Integer sizeItem = (Integer) sizeComboBox.getSelectedItem();
        int fontStyle = FontStyleEnum.getFontStyleByName(fontStyleItem);
        String charsStr = charsTextField.getText();
        char[] chars = charsStr.toCharArray();
        System.out.println(chars);
        String fontName = fontComboBox.getFontName();
        System.out.println(fontName);
        Font font = new Font(fontName, fontStyle, sizeItem);
        // initialize cache
//        AsciiImgCache cache = AsciiImgCache.create(font, new char[]{'\\', ' ', '/'});
        AsciiImgCache cache = AsciiImgCache.create(font, chars);
        // load image
        BufferedImage portraitImage = ImageIO.read(new File(imgPath));
        // initialize converters
        AsciiToImageConverter imageConverter =
                new AsciiToImageConverter(cache, new ColorSquareErrorFitStrategy());
        AsciiToStringConverter stringConverter =
                new AsciiToStringConverter(cache, new StructuralSimilarityFitStrategy());
        // image output
        ImageIO.write(imageConverter.convertImage(portraitImage), "png",
                new File(newImgPath));
        // string converter, output to console
        System.out.println(stringConverter.convertImage(portraitImage));
    }

}
