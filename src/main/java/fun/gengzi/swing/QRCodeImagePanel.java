package fun.gengzi.swing;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.containers.ContainerUtil;
import fun.gengzi.constant.GlobalConstant;
import fun.gengzi.constant.ImagePanelTipsConstant;
import fun.gengzi.enums.FileNameExtendEnum;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.imgeservice.ImagePanelHint;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.utils.I18nBundle;
import fun.gengzi.utils.UiRefreshThreadUtils;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXColorSelectionButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <h1>提供二维码图片转换</h1>
 *
 * @author gengzi
 * @date 2022年2月14日16:27:43
 */
@Getter
public class QRCodeImagePanel extends JXPanel implements ImageFilePathProcess, ImagePanelHint {
    private static final Logger LOG = Logger.getInstance(QRCodeImagePanel.class);
    // 默认的图片UI
    private DefaultImageEditorUI jxImageView;
    private JXPanel jxPanel;
    private JBTextArea qrCodeEditor;
    private JBScrollPane jbScrollPane;
    private JXPanel lastPanel;
    private JXPanel topPanelOne;
    private JXPanel topPanelTwo;
    private JXPanel topPanelThree;
    private JXPanel topPanel;
    private JXPanel editorPanel;
    private JXLabel copyLabel;
    private JXLabel errorCorrectionOptionsLabel;
    private JXLabel sizeLabel;
    private JXButton toQRcodeButton;
    private JXButton saveAsButton;
    private String imgPath;
    private Runnable runnable;
    private ComboBox<String> errorCorrectionOptionsComboBox;
    private ComboBox<Integer> sizeComboBox;
    private JXLabel colorLabel;
    private static final int BLACK = new Color(0, 0, 0).getRGB();
    private static final int WHITE = new Color(232, 229, 229).getRGB();
    // 支持的图片格式
    private static final List<String> IMAGE_TYPE = ContainerUtil.immutableList("png", "ico", "bmp", "gif", "jpg", "svg");
    private String[] arr = {};

    // 默认的字符类型
    private Font defaultfont;

