package fun.gengzi.swing;


import fun.gengzi.constant.GlobalConstant;
import fun.gengzi.constant.ImagePanelTipsConstant;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.imgeservice.ImagePanelHint;
import fun.gengzi.message.NotficationMsg;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXPanel;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <h1>图片预览视图</h1>
 *
 * @author gengzi
 * @date 2022年2月2日12:58:17
 */
public class ImageShowPanel extends JXPanel implements ImageFilePathProcess, ImagePanelHint {
    private final JXImageView jxImageView;
    private final JXPanel jxPanel;


    /**
     * 初始化面板
     */
    @SneakyThrows
    public ImageShowPanel() {
        // 根据
        jxImageView = new JXImageView();
        this.jxPanel = this;
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.add(jxImageView, BorderLayout.CENTER);
    }

    /**
     * 根据图片路径处理图片
     *
     * @param imgPath 图片路径
     */
    @SneakyThrows
    @Override
    public void process(String imgPath) {
        boolean blank = NotficationMsg.isBlank(imgPath);
        if (blank) {
            return;
        }
        jxImageView.setImage(new File(imgPath));
        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth(), this.jxPanel.getHeight()));
        jxImageView.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        jxImageView.updateUI();
    }

    @Override
    public List gethints() {
        return Arrays.stream(ImagePanelTipsConstant.IMAGESHOWPANEL).collect(Collectors.toList());
    }
}
