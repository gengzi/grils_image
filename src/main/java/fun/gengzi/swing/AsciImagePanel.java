package fun.gengzi.swing;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.intellij.notification.*;
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
import fun.gengzi.service.StockImpl;
import fun.gengzi.utils.UiRefreshThreadUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * <h1>Asci图片</h1>
 *
 * @author gengzi
 * @date 2022年1月30日22:59:20
 */
@Getter
public class AsciImagePanel extends JXPanel implements ImageFilePathProcess {
    private JXImageView jxImageView;
    private JXPanel jxPanel;
    private static final Logger LOG = Logger.getInstance(AsciImagePanel.class);

    /**
     * 初始化面板
     */
    @SneakyThrows
    public AsciImagePanel() {
        this.jxPanel = this;
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        // 初始化面板组件
        initCompant();
    }

    private void initCompant() throws IOException {
        jxImageView = new JXImageView();
        jxImageView.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
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
        File file = FileUtil.file(imgPath);
        String parent = PathUtil.getParent(imgPath);
        // 解析文件名称
        String name = FileNameUtil.mainName(file);
        String extName = FileNameUtil.extName(file);
        String newName = String.format(FileNameExtendEnum.ASCIIMG_EXTEND.getFileName(), name, extName);
        String newImagePath = FileUtil.getAbsolutePath(parent + File.separator + newName);
        LOG.info("图片绝对路径：" + newImagePath);
        imgToAsci(imgPath, newImagePath);
        jxImageView.setImage(new File(newImagePath));
        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth(), this.jxPanel.getHeight()));
        jxImageView.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));


        NotficationMsg.notifySaveImgMsg(newImagePath);
    }


    @SneakyThrows
    private void imgToAsci(String imgPath, String newImgPath) {
        // initialize cache
        AsciiImgCache cache = AsciiImgCache.create(new Font(
                "Courier", Font.BOLD, 10), new char[]{'\\', ' ', '/'});
        // load image
        BufferedImage portraitImage = ImageIO.read(new File(imgPath));
        // initialize converters
        AsciiToImageConverter imageConverter =
                new AsciiToImageConverter(cache, new ColorSquareErrorFitStrategy());
        AsciiToStringConverter stringConverter =
                new AsciiToStringConverter(cache, new StructuralSimilarityFitStrategy());
        // image output
        ImageIO.write(imageConverter.convertImage(portraitImage), "png",
                new File(newImgPath));
        // string converter, output to console
        System.out.println(stringConverter.convertImage(portraitImage));
    }

}
