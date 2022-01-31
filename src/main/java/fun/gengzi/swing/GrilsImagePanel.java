package fun.gengzi.swing;


import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.service.StockImpl;
import fun.gengzi.utils.UiRefreshThreadUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;


/**
 * <h1>随机图片</h1>
 *
 * @author gengzi
 * @date 2022年1月30日22:36:38
 */
@Getter
public class GrilsImagePanel extends JXPanel implements ImageFilePathProcess {
    private JXImageView jxImageView;
    private JXPanel jxPanel;
    private JButton buttonup;
    private JButton buttondown;
    private ActionEvent event;
    // 异步刷新任务
    private Runnable runnable;
    private JXBusyLabel jxBusyLabel;

    private static final Logger LOG = Logger.getInstance(GrilsImagePanel.class);

    /**
     * 初始化面板
     */
    @SneakyThrows
    public GrilsImagePanel() {
        this.jxPanel = this;
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        // 初始化面板组件
        initCompant();
        // 异步任务
        this.runnable = () -> showImage(this.event);
        // 添加监听器
        addAllActionListener();
    }

    private void initCompant() throws IOException {
        buttonup = new JButton("up");
        buttondown = new JButton("down");
        jxBusyLabel = new JXBusyLabel();
        jxImageView = new JXImageView();
        jxImageView.setToolTipText("当前页面资源均来源网络采集，仅供学习，请及时删除！");
        jxImageView.setImage(new File(StockImpl.upOrDownImage()));
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.add(buttondown, BorderLayout.PAGE_END);
        this.add(jxImageView, BorderLayout.CENTER);
        this.add(buttonup, BorderLayout.PAGE_START);
    }


    /**
     * 根据图片路径处理图片
     *
     * @param imgPath 图片路径
     */
    @Override
    public void process(String imgPath) {
    }


    /**
     * 添加action监听器
     */
    private void addAllActionListener() {
        // 向上按钮
        buttonup.addActionListener(e -> {
            event = e;
            this.jxPanel.remove(jxImageView);
            jxBusyLabel.setPreferredSize(new Dimension(20, 20));
            jxBusyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jxBusyLabel.setBusy(true);
            this.jxPanel.add(jxBusyLabel);
            // 点击后，禁止触发
            buttonup.setEnabled(false);
            // 异步执行
            UiRefreshThreadUtils.instance(runnable).start();
        });
        // 向下按钮
        buttondown.addActionListener(e -> {
            event = e;
            this.jxPanel.remove(jxImageView);
            jxBusyLabel.setPreferredSize(new Dimension(20, 20));
            jxBusyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jxBusyLabel.setBusy(true);
            this.jxPanel.add(jxBusyLabel);
            // 点击后，禁止触发
            buttonup.setEnabled(false);
            // 异步执行
            UiRefreshThreadUtils.instance(runnable).start();
        });
    }


    /**
     * 展示image
     *
     * @param e 事件属性
     */
    @SneakyThrows
    private void showImage(ActionEvent e) {
        // 设置图片
        jxImageView.setImage(new File(StockImpl.upOrDownImage()));
        buttonup.setEnabled(true);
        jxBusyLabel.setBusy(false);
        this.jxPanel.remove(jxBusyLabel);
        this.jxPanel.add(jxImageView);
        Action zoomInAction = jxImageView.getZoomOutAction();
        zoomInAction.actionPerformed(e);
        this.jxPanel.updateUI();
    }
}
