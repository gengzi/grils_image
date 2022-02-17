package fun.gengzi.listener;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * <h1>滚动提示语监听器</h1>
 * <p>
 * 不同面板触发不同的提示语
 *
 * @author gengzi
 * @date 2022年2月16日19:24:20
 */
@Getter
@Setter
public class ScrollingPromptListener implements ActionListener {

    // 展示文本的组件
    private JLabel jLabel;

    // 文本集合
    private List hits;

    // 控制第几条
    private volatile int index = 1;

    /**
     * 构造方法
     *
     * @param jLabel 控件
     * @param hits   提示语集合
     */
    public ScrollingPromptListener(JLabel jLabel, List hits) {
        this.jLabel = jLabel;
        this.hits = hits;
    }

    public void setHits(List hits) {
        this.hits = hits;
        // 每次重新set hits ，就重置 index 的值
        index = 1;
    }

    /**
     * 顺序展示滚动文本
     * <p>
     * 滚动原理，通过%集合长度求余数的方式，得到集合下标，获取文本。集合下标的范围肯定在[0，size-1] 之间
     * 执行一次，让除数增加1，直到除数与size 一致，重置为除数为1 ，控制循环下一条文本
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Assert.notNull(hits, () -> new IllegalArgumentException("hits is null"));
        Assert.notNull(jLabel, () -> new IllegalArgumentException("jLabel is null"));
        int size = hits.size();
        jLabel.setText(hits.get(index % size).toString());
        if (size >= index) {
            index++;
        } else {
            index = 1;
        }

    }
}