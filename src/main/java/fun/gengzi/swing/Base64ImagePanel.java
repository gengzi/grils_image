package fun.gengzi.swing;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.components.JBScrollPane;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.utils.I18nBundle;
import groovy.ui.text.TextEditor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * <h1>提供Base64图片转换</h1>
 *
 * @author gengzi
 * @date 2022年2月4日15:32:13
 */
@Getter
public class Base64ImagePanel extends JXPanel implements ImageFilePathProcess {
    private JXImageView jxImageView;
    private JTextArea base64Editor;
    private JBScrollPane jbScrollPane;
    private JXPanel jxPanel;
    private JXPanel lastPanel;
    private JXPanel editorPanel;
    private static final Logger LOG = Logger.getInstance(Base64ImagePanel.class);
    private static final int BLACK = new Color(0, 0, 0).getRGB();
    private static final int WHITE = new Color(232, 229, 229).getRGB();
    private double SW = 192;
    private JTextField imageThreshold;
    private JXLabel thresholdLabel;
    private JXButton okButton;
    private String imgPath;

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
    }

    private void initCompant() throws IOException {
        editorPanel = new JXPanel();
        base64Editor = new JTextArea();
        jbScrollPane = new JBScrollPane(base64Editor);
        lastPanel = new JXPanel();
        thresholdLabel = new JXLabel();
        thresholdLabel.setText(I18nBundle.message(I18nBundle.Key.PIXELIMAGEPANEL_THRESHOLD));
        okButton = new JXButton();
        okButton.setText("ok");
        lastPanel.setLayout(new BorderLayout());
        editorPanel.setLayout(new BorderLayout());
        jxImageView = new JXImageView();
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        imageThreshold = new JTextField();
        imageThreshold.setToolTipText(I18nBundle.message(I18nBundle.Key.PIXELIMAGEPANEL_IMGPATH_TOOLTIPTEXT));
        lastPanel.add(thresholdLabel, BorderLayout.LINE_START);
        lastPanel.add(imageThreshold, BorderLayout.CENTER);
        lastPanel.add(okButton, BorderLayout.LINE_END);
        // 编辑器部分
        editorPanel.add(jxImageView,BorderLayout.LINE_START);
        editorPanel.add(jbScrollPane,BorderLayout.LINE_START);
        this.add(lastPanel, BorderLayout.PAGE_END);
        this.add(editorPanel, BorderLayout.CENTER);
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
        // 解析文件名称
        String extName = FileNameUtil.extName(file);
        BufferedImage src = ImageIO.read(file);
        String base64 = ImgUtil.toBase64DataUri(src, extName);
        base64Editor.setText(base64);
        base64Editor.setFont(new Font("Serif", Font.ITALIC, 16));
        base64Editor.setLineWrap(true);
        base64Editor.setWrapStyleWord(true);
        base64Editor.setPreferredSize(new Dimension(this.jxPanel.getWidth()/2-30, this.jxPanel.getHeight()));
        jxImageView.setImage(new File(imgPath));
        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth()/2-30, this.jxPanel.getHeight()));
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

}
