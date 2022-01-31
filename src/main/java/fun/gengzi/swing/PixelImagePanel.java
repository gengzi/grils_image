package fun.gengzi.swing;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.PathUtil;
import fun.gengzi.asciimg.image.AsciiImgCache;
import fun.gengzi.asciimg.image.character_fit_strategy.ColorSquareErrorFitStrategy;
import fun.gengzi.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;
import fun.gengzi.asciimg.image.converter.AsciiToImageConverter;
import fun.gengzi.asciimg.image.converter.AsciiToStringConverter;
import fun.gengzi.enums.FileNameExtendEnum;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.message.NotficationMsg;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jf.util.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * <h1>Asci图片</h1>
 *
 * @author gengzi
 * @date 2022年1月30日22:59:20
 */
@Getter
public class PixelImagePanel extends JXPanel implements ImageFilePathProcess {
    private JXImageView jxImageView;
    private JXPanel jxPanel;
    private JXPanel lastPanel;
    private static final Logger LOG = Logger.getInstance(PixelImagePanel.class);
    private static final int BLACK = new Color(0, 0, 0).getRGB();
    private static final int WHITE = new Color(232, 229, 229).getRGB();
    private double SW = 192;
    private JTextField imageThreshold;
    private JXLabel thresholdLabel;
    private JXButton okButton;
    private String imgPath;

    /**
     * 初始化面板
     */
    @SneakyThrows
    public PixelImagePanel() {
        this.jxPanel = this;
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        // 初始化面板组件
        initCompant();
        // 监听器
        addAllActionListener();
    }

    private void initCompant() throws IOException {
        lastPanel = new JXPanel();
        thresholdLabel = new JXLabel();
        thresholdLabel.setText("threshold:");
        okButton = new JXButton();
        okButton.setText("ok");
        lastPanel.setLayout(new BorderLayout());
        jxImageView = new JXImageView();
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        imageThreshold = new JTextField();
        lastPanel.add(thresholdLabel, BorderLayout.LINE_START);
        lastPanel.add(imageThreshold, BorderLayout.CENTER);
        lastPanel.add(okButton, BorderLayout.LINE_END);
        this.add(lastPanel, BorderLayout.PAGE_END);
        this.add(jxImageView, BorderLayout.CENTER);
    }

    private void addAllActionListener() {
        okButton.addActionListener(e -> {
            this.process(imgPath);
        });
    }


    /**
     * 根据图片路径处理图片
     *
     * @param imgPath 图片路径
     */
    @SneakyThrows
    @Override
    public void process(String imgPath) {
        this.imgPath = imgPath;
        File file = FileUtil.file(imgPath);
        String parent = PathUtil.getParent(imgPath);
        // 解析文件名称
        String name = FileNameUtil.mainName(file);
        String extName = FileNameUtil.extName(file);
        String newName = String.format(FileNameExtendEnum.PIXELIMG_EXTEND.getFileName(), name, extName);
        String newImagePath = FileUtil.getAbsolutePath(parent + File.separator + newName);
        LOG.info("图片绝对路径：" + newImagePath);
        imgToPixel(imgPath, newImagePath);
        jxImageView.setImage(new File(newImagePath));
        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth(), this.jxPanel.getHeight()));
        jxImageView.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        NotficationMsg.notifySaveImgMsg(newImagePath);
    }

    @SneakyThrows
    private void imgToPixel(String imgPath, String newImgPath) {
        if (StrUtil.isNotBlank(imageThreshold.getText())) {
            SW = Double.valueOf(imageThreshold.getText());
        }
        FileOutputStream out = null;
        try {
            File file = new File(imgPath);
            // 构造Image对象
            BufferedImage src = ImageIO.read(file);
            int width = src.getWidth();
            int height = src.getHeight();
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 绘制 缩小  后的图片
            tag.getGraphics().drawImage(src, 0, 0, width, height, null);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int[] rgb = new int[3];
                    int pixel = tag.getRGB(i, j);
                    rgb[0] = (pixel & 0xff0000) >> 16; //r
                    rgb[1] = (pixel & 0xff00) >> 8; //g
                    rgb[2] = (pixel & 0xff); //b
                    float avg = (rgb[0] + rgb[1] + rgb[2]) / 3;
                    if (avg < SW) {
                        tag.setRGB(i, j, BLACK);
                    } else {
                        tag.setRGB(i, j, WHITE);
                    }
                }
            }
            ImageIO.write(tag, FileNameUtil.extName(imgPath), new File(newImgPath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
