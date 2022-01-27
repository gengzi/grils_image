package fun.gengzi.swing;

import com.intellij.openapi.diagnostic.Logger;
import fun.gengzi.service.StockImpl;
import fun.gengzi.utils.UiRefreshThreadUtils;
import lombok.Getter;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXImageView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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
    private JXBusyLabel jxBusyLabel;
    private JXImageView jxImageView;
    private Runnable runnable;
    private ActionEvent event;

    private static final Logger LOG = Logger.getInstance(ImageShow.class);

    public ImageShow() {
        LOG.info("加载开始");
        // 初始化组件
        initLoadJComponent();
        // 异步任务
        this.runnable = () -> showImage(this.event);
        // 向上按钮监听器,向下按钮监听器
        addAllActionListener();
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
        jxBusyLabel = new JXBusyLabel();
        jxImageView = new JXImageView();
        try {
            jxImageView.setImage(new File(StockImpl.upOrDownImage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagepenel.add(jxImageView, BorderLayout.CENTER);
    }

}
