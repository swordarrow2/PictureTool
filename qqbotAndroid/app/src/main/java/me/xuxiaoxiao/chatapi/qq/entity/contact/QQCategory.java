package me.xuxiaoxiao.chatapi.qq.entity.contact;

import java.io.Serializable;
import java.util.HashMap;

/**
 * QQ分组
 */
public class QQCategory implements Serializable, Cloneable {
    /**
     * 分组id
     */
    public int index;
    /**
     * 分组名称
     */
    public String name;
    /**
     * 分组内的好友
     */
    public HashMap<Long, QQUser> friends = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QQCategory that = (QQCategory) o;

        if (index != that.index) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return friends != null ? friends.equals(that.friends) : that.friends == null;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (friends != null ? friends.hashCode() : 0);
        return result;
    }

    @Override
    public QQCategory clone() {
        try {
            QQCategory qqCategory = (QQCategory) super.clone();
            if (this.friends != null) {
                qqCategory.friends = (HashMap<Long, QQUser>) this.friends.clone();
            }
            return qqCategory;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
