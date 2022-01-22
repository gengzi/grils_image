package fun.gengzi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;

/**
 * <h1> </h1>
 *
 * @author Administrator
 * @date 2022/1/22 12:58
 */
public class ImageMain implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(ImageMain.class);
    private ImageShow imageShow = new ImageShow();

    /**
     * 创建窗口内容
     *
     * @param project
     * @param toolWindow
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory instance = ContentFactory.SERVICE.getInstance();
        Content content = instance.createContent(imageShow.getPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
        LOG.info("初始化内容");
    }

    /**
     * Perform additional initialization routine here.
     *
     * @param toolWindow
     */
    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        LOG.info("初始化");
    }
}
