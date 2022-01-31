package fun.gengzi.swing;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.ui.components.JBTabbedPane;
import fun.gengzi.filetype.PictureChooserDescriptor;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.service.StockImpl;
import fun.gengzi.utils.IconButtonUtils;
import fun.gengzi.utils.UiRefreshThreadUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXImageView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

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
    private JButton amplifyButton;
    private JButton narrowButton;
    private JPanel showJPanel;
    private JTextField pathTextField;
    private JPanel last;
    private JLabel path;
    private JButton viewButton;

    // 随机图片面板
    private GrilsImagePanel grilsImagePanel;

    // img 选项卡窗口
    private JTabbedPane imgTabbedPane;

    private PixelImagePanel pixelImagePanel;

    // 日志
    private static final Logger LOG = Logger.getInstance(ImageShow.class);

    // 存储所有选项卡中的面板
    private static final ConcurrentHashSet allTabbedPane = new ConcurrentHashSet<Object>();

    public ImageShow() {
        LOG.info("加载开始");
        // 初始化组件
        initLoadJComponent();
        // 添加监听器
        addAllListener();

    }


    private void addAllListener() {

        grilsImagePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                grilsImagePanel.getJxImageView().setPreferredSize(new Dimension(grilsImagePanel.getWidth(), grilsImagePanel.getHeight()));
            }
        });


        // 添加选项卡监听器
        imgTabbedPane.addChangeListener(new ChangeListener() {
            @SneakyThrows
            @Override
            public void stateChanged(ChangeEvent e) {
                // 获取图片路径
                String imgPath = pathTextField.getText();
                if (ObjectUtil.isEmpty(imgPath)) {
                    // 提示
//                    JBPopupFactory instance = JBPopupFactory.getInstance();
//                    JBPopup jbPopup = instance.createMessage("place input image path,thanks!");
//                    jbPopup.show(showJPanel);
                    NotficationMsg.notifySelectImgMsg("");
                    return;
                }
                JBTabbedPane source = (JBTabbedPane) e.getSource();
                // 获取选中的component
                Component selectedComponent = source.getSelectedComponent();
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
    private void initLoadJComponent() {
        // 初始化img选项卡窗口，默认加载一张图
        imgTabbedPane = new JBTabbedPane();

        grilsImagePanel = new GrilsImagePanel();
        // 添加各个面板
        imgTabbedPane.addTab("美女图片", grilsImagePanel);
        allTabbedPane.add(grilsImagePanel);

        AsciImagePanel asciImagePanel = new AsciImagePanel();
        imgTabbedPane.addTab("ASCI图片", asciImagePanel);
        allTabbedPane.add(asciImagePanel);

        BlackandWhiteImagePanel blackandWhiteImagePanel = new BlackandWhiteImagePanel();
        imgTabbedPane.addTab("灰色图片", blackandWhiteImagePanel);
        allTabbedPane.add(blackandWhiteImagePanel);


        pixelImagePanel = new PixelImagePanel();
        imgTabbedPane.addTab("像素图片", pixelImagePanel);
        allTabbedPane.add(pixelImagePanel);


        showJPanel.add(imgTabbedPane);

    }
}