    // 基本-纠错 选项数据
    private static final List<String> errorCorrectionOptions = ContainerUtil.immutableList(
            I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_HIGH),
            I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_MEDIUM),
            I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_LOW),
            I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_LOWEST)
    );


    private static final int[] SIZEVALUES = ArrayUtil.range(1, 11, 1);


    private static final List<Integer> SIZE_TYPE = new ArrayList<>();

    /**
     * 初始化面板
     */
    @SneakyThrows
    public QRCodeImagePanel() {
        String fontName = this.getFont().getFontName();
        defaultfont = new Font(fontName, Font.PLAIN, 16);
        this.jxPanel = this;
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        // 初始化面板组件
        initCompant();
        // 监听器
        addAllActionListener();
        // 异步刷新UI任务
        this.runnable = () -> {
            showImage();
        };
    }


    private void initCompant() {
        // 头部
        topPanel = new JXPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        configPanelone();
        configPaneltwo();
        topPanel.add(topPanelOne);
        topPanel.add(topPanelTwo);


        // 中部
        GridLayoutManager gridLayoutManager = new GridLayoutManager(2, 1);
        // 设置相同的行高
        gridLayoutManager.setSameSizeVertically(true);
        editorPanel = new JXPanel(gridLayoutManager);
        qrCodeEditor = new JBTextArea(40, 50);
        qrCodeEditor.setFont(defaultfont);
        qrCodeEditor.setLineWrap(true);
        qrCodeEditor.setWrapStyleWord(true);
        qrCodeEditor.setSize(new Dimension(500, this.getHeight()));
        jbScrollPane = new JBScrollPane(qrCodeEditor);
        jxImageView = new DefaultImageEditorUI(null);
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setRow(0);
        gridConstraints.setColumn(0);
        gridConstraints.setFill(GridConstraints.FILL_BOTH);
        editorPanel.add(jbScrollPane, gridConstraints);
        jbScrollPane.setBorder(new LineBorder(new Color(0, 0, 0)));
        GridConstraints gridConstraints1 = new GridConstraints();
        gridConstraints1.setRow(1);
        gridConstraints1.setColumn(0);
        gridConstraints1.setFill(GridConstraints.FILL_BOTH);
        editorPanel.add(jxImageView, gridConstraints1);
        jxImageView.setBorder(new LineBorder(new Color(0, 0, 0)));
        URL pictureUrl = getClass().getResource("/icons/image-text.png");
        VirtualFile picture = VirtualFileManager.getInstance().findFileByUrl(VfsUtil.convertFromUrl(pictureUrl));
        if (picture != null) {
            jxImageView.showImage(picture);
        }
        // 尾部
        lastPanel = new JXPanel();
        toQRcodeButton = new JXButton();
        saveAsButton = new JXButton();
        saveAsButton.setText(I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_SAVEASBUTTON_TEXT));
        toQRcodeButton.setText(I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_TOQRCODEBUTTON_TEXT));
        lastPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        lastPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        lastPanel.add(toQRcodeButton);
        lastPanel.add(saveAsButton);
        // 总布局
        this.add(topPanel, BorderLayout.PAGE_START);
        this.add(editorPanel, BorderLayout.CENTER);
        this.add(lastPanel, BorderLayout.PAGE_END);

    }

    /**
     * 第一层配置
     */
    private void configPanelone() {
        // 顶部panel
        topPanelOne = new JXPanel(new FlowLayout(FlowLayout.LEADING));
        copyLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_BASICLABEL_TEXT));

        errorCorrectionOptionsLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_ERRORCORRECTIONOPTIONS_TEXT));
        // 纠错能力
        errorCorrectionOptionsComboBox = new ComboBox(errorCorrectionOptions.toArray(arr));
        errorCorrectionOptionsComboBox.setSelectedIndex(0);
        // 尺寸
        Arrays.stream(SIZEVALUES).forEach(item -> {
            SIZE_TYPE.add(Integer.valueOf(item));
        });
        // 倒序排列
        sizeComboBox = new ComboBox(SIZE_TYPE.stream().sorted(Comparator.reverseOrder()).toArray());
        sizeComboBox.setSelectedIndex(0);
        // 大小
        sizeLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_SIZE_TEXT));
        topPanelOne.add(copyLabel);
        topPanelOne.add(errorCorrectionOptionsLabel);
        topPanelOne.add(errorCorrectionOptionsComboBox);
        topPanelOne.add(sizeLabel);
        topPanelOne.add(sizeComboBox);
        topPanelOne.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }


    /**
     * 第二层配置
     */
    private void configPaneltwo() {
        // 顶部panel
        topPanelTwo = new JXPanel(new FlowLayout(FlowLayout.LEADING));
        topPanelTwo.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        // 颜色
        colorLabel = new JXLabel(I18nBundle.message(I18nBundle.Key.QRCODEIMAGEPANEL_COLOUR_TEXT));
        // 前景色
        JXColorSelectionButton jxColorSelectionButton = new JXColorSelectionButton();
        jxColorSelectionButton.setText("颜色");
        // 背景色
        topPanelTwo.add(colorLabel);
        topPanelTwo.add(jxColorSelectionButton);
    }

    /**
     * @param imgType
     * @return
     */
    private String tobase64ImageTypeStr(String imgType) {
        return String.format(GlobalConstant.BASE64_IMAGETYPE, imgType);
    }

    private void addAllActionListener() {

        saveAsButton.addActionListener(e -> {
            // 另存
            FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            Project openProjects = ProjectManager.getInstance().getDefaultProject();
            VirtualFile virtualFile = FileChooser.chooseFile(chooserDescriptor, openProjects, null);
            String content = qrCodeEditor.getText();
            if (virtualFile == null || StrUtil.isBlank(content)) {
                ApplicationManager.getApplication().invokeLater(() -> NotficationMsg.notifyErrorMsg(I18nBundle.message(I18nBundle.Key.BASE64IMAGEPANEL_SAVEAS_ERROR_TEXT)));
                return;
            }
            // 路径
            ImgEntity img = getImg(content);
            String destPath = virtualFile.getPath();
            String fileName = FileNameExtendEnum.QRCODE_EXTEND.getFileName();
            String pngName = String.format(fileName, System.currentTimeMillis(), img.getImgType());
            String filePath = destPath + File.separator + pngName;
            try {
                ImageIO.write(img.getImage(), img.getImgType(), new File(filePath));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            ApplicationManager.getApplication().invokeLater(() -> NotficationMsg.notifySaveImgMsg(filePath));
        });
//        imgTypeComboBox.addActionListener(e -> {
//            // 获取现在选择的选项
//            String selectedItem = (String) imgTypeComboBox.getSelectedItem();
//            imageTypeTextField.setText(tobase64ImageTypeStr(selectedItem));
//        });

        toQRcodeButton.addActionListener(e -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                String content = qrCodeEditor.getText();
                ImgEntity img = getImg(content);
                jxImageView.showImage(img.getImage(), img.getImgType());
                jxImageView.updateUI();
            });

        });
    }

    private ImgEntity getImg(String base64Content) {
        if (StrUtil.isBlank(base64Content)) {
            return null;
        }
        ImgEntity imgEntity = new ImgEntity();
        QrConfig config = new QrConfig();
        // 高纠错级别
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        BufferedImage generate = QrCodeUtil.generate(base64Content, config);
        imgEntity.setImgType("png");
        imgEntity.setImage(generate);
        return imgEntity;

    }


    @Data
    private static final class ImgEntity {
        // 图片类型
        private String imgType;
        // 图片样式
        private BufferedImage image;
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
        // 异步执行
        UiRefreshThreadUtils.instance(runnable).start();
    }


    @SneakyThrows
    private void showImage() {
        File file = FileUtil.file(imgPath);
        // 解析文件名称
        String decode = QrCodeUtil.decode(file);
        qrCodeEditor.setText(decode);
        qrCodeEditor.setFont(defaultfont);
        qrCodeEditor.setLineWrap(true);
        qrCodeEditor.setWrapStyleWord(true);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(imgPath));
        jxImageView.showImage(virtualFile);
        jxImageView.updateUI();
    }

    /**
     * 获取提示语集合
     *
     * @return {@link List}
     */
    @Override
    public List gethints() {
        return Arrays.stream(ImagePanelTipsConstant.QRCODEIMAGEPANEL).collect(Collectors.toList());
    }

}
