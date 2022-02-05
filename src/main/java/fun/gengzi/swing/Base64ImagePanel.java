package fun.gengzi.swing;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBViewport;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.utils.I18nBundle;
import groovy.ui.text.TextEditor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.intellij.images.editor.ImageDocument;
import org.intellij.images.editor.impl.ImageEditorImpl;
import org.intellij.images.ui.ImageComponent;
import org.intellij.images.vfs.IfsUtil;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * <h1>提供Base64图片转换</h1>
 *
 * @author gengzi
 * @date 2022年2月4日15:32:13
 */
@Getter
public class Base64ImagePanel extends JXPanel implements ImageFilePathProcess {
    private DefaultImageEditorUI jxImageView;
    private JTextArea base64Editor;
    private JBScrollPane jbScrollPane;
    private JXPanel jxPanel;
    private JXPanel lastPanel;
    private JSplitPane editorPanel;
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

        base64Editor = new JTextArea();
        jbScrollPane = new JBScrollPane(base64Editor);
        base64Editor.setMinimumSize(new Dimension(100, this.getHeight()));
        lastPanel = new JXPanel();
        thresholdLabel = new JXLabel();
        thresholdLabel.setText(I18nBundle.message(I18nBundle.Key.PIXELIMAGEPANEL_THRESHOLD));
        okButton = new JXButton();
        okButton.setText("Base64 to Image");
        lastPanel.setLayout(new BorderLayout());
        jxImageView = new DefaultImageEditorUI(null);
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        imageThreshold = new JTextField();
        imageThreshold.setToolTipText(I18nBundle.message(I18nBundle.Key.PIXELIMAGEPANEL_IMGPATH_TOOLTIPTEXT));
        lastPanel.add(okButton, BorderLayout.LINE_START);

        editorPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jbScrollPane, jxImageView);
        this.add(lastPanel, BorderLayout.PAGE_END);
        this.add(editorPanel, BorderLayout.CENTER);
    }

    private void addAllActionListener() {
        okButton.addActionListener(e -> {
            String x = ";base64,";
            // base64 转 image
            String content = base64Editor.getText();
            // data:image/[imageType];base64,
            String[] split = content.split(x);
            String imgtype = split[0].replace("data:image/", "");
            BufferedImage bufferedImage = ImgUtil.toImage(split[split.length - 1]);
            ImageWriter writer = ImgUtil.getWriter(bufferedImage, imgtype);
            try {
                ImgUtil.write(bufferedImage, imgtype, new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\wallhaven-e7rje8.jpg")));
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }


            JBPopupMenu jbPopupMenu = new JBPopupMenu();
            jbPopupMenu.setPreferredSize(new Dimension(200, 200));
            jbPopupMenu.add("hahaha");
            this.add(jbPopupMenu, BorderLayout.CENTER);
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
//        ProjectManager instance = ProjectManager.getInstance();
//        Project defaultProject = instance.getDefaultProject();
//        @NotNull Project[] openProjects = instance.getOpenProjects();
//        VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(imgPath));
//        FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(openProjects[0]);
////        VirtualFileManager instance1 = VirtualFileManager.getInstance();
////        VirtualFile picture = instance1.findFileByUrl(imgPath);
//        fileEditorManager.openFile(fileByIoFile, true);
//
//        EditorWindow currentWindow = fileEditorManager.getCurrentWindow();
//
//        JBViewport jbViewport = new JBViewport();
//
//
//        ImageEditorImpl imageEditor = new ImageEditorImpl(openProjects[0],fileByIoFile);


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
        System.out.println(base64);
        base64Editor.setText(base64);
        base64Editor.setFont(new Font("Consolas", Font.PLAIN, 16));
        base64Editor.setLineWrap(true);
        base64Editor.setWrapStyleWord(true);
        base64Editor.setPreferredSize(new Dimension(200, this.jxPanel.getHeight()));
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(imgPath));
        jxImageView.showImage(virtualFile);

//        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth() / 2 - 30, this.jxPanel.getHeight()));
//        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

}
