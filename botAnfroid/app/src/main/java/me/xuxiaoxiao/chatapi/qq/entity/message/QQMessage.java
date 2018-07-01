package me.xuxiaoxiao.chatapi.qq.entity.message;

import me.xuxiaoxiao.chatapi.qq.entity.contact.QQDiscuss;
import me.xuxiaoxiao.chatapi.qq.entity.contact.QQGroup;
import me.xuxiaoxiao.chatapi.qq.entity.contact.QQUser;

import java.io.Serializable;

/**
 * QQ消息
 */
public abstract class QQMessage implements Serializable, Cloneable {
    /**
     * 消息id
     */
    public long id;
    /**
     * 如果是群消息则为消息来源的群，否则为null
     */
    public QQGroup fromGroup;
    /**
     * 如果是群消息则为消息来源的群成员，否则为null
     */
    public QQGroup.Member fromGroupMember;
    /**
     * 如果是讨论组消息则为来源的讨论组，否则为null
     */
    public QQDiscuss fromDiscuss;
    /**
     * 如果是讨论组消息则为消息来源的讨论组成员，否则为null
     */
    public QQDiscuss.Member fromDiscussMember;
    /**
     * 如果是好友消息则为消息来源的好友，否则为null
     */
    public QQUser fromUser;
    /**
     * 消息的接收方，如果是我发给好友的消息，则为好友，否则为自己
     */
    public QQUser toUser;
    /**
     * 消息的创建时间
     */
    public long timestamp;
    /**
     * 消息的内容，多个消息块用空格连接
     */
    public String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QQMessage qqMessage = (QQMessage) o;

        if (id != qqMessage.id) {
            return false;
        }
        if (timestamp != qqMessage.timestamp) {
            return false;
        }
        if (fromGroup != null ? !fromGroup.equals(qqMessage.fromGroup) : qqMessage.fromGroup != null) {
            return false;
        }
        if (fromGroupMember != null ? !fromGroupMember.equals(qqMessage.fromGroupMember) : qqMessage.fromGroupMember != null) {
            return false;
        }
        if (fromDiscuss != null ? !fromDiscuss.equals(qqMessage.fromDiscuss) : qqMessage.fromDiscuss != null) {
            return false;
        }
        if (fromDiscussMember != null ? !fromDiscussMember.equals(qqMessage.fromDiscussMember) : qqMessage.fromDiscussMember != null) {
            return false;
        }
        if (fromUser != null ? !fromUser.equals(qqMessage.fromUser) : qqMessage.fromUser != null) {
            return false;
        }
        if (toUser != null ? !toUser.equals(qqMessage.toUser) : qqMessage.toUser != null) {
            return false;
        }
        return content != null ? content.equals(qqMessage.content) : qqMessage.content == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (fromGroup != null ? fromGroup.hashCode() : 0);
        result = 31 * result + (fromGroupMember != null ? fromGroupMember.hashCode() : 0);
        result = 31 * result + (fromDiscuss != null ? fromDiscuss.hashCode() : 0);
        result = 31 * result + (fromDiscussMember != null ? fromDiscussMember.hashCode() : 0);
        result = 31 * result + (fromUser != null ? fromUser.hashCode() : 0);
        result = 31 * result + (toUser != null ? toUser.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public QQMessage clone() {
        try {
            QQMessage qqMessage = (QQMessage) super.clone();
            if (this.fromGroup != null) {
                qqMessage.fromGroup = this.fromGroup.clone();
            }
            if (this.fromGroupMember != null) {
                qqMessage.fromGroupMember = this.fromGroupMember.clone();
            }
            if (this.fromDiscuss != null) {
                qqMessage.fromDiscuss = this.fromDiscuss.clone();
            }
            if (this.fromDiscussMember != null) {
                qqMessage.fromDiscussMember = this.fromDiscussMember.clone();
            }
            if (this.fromUser != null) {
                qqMessage.fromUser = this.fromUser.clone();
            }
            if (this.toUser != null) {
                qqMessage.toUser = this.toUser.clone();
            }
            return qqMessage;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
