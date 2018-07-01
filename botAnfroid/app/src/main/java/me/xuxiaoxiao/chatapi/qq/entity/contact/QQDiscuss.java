package me.xuxiaoxiao.chatapi.qq.entity.contact;

import java.io.Serializable;
import java.util.HashMap;

/**
 * QQ讨论组
 */
public class QQDiscuss extends QQContact implements Serializable, Cloneable {
    /**
     * 讨论组的所有成员
     */
    public HashMap<Long, Member> members;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        QQDiscuss qqDiscuss = (QQDiscuss) o;

        return members != null ? members.equals(qqDiscuss.members) : qqDiscuss.members == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (members != null ? members.hashCode() : 0);
        return result;
    }

    @Override
    public QQDiscuss clone() {
        QQDiscuss qqDiscuss = (QQDiscuss) super.clone();
        if (this.members != null) {
            qqDiscuss.members = (HashMap<Long, Member>) this.members.clone();
        }
        return qqDiscuss;
    }

    /**
     * 讨论组成员
     */
    public static class Member implements Serializable, Cloneable {
        /**
         * 讨论组成员id
         */
        public long id;
        /**
         * 讨论组成员名称
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

            Member member = (Member) o;

            if (id != member.id) {
                return false;
            }
            return name != null ? name.equals(member.name) : member.name == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public Member clone() {
            try {
                return (Member) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
    }
}
