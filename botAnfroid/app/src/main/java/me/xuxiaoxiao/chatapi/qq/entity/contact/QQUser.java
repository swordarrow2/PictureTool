package me.xuxiaoxiao.chatapi.qq.entity.contact;

import java.io.Serializable;

/**
 * QQ用户
 */
public class QQUser extends QQContact implements Serializable, Cloneable {
    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    /**
     * 用户的备注名
     */
    public String remark;
    /**
     * 用户个性签名
     */
    public String signature;
    /**
     * 用户所在国家
     */
    public String country;
    /**
     * 用户所在省份
     */
    public String province;
    /**
     * 用户所在城市
     */
    public String city;
    /**
     * 用户性别
     */
    public int gender;
    /**
     * 用户生日
     */
    public String birthday;
    /**
     * 用户在线状态
     */
    public String status;
    /**
     * 用户超级会员等级
     */
    public int vipLevel;
    /**
     * 是否是详细信息，如果不是详细信息，则说明是获取好友列表时的数据。
     * 接收到这个用户发来的消息后会再次获取该用户详细数据
     */
    public boolean isDetail;

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

        QQUser qqUser = (QQUser) o;

        if (gender != qqUser.gender) {
            return false;
        }
        if (vipLevel != qqUser.vipLevel) {
            return false;
        }
        if (isDetail != qqUser.isDetail) {
            return false;
        }
        if (remark != null ? !remark.equals(qqUser.remark) : qqUser.remark != null) {
            return false;
        }
        if (signature != null ? !signature.equals(qqUser.signature) : qqUser.signature != null) {
            return false;
        }
        if (country != null ? !country.equals(qqUser.country) : qqUser.country != null) {
            return false;
        }
        if (province != null ? !province.equals(qqUser.province) : qqUser.province != null) {
            return false;
        }
        if (city != null ? !city.equals(qqUser.city) : qqUser.city != null) {
            return false;
        }
        if (birthday != null ? !birthday.equals(qqUser.birthday) : qqUser.birthday != null) {
            return false;
        }
        return status != null ? status.equals(qqUser.status) : qqUser.status == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + gender;
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + vipLevel;
        result = 31 * result + (isDetail ? 1 : 0);
        return result;
    }

    @Override
    public QQUser clone() {
        return (QQUser) super.clone();
    }
}
