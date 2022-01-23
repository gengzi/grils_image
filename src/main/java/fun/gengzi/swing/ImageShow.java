package fun.gengzi.swing;

import com.intellij.codeInsight.preview.ImagePreviewComponent;
import com.intellij.openapi.diagnostic.Logger;
import fun.gengzi.service.StockImpl;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.painter.TextPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.File;
import java.io.IOException;

/**
 * <h1> 图片展示 </h1>
 *
 * @author Administrator
 * @date 2022/1/22 13:42
 */
public class ImageShow {

    private JButton buttonup;
    private JButton buttondown;
    private JPanel imagepenel;
    private JPanel panel;
    private JXBusyLabel jxBusyLabel;
    private JXImageView jxImageView;

    private static final Logger LOG = Logger.getInstance(ImageShow.class);

    public ImageShow() {
        // 向上按钮监听器
        buttonup.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // 展示上一张图片
                showImage(e);

            }
        });
        // 向下按钮监听器
        buttondown.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // 展示下一张图片
                showImage(e);
            }
        });
        LOG.info("展示图片");
    }

    /**
     * 展示image
     */
    private void showImage(ActionEvent e) {
        Component[] components = imagepenel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JXImageView) {
                JXImageView image = (JXImageView) components[i];
                try {
                    image.setImage(new File(StockImpl.upOrDownImage()));
                    image.updateUI();
                    Action zoomInAction = jxImageView.getZoomOutAction();
                    zoomInAction.actionPerformed(e);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public JButton getButtonup() {
        return buttonup;
    }

    public JButton getButtondown() {
        return buttondown;
    }

    public JPanel getImagepenel() {
        return imagepenel;
    }

    public JPanel getPanel() {
        jxImageView = new JXImageView();
        try {
            jxImageView.setImage(new File(StockImpl.upOrDownImage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jxImageView.setSize(500, 500);
        imagepenel.add(jxImageView);
        return panel;//https://www.fulitu.cc/2022/01/01/805.html
    }

}
