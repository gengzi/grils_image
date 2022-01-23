package fun.gengzi.service;


import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;


public class StockImpl {
    // 本地存储
    public static final String DIR = "C:\\Users\\Administrator\\Pictures\\%s.jpg";
    // 最大
    public static final Integer MAX_SIZE = 1572864; // 1.5M
    // 缓存列表
    public static final ConcurrentHashSet<String> fileSet = new ConcurrentHashSet<>();

    /**
     * 图片下载，存储在 c 盘图片
     * <p>
     * 预下载
     *
     * @return
     */
    public static String downImage() {
        long filename = System.currentTimeMillis();
        String imgfilepath = String.format(DIR, filename);
        String result = HttpUtil.get("https://api.r10086.com/%E6%AD%BB%E5%BA%93%E6%B0%B4%E8%90%9D%E8%8E%89.php");
        long size = HttpUtil.downloadFile("https://api.r10086.com/" + result.split("<br>\\.\\.")[1], FileUtil.file(imgfilepath));
        if (size > MAX_SIZE) {
            // 压缩存储
            Img.from(FileUtil.file(imgfilepath))
                    .setQuality(0.8)//压缩比率
                    .write(FileUtil.file(imgfilepath));
        }
        return imgfilepath;
    }


    public static String upOrDownImage() {
        try {
            String url = "https://tvv.tw/xjj/meinv/imgawfdawf11.php";
            // 执行异步任务，下载图片
            ThreadUtil.execAsync(() -> {
                for (int i = 0; i < 10; i++) {
                    String file = downImage(url);
                    fileSet.add(file);
                }
            });
            String first = fileSet.stream().findFirst().orElse(null);
            if (first == null) {
                return downImage(url);
            }
            fileSet.remove(first);
            return first;
        }catch (Exception e){
            return "";
        }

    }

    public static String downImage(String url) {
        long filename = System.currentTimeMillis();
        String imgfilepath = String.format(DIR, filename);
        long size = HttpUtil.downloadFile(url, FileUtil.file(imgfilepath));
        if (size > MAX_SIZE) {
            // 压缩存储
            Img.from(FileUtil.file(imgfilepath))
                    .setQuality(0.8)//压缩比率
                    .write(FileUtil.file(imgfilepath));
        }
        return imgfilepath;
    }

//    public static void main(String[] args) {
//        // 263149
//        String s = StockImpl.downImage("https://tvv.tw/xjj/meinv/imgawfdawf11.php");
//    }

}