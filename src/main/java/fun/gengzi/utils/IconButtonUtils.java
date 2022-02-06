package fun.gengzi.utils;


import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.util.ui.JBImageIcon;
import com.intellij.util.ui.JBUI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IconButtonUtils {

    /**
     * icon图标类型按钮
     *
     * @param button
     * @return
     */
    public static JButton icon(JButton button) {
        button.setMinimumSize(new Dimension(26, 24));
        button.setPreferredSize(new Dimension(26, 24));
        button.setBackground(new Color(242, 242, 242, 255));
        button.setForeground(new Color(0, 0, 0, 255));
        button.setAlignmentX((float) 0.5);
        button.setAlignmentY((float) 0.5);
        button.setMaximumSize(new Dimension(32767, 32767));
        // 边框不被绘制
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.updateUI();
        return button;
    }


}
