package fun.gengzi.swing;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.containers.ContainerUtil;
import fun.gengzi.constant.GlobalConstant;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.utils.I18nBundle;
import fun.gengzi.utils.UiRefreshThreadUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;

import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;


/**
 * <h1>提供Base64图片转换</h1>
 *
 * @author gengzi
 * @date 2022年2月4日15:32:13
 */
@Getter
public class Base64ImagePanel extends JXPanel implements ImageFilePathProcess {
    // 默认的图片UI
    private DefaultImageEditorUI jxImageView;
    private JXPanel jxPanel;
    private JBTextArea base64Editor;
    private JBScrollPane jbScrollPane;
    private JXPanel lastPanel;
    private JXPanel topPanel;
    private JXPanel editorPanel;
    private static final Logger LOG = Logger.getInstance(Base64ImagePanel.class);
    private static final int BLACK = new Color(0, 0, 0).getRGB();
    private static final int WHITE = new Color(232, 229, 229).getRGB();
    private double SW = 192;
    private JXButton toBase64Button;
    private String imgPath;
    private Runnable runnable;
    private ComboBox<String> imgTypeComboBox;
    private JBTextField imageTypeTextField;
    // 支持的图片格式
    private static final List<String> IMAGE_TYPE = ContainerUtil.immutableList("png", "ico", "bmp", "gif", "jpg", "svg");
    private String[] arr = {};

    /**
     * 初始化面板
     */
    @SneakyThrows
    public Base64ImagePanel() {
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


    private void initCompant() throws IOException {
        // 顶部panel
        topPanel = new JXPanel(new FlowLayout(FlowLayout.LEADING));
        imgTypeComboBox = new ComboBox(IMAGE_TYPE.toArray(arr));
        imgTypeComboBox.setSelectedIndex(0);
        imageTypeTextField = new JBTextField();
        imageTypeTextField.setText(tobase64ImageTypeStr(imgTypeComboBox.getSelectedItem().toString()));
        topPanel.add(imgTypeComboBox);
        topPanel.add(imageTypeTextField);
        topPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        // 中部
        GridLayoutManager gridLayoutManager = new GridLayoutManager(2, 1);
        editorPanel = new JXPanel(gridLayoutManager);
        base64Editor = new JBTextArea(50, 50);
        base64Editor.setSize(new Dimension(500, this.getHeight()));
        jbScrollPane = new JBScrollPane(base64Editor);
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
        // 尾部
        lastPanel = new JXPanel();
        toBase64Button = new JXButton();
        toBase64Button.setText(I18nBundle.message(I18nBundle.Key.BASE64IMAGEPANEL_TOBASE64BUTTON_TEXT));
        lastPanel.setLayout(new BorderLayout());
        lastPanel.add(toBase64Button, BorderLayout.LINE_START);
        // 总布局
        this.add(topPanel, BorderLayout.PAGE_START);
        this.add(editorPanel, BorderLayout.CENTER);
        this.add(lastPanel, BorderLayout.PAGE_END);

    }

    /**
     * @param imgType
     * @return
     */
    private String tobase64ImageTypeStr(String imgType) {
        return String.format(GlobalConstant.BASE64_IMAGETYPE, imgType);
    }

    private void addAllActionListener() {

        imgTypeComboBox.addActionListener(e -> {
            // 获取现在选择的选项
            String selectedItem = (String) imgTypeComboBox.getSelectedItem();
            imageTypeTextField.setText(tobase64ImageTypeStr(selectedItem));
        });

        toBase64Button.addActionListener(e -> {
            String x = ";base64,";
            // base64 转 image
            String content = base64Editor.getText();
            // data:image/[imageType];base64,
            String[] split = content.split(x);
            String imgtype = split[0].replace("data:image/", "");
            BufferedImage bufferedImage = ImgUtil.toImage(split[split.length - 1]);
            jxImageView.showImage(bufferedImage);
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
        // 异步执行
        UiRefreshThreadUtils.instance(runnable).start();
    }


    @SneakyThrows
    private void showImage() {
        File file = FileUtil.file(imgPath);
        // 解析文件名称
        String extName = FileNameUtil.extName(file);
        BufferedImage src = ImageIO.read(file);
        String base64 = ImgUtil.toBase64DataUri(src, extName);
        System.out.println(base64);
        base64Editor.setText(base64);
        base64Editor.setFont(new Font("Consolas", Font.PLAIN, 5));
        base64Editor.setLineWrap(true);
        base64Editor.setWrapStyleWord(true);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(imgPath));
        jxImageView.showImage(virtualFile);
        jxImageView.updateUI();
    }

}
