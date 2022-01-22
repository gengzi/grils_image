package fun.gengzi;

import com.intellij.codeInsight.preview.ImagePreviewComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.jdesktop.swingx.JXImageView;

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

                Component[] components = imagepenel.getComponents();
                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JXImageView) {
                        JXImageView image = (JXImageView) components[i];
                        try {
                            image.setImage(new File("C:\\Users\\Administrator\\Desktop\\2.jpg"));
                            image.updateUI();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
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
            }
        });
        LOG.info("展示图片");
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
        JXImageView jxImageView = new JXImageView();
        try {
            jxImageView.setImage(new File("C:\\Users\\Administrator\\Desktop\\test.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagepenel.add(jxImageView);
        return panel;
    }

}
