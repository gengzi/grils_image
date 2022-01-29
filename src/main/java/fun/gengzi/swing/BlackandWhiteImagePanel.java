package fun.gengzi.swing;


import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.service.StockImpl;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXPanel;

import java.awt.*;
import java.io.File;


/**
 * <h1>黑白照片视图</h1>
 *
 * @author gengzi
 * @date 2022年1月29日15:05:08
 */
public class BlackandWhiteImagePanel extends JXPanel implements ImageFilePathProcess {
    private JXImageView jxImageView;
    private JXPanel jxPanel;

    /**
     * 初始化面板
     */
    public BlackandWhiteImagePanel() {
        // 根据
        jxImageView = new JXImageView();
        this.jxPanel = this;
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.add(jxImageView);

    }

    /**
     * 根据图片路径处理图片
     *
     * @param imgPath 图片路径
     */
    @SneakyThrows
    @Override
    public void process(String imgPath) {
        jxImageView.setImage(new File(imgPath));
        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth(),this.jxPanel.getHeight()));
        jxImageView.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        jxImageView.updateUI();
    }
}
