package fun.gengzi.swing;


import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import fun.gengzi.enums.FileNameExtendEnum;
import fun.gengzi.imgeservice.ImageFilePathProcess;
import fun.gengzi.message.NotficationMsg;
import lombok.SneakyThrows;
import org.jdesktop.swingx.JXImageView;
import org.jdesktop.swingx.JXPanel;

import java.awt.*;
import java.io.File;


/**
 * <h1>TODO文字水印视图</h1>
 *
 * @author gengzi
 * @date 2022年1月29日15:05:08
 */
public class PressTextImagePanel extends JXPanel implements ImageFilePathProcess {
    private JXImageView jxImageView;
    private JXPanel jxPanel;

    /**
     * 初始化面板
     */
    public PressTextImagePanel() {
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
        boolean blank = NotficationMsg.isBlank(imgPath);
        if (blank) {
            return;
        }
        File file = FileUtil.file(imgPath);
        String parent = FileUtil.getParent(imgPath, 1);
        // 解析文件名称
        String name = FileNameUtil.mainName(file);
        String extName = FileNameUtil.extName(file);
        String newName = String.format(FileNameExtendEnum.BLACKANDWHITE_EXTEND.getFileName(), name, extName);
        String newImagePath = parent + File.pathSeparator + newName;
        ImgUtil.gray(file, FileUtil.file(newImagePath));
        jxImageView.setImage(new File(newImagePath));
        jxImageView.setPreferredSize(new Dimension(this.jxPanel.getWidth(), this.jxPanel.getHeight()));
        jxImageView.setMinimumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        jxImageView.updateUI();
    }
}
