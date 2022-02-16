package fun.gengzi.listener;

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

    private JLabel jLabel;

    private List hits;

    // 控制第几条
    private int index = 1;

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

    @Override
    public void actionPerformed(ActionEvent e) {
        int size = hits.size();
        jLabel.setText(hits.get(index % size).toString());
        if (size >= index) {
            index++;
        } else {
            index = 1;
        }

    }
}