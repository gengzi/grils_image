package fun.gengzi.swing;


import cn.hutool.core.io.IoUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.jcef.JBCefBrowser;
import lombok.SneakyThrows;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;


/**
 * <h1>视频预览视图</h1>
 *
 *
 * 现在排查只有 youtube 可以实现，这个版本的 cef 是没有 flash 功能的。也不支持 html5 的视频播放。 （chrome 版本84）
 * youtube 视频的解决办法，在不使用flash 的情况下，也能正常播放。
 *
 * 原来是认为一些视频网站写的 播放视频的不规范，导致idea cef 不播放。后来发现压根就无法播放
 *
 *
 * 解决思路：1，是否高版本的idea 绑定的 cef 版本高，就支持了html5 的视频播放
 * 2,使用cef 提供的api 接口，能否让这个浏览器，开启 flash 的功能。或者禁用同源策略。这些待学习 cef 这个开源软件才能知道，暂时在idea 提供的接口下没有看到
 *
 *
 * 一些其他点：发现使用手动加载html 的时候，引入的js  和 css 都会从，file:jbcefbrowser/// 这个目录下查找。导致引入本地的js ，css 都无法被找到。
 * 这是因为源码中，加载本地html，设置的路径就是这个。把这个路径提供给了 cef 这个接口
 *
 *解决办法：1，将所有的图片，js css 都放进要执行的html 中
 * 2，开一个web服务，将这些资源文件，都加载进入，然后html 中使用web链接的方式请求。这就要求，在开启此插件，需要实例化一个web服务器，将文件都载入。提供接口访问
 * 3，最好的解决办法还是，能否从cef 这个开源软件上下手，通过接口或者什么形式，让html找到这些文件
 *
 *
 *
 * 有了cef 的存在，我觉得很多插件都没必要再使用 java swing 来画图写了，麻烦。通过html来初始化插件界面，通过请求，来请求插件接口，来实现插件的功能。有很多现成的页面和功能
 * 直接拿来使用即可。或者甚至不需要idea 中的功能时，单独的html 就解决了问题。
 *
 *
 *
 *
 *
 * @author gengzi
 * @date 2022年2月2日12:58:17
 */
public class PlayVideoPanel extends JXPanel {
    private final JXPanel jxPanel;
    private final JComponent component;
    private Runnable runnable;
    private String imagePath;


    /**
     * 初始化面板
     */
    @SneakyThrows
    public PlayVideoPanel() {
//        JCefAppConfig jCefAppConfig = JCefAppConfig.getInstance();
//        List<String> appArgsAsList = jCefAppConfig.getAppArgsAsList();
//        appArgsAsList.add("--disable-web-security");
//        appArgsAsList.add("--enable-system-flash");
//
//
//
//        JBCefApp jbCefApp = JBCefApp.getInstance();
//        JBCefClient client = jbCefApp.createClient();






        JBCefBrowser jbCefBrowser = new JBCefBrowser("test");
        URL resource = this.getClass().getResource("/html/index.html");
        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(VfsUtil.convertFromUrl(resource));
        InputStream inputStream = file.getInputStream();
        String s = IoUtil.readUtf8(inputStream);

//        FileReader fileReader = new FileReader(pictureUrl.getFile());
//        String s = fileReader.readString();
        jbCefBrowser.loadHTML(s);
//        jbCefBrowser.loadURL("https://v.nrzj.vip/");
        component = jbCefBrowser.getComponent();



        CefBrowser cefBrowser = jbCefBrowser.getCefBrowser();

        CefClient cefClient = jbCefBrowser.getJBCefClient().getCefClient();
        // 根据
        this.jxPanel = this;
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        this.add(component, BorderLayout.CENTER);


    }
}
