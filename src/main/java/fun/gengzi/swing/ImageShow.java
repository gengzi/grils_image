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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.ui.components.JBTabbedPane;
import fun.gengzi.imgeservice.ImageFilePathProcess;
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

    private JButton buttonup;
    private JButton buttondown;
    private JPanel imagepenel;
    private JPanel panel;
    private JPanel top;
    private JButton amplifyButton;
    private JButton narrowButton;
    private JPanel showJPanel;
    private JTextField pathTextField;
    private JPanel last;
    private JLabel path;
    private JButton viewButton;
    private JXBusyLabel jxBusyLabel;
    private JXImageView jxImageView;
    private Runnable runnable;
    private ActionEvent event;

    // img 选项卡窗口
    private JTabbedPane imgTabbedPane;

    // 日志
    private static final Logger LOG = Logger.getInstance(ImageShow.class);

    // 存储所有选项卡中的面板
    private static final ConcurrentHashSet allTabbedPane = new ConcurrentHashSet<Object>();

    public ImageShow() {
        LOG.info("加载开始");
        // 初始化组件
        initLoadJComponent();
        // 异步任务
        this.runnable = () -> showImage(this.event);

        // 添加选项卡监听器
        imgTabbedPane.addChangeListener(new ChangeListener() {
            @SneakyThrows
            @Override
            public void stateChanged(ChangeEvent e) {
                // 获取图片路径
                String imgPath = pathTextField.getText();
                if (ObjectUtil.isEmpty(imgPath)) {
                    // 提示
                    JBPopupFactory instance = JBPopupFactory.getInstance();
                    JBPopup jbPopup = instance.createMessage("place input image path,thanks!");
                    jbPopup.show(showJPanel);
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


        // 向上按钮监听器,向下按钮监听器
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
//                FileChooserDescriptorFactory.createSingleFileDescriptor()
                // 文件选择描述
                FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
                Project openProjects = ProjectManager.getInstance().getDefaultProject();
                // 触发文件选择
                VirtualFile virtualFile = FileChooser.chooseFile(fileChooserDescriptor, openProjects, null);
                String canonicalPath = virtualFile.getCanonicalPath();
                System.out.println(canonicalPath);
                // 将文件路径设置到文本区域
                pathTextField.setText(canonicalPath);

            }
        });
    }

    /**
     * 添加action监听器
     */
    private void addAllActionListener() {
        // 向上按钮
        buttonup.addActionListener(e -> {
            event = e;
            imagepenel.remove(jxImageView);
            jxBusyLabel.setPreferredSize(new Dimension(20, 20));
            jxBusyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jxBusyLabel.setBusy(true);
            imagepenel.add(jxBusyLabel);
            // 点击后，禁止触发
            buttonup.setEnabled(false);
            // 异步执行
            UiRefreshThreadUtils.instance(runnable).start();
        });
        // 向下按钮
        buttondown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                event = e;
                imagepenel.remove(jxImageView);
                jxBusyLabel.setPreferredSize(new Dimension(20, 20));
                jxBusyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                jxBusyLabel.setBusy(true);
                imagepenel.add(jxBusyLabel);
                // 点击后，禁止触发
                buttonup.setEnabled(false);
                // 异步执行
                UiRefreshThreadUtils.instance(runnable).start();
            }
        });
    }

    /**
     * 无用方法
     */
    private void showImage() {
        Component[] components = imagepenel.getComponents();
        buttonup.setEnabled(true);
        jxBusyLabel.setBusy(false);
        imagepenel.remove(jxBusyLabel);
        imagepenel.add(jxImageView);
    }

    /**
     * 展示image
     *
     * @param e 事件属性
     */
    private void showImage(ActionEvent e) {
        try {
            jxImageView.setImage(new File(StockImpl.upOrDownImage()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        buttonup.setEnabled(true);
        jxBusyLabel.setBusy(false);
        imagepenel.remove(jxBusyLabel);
        imagepenel.add(jxImageView);
        Action zoomInAction = jxImageView.getZoomOutAction();
        zoomInAction.actionPerformed(e);
        imagepenel.updateUI();
    }


    /**
     * 初始化页面中的组件
     */
    private void initLoadJComponent() {

        // 加载动画
        jxBusyLabel = new JXBusyLabel();
        jxImageView = new JXImageView();
        try {
            jxImageView.setImage(new File(StockImpl.upOrDownImage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        imagepenel.add(jxImageView, BorderLayout.CENTER);


        // 初始化img选项卡窗口，默认加载一张图
        imgTabbedPane = new JBTabbedPane();
        // 添加各个面板
        imgTabbedPane.addTab("美女图片", jxImageView);
        allTabbedPane.add(jxImageView);

        BlackandWhiteImagePanel blackandWhiteImagePanel = new BlackandWhiteImagePanel();
        imgTabbedPane.addTab("黑白图片", blackandWhiteImagePanel);
        allTabbedPane.add(blackandWhiteImagePanel);
        showJPanel.add(imgTabbedPane);

    }
}
