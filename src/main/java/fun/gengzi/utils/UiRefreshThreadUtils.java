package fun.gengzi.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.swing.*;

/**
 * <h1>ui异步刷新工具类型</h1>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UiRefreshThreadUtils extends Thread {

    // 异步任务
    private Runnable runnable;

    public static UiRefreshThreadUtils instance(Runnable runnable) {
        Assert.notNull(runnable, () -> {
            throw new IllegalArgumentException();
        });
        UiRefreshThreadUtils uiRefreshThreadUtils = new UiRefreshThreadUtils();
        uiRefreshThreadUtils.runnable = runnable;
        return uiRefreshThreadUtils;
    }


    @Override
    public void run() {
        if (ObjectUtil.isEmpty(runnable)) {
            throw new IllegalArgumentException();
        }
        SwingUtilities.invokeLater(runnable);// 将对象排到事件派发线程的队列中
    }


}
