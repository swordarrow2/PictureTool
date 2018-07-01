package me.xuxiaoxiao.chatapi.qq.entity.message;

import java.io.Serializable;

/**
 * QQ未知类型消息
 */
public class QQUnknown extends QQMessage implements Serializable, Cloneable {
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public QQUnknown clone() {
        return (QQUnknown) super.clone();
    }
}
