package me.xuxiaoxiao.chatapi.qq.entity.contact;

import java.io.Serializable;

/**
 * QQ联系人
 */
public class QQContact implements Serializable, Cloneable {
    /**
     * 联系人id
     */
    public long id;
    /**
     * 联系人名称
     */
    public String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QQContact qqContact = (QQContact) o;

        if (id != qqContact.id) {
            return false;
        }
        return name != null ? name.equals(qqContact.name) : qqContact.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public QQContact clone() {
        try {
            return (QQContact) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
