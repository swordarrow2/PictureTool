package me.xuxiaoxiao.chatapi.qq.entity.contact;

import java.io.Serializable;
import java.util.HashMap;

/**
 * QQ群
 */
public class QQGroup extends QQContact implements Serializable, Cloneable {
    /**
     * 群code，获取群信息时用到
     */
    public long code;
    /**
     * 群主id
     */
    public long owner;
    /**
     * 群备注名称，手机qq上没有这个功能，桌面qq有
     */
    public String remark;
    /**
     * 群公告
     */
    public String notice;
    /**
     * 群创建时间
     */
    public long createtime;
    /**
     * 群内所有成员
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

        QQGroup qqGroup = (QQGroup) o;

        if (code != qqGroup.code) {
            return false;
        }
        if (owner != qqGroup.owner) {
            return false;
        }
        if (createtime != qqGroup.createtime) {
            return false;
        }
        if (remark != null ? !remark.equals(qqGroup.remark) : qqGroup.remark != null) {
            return false;
        }
        if (notice != null ? !notice.equals(qqGroup.notice) : qqGroup.notice != null) {
            return false;
        }
        return members != null ? members.equals(qqGroup.members) : qqGroup.members == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (code ^ (code >>> 32));
        result = 31 * result + (int) (owner ^ (owner >>> 32));
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (notice != null ? notice.hashCode() : 0);
        result = 31 * result + (int) (createtime ^ (createtime >>> 32));
        result = 31 * result + (members != null ? members.hashCode() : 0);
        return result;
    }

    @Override
    public QQGroup clone() {
        QQGroup qqGroup = (QQGroup) super.clone();
        if (this.members != null) {
            qqGroup.members = (HashMap<Long, Member>) this.members.clone();
        }
        return qqGroup;
    }

    /**
     * QQ群成员
     */
    public static class Member implements Serializable, Cloneable {
        /**
         * 群成员id
         */
        public long id;
        /**
         * 群成员名称
         */
        public String name;
        /**
         * 群成员名片
         */
        public String display;
        /**
         * 群成员所在国家
         */
        public String country;
        /**
         * 群成员所在省份
         */
        public String province;
        /**
         * 群成员所在城市
         */
        public String city;
        /**
         * 群成员性别
         */
        public int gender;
        /**
         * 群成员vip等级
         */
        public int vipLevel;

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
            if (gender != member.gender) {
                return false;
            }
            if (vipLevel != member.vipLevel) {
                return false;
            }
            if (name != null ? !name.equals(member.name) : member.name != null) {
                return false;
            }
            if (display != null ? !display.equals(member.display) : member.display != null) {
                return false;
            }
            if (country != null ? !country.equals(member.country) : member.country != null) {
                return false;
            }
            if (province != null ? !province.equals(member.province) : member.province != null) {
                return false;
            }
            return city != null ? city.equals(member.city) : member.city == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (display != null ? display.hashCode() : 0);
            result = 31 * result + (country != null ? country.hashCode() : 0);
            result = 31 * result + (province != null ? province.hashCode() : 0);
            result = 31 * result + (city != null ? city.hashCode() : 0);
            result = 31 * result + gender;
            result = 31 * result + vipLevel;
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
