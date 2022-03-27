package fun.gengzi.swing;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.date.DateUtil;
import com.intellij.codeInsight.preview.ImagePreviewComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.rt.debugger.ImageSerializer;
import com.intellij.ui.components.JBTabbedPane;
import fun.gengzi.filetype.PictureChooserDescriptor;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.imgeservice.ImagePanelHint;
import fun.gengzi.listener.ScrollingPromptListener;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.utils.I18nBundle;
import lombok.Getter;
import lombok.SneakyThrows;
import org.intellij.images.editor.ImageDocument;
import org.intellij.images.ui.ImageComponent;
import org.jdesktop.swingx.JXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;

/**
 * <h1> 图片展示 </h1>
 *
 * @author Administrator
 * @date 2022/1/22 13:42
 */
@Getter
public class ImageShow {
    // 主面板
    private JPanel panel;
    // 主面板顶层面板
    private JPanel top;
    private JPanel showJPanel;
    private JTextField pathTextField;
    private JPanel last;
    private JLabel path;
    private JButton viewButton;
    private JLabel tipsJlabel;

    // 随机图片面板
//    private GrilsImagePanel grilsImagePanel;

    // img 选项卡窗口
    private JTabbedPane imgTabbedPane;
    private PixelImagePanel pixelImagePanel;
    // 二维码面板
    private QRCodeImagePanel qrCodeImagePanel;
    // 预览面板
    private ImageShowPanel imageShowPanel;
    // 面板提示语滚动条
    private ScrollingPromptListener scrollingPromptListener;
    // 日志
    private static final Logger LOG = Logger.getInstance(ImageShow.class);

    // 存储所有选项卡中的面板
    private static final ConcurrentHashSet allTabbedPane = new ConcurrentHashSet<JXPanel>();

    public ImageShow() {
        LOG.info("加载开始");
        // 初始化组件
        initLoadJComponent();
        // 添加监听器
        addAllListener();
        // 添加定时任务
        addTimerTask();
    }

    private void addTimerTask() {
        scrollingPromptListener = new ScrollingPromptListener(tipsJlabel, imageShowPanel.gethints());
        Timer timer = new Timer(5000, scrollingPromptListener);
        timer.start();
    }


    private void addAllListener() {

//        grilsImagePanel.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentShown(ComponentEvent e) {
//                super.componentShown(e);
//                grilsImagePanel.getJxImageView().setPreferredSize(new Dimension(grilsImagePanel.getWidth(), grilsImagePanel.getHeight()));
//            }
//        });


        // 添加选项卡监听器
        imgTabbedPane.addChangeListener(new ChangeListener() {
            @SneakyThrows
            @Override
            public void stateChanged(ChangeEvent e) {
                JBTabbedPane source = (JBTabbedPane) e.getSource();
                // 获取选中的component
                Component selectedComponent = source.getSelectedComponent();
                if (selectedComponent instanceof ImagePanelHint) {
                    ImagePanelHint hint = (ImagePanelHint) selectedComponent;
                    scrollingPromptListener.setHits(hint.gethints());
                }
                // 获取图片路径
                String imgPath = pathTextField.getText();
                boolean blank = NotficationMsg.isBlank(imgPath);
                if (blank) {
                    return;
                }
                if (selectedComponent instanceof ImageFilePathProcess) {
                    ImageFilePathProcess pathProcess = (ImageFilePathProcess) selectedComponent;
                    pathProcess.process(imgPath);
                }
                // 比较是否为同一个对象类型
//                Object o = allTabbedPane.stream().filter(panel -> {
//                    if (ClassUtil.isAssignable(selectedComponent.getClass(), panel.getClass())) {
//                        return true;
//                    }
//                    return false;
//                }).findFirst().orElseThrow(() -> new UnsupportedOperationException());


            }
        });


        // addAllActionListener();
        pathTextField.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                pathTextField.getText();

            }
        });
        /**
         * 文件选择按钮
         */
        viewButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // 文件类型
                FileChooserDescriptor imageDescriptor = PictureChooserDescriptor.getInstance();
                Project openProjects = ProjectManager.getInstance().getDefaultProject();
                // 触发文件选择
                VirtualFile virtualFile = FileChooser.chooseFile(imageDescriptor, openProjects, null);
                String imgPath = virtualFile.getCanonicalPath();
                // 将文件路径设置到文本区域
                pathTextField.setText(imgPath);
                // 刷线当前页面的图片

                Component selectedComponent = imgTabbedPane.getSelectedComponent();
                if (selectedComponent instanceof ImageFilePathProcess) {
                    ImageFilePathProcess pathProcess = (ImageFilePathProcess) selectedComponent;
                    pathProcess.process(imgPath);
                }

            }
        });
    }


    /**
     * 初始化页面中的组件
     */
    @SneakyThrows
    private void initLoadJComponent() {
        // 初始化img选项卡窗口，默认加载一张图
        imgTabbedPane = new JBTabbedPane();
        // 添加各个面板
        imageShowPanel = new ImageShowPanel();
        imgTabbedPane.addTab(I18nBundle.message(I18nBundle.Key.IMGTABBEDPANE_TAB_IMAGESHOWPANEL), imageShowPanel);
        allTabbedPane.add(imageShowPanel);


        Base64ImagePanel base64ImagePanel = new Base64ImagePanel();
        imgTabbedPane.addTab(I18nBundle.message(I18nBundle.Key.IMGTABBEDPANE_TAB_BASE64IMAGEPANEL), base64ImagePanel);
        allTabbedPane.add(base64ImagePanel);

        AsciImagePanel asciImagePanel = new AsciImagePanel();
        imgTabbedPane.addTab(I18nBundle.message(I18nBundle.Key.IMGTABBEDPANE_TAB_ASCIIMAGEPANEL), asciImagePanel);
        allTabbedPane.add(asciImagePanel);

        BlackandWhiteImagePanel blackandWhiteImagePanel = new BlackandWhiteImagePanel();
        imgTabbedPane.addTab(I18nBundle.message(I18nBundle.Key.IMGTABBEDPANE_TAB_BLACKANDWHITEIMAGEPANEL), blackandWhiteImagePanel);
        allTabbedPane.add(blackandWhiteImagePanel);

        pixelImagePanel = new PixelImagePanel();
        imgTabbedPane.addTab(I18nBundle.message(I18nBundle.Key.IMGTABBEDPANE_TAB_PIXELIMAGEPANEL), pixelImagePanel);
        allTabbedPane.add(pixelImagePanel);

        qrCodeImagePanel = new QRCodeImagePanel();
        imgTabbedPane.addTab(I18nBundle.message(I18nBundle.Key.IMGTABBEDPANE_TAB_QRCODEIMAGEPANEL), qrCodeImagePanel);
        allTabbedPane.add(qrCodeImagePanel);

        PlayVideoPanel playVideoPanel = new PlayVideoPanel();
        imgTabbedPane.addTab("video", playVideoPanel);
        allTabbedPane.add(playVideoPanel);


//        // 美女图片
//        grilsImagePanel = new GrilsImagePanel();
//        imgTabbedPane.addTab(I18nBundle.message(I18nBundle.Key.IMGTABBEDPANE_TAB_GRILSIMAGEPANEL), grilsImagePanel);
//        allTabbedPane.add(grilsImagePanel);
        showJPanel.add(imgTabbedPane);
    }
}
