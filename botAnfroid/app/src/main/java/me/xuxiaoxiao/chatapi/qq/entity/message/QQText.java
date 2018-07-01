package me.xuxiaoxiao.chatapi.qq.entity.message;

import java.io.Serializable;

/**
 * qq文字消息
 */
public class QQText extends QQMessage implements Serializable, Cloneable {
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public QQText clone() {
        return (QQText) super.clone();
    }
}
