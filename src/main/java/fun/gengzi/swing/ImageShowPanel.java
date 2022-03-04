package fun.gengzi.swing;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.intellij.util.ui.ImageUtil;
import fun.gengzi.constant.ImagePanelTipsConstant;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.imgeservice.ImagePanelHint;
import fun.gengzi.message.NotficationMsg;
import fun.gengzi.utils.GifDecoder;
import fun.gengzi.utils.UiRefreshThreadUtils;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    private Runnable runnable;
    private String imagePath;
    // 固定线程执行
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private GifDecoder.GifImage gifImage;


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
        this.runnable = () -> gif();
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
        // gif 单独处理
        // 判断是否为 gif ，后缀
        if (imgPath.endsWith(ImgUtil.IMAGE_TYPE_GIF)) {
            this.imagePath = imgPath;
            // 是。执行gif 解析，循环播放
            byte[] bytes = FileUtil.readBytes(imagePath);
            gifImage = GifDecoder.read(bytes);
            int delay = gifImage.getDelay(0);
            if (delay <= 100) {
                delay = 100;
            }
            executorService.scheduleAtFixedRate(runnable, delay, delay, TimeUnit.MILLISECONDS);
        }else{
            // 不是，既往
            jxImageView.setImage(new File(imgPath));
            jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth(), this.jxPanel.getHeight()));
            jxImageView.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            jxImageView.updateUI();
        }
    }

    private volatile int index = 0;

    @SneakyThrows
    private void gif() {
        int frameCount = gifImage.getFrameCount();
        if (index == frameCount - 1) {
            index = 0;
        } else {
            index++;
        }
        jxImageView.setImage(gifImage.getFrame(index));
        jxImageView.updateUI();
    }

    @Override
    public List gethints() {
        return Arrays.stream(ImagePanelTipsConstant.IMAGESHOWPANEL).collect(Collectors.toList());
    }
}
