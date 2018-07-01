package me.xuxiaoxiao.chatapi.qq.protocol;

import me.xuxiaoxiao.chatapi.qq.entity.contact.QQUser;

public class ResultUser {
    public Birthday birthday;
    public int face;
    public String phone;
    public String occupation;
    public int allow;
    public String college;
    public long uin;
    public int blood;
    public int constel;
    public String lnick;
    public String vfwebqq;
    public String homepage;
    public int vip_info;
    public String city;
    public String country;
    public String personal;
    public int shengxiao;
    public String nick;
    public String email;
    public String province;
    public long account;
    public String gender;
    public String mobile;

    public QQUser convert() {
        QQUser qqUser = new QQUser();
        qqUser.id = this.uin;
        qqUser.name = this.nick;
        qqUser.signature = this.lnick;
        qqUser.country = this.country;
        qqUser.province = this.province;
        qqUser.city = this.city;
        if (this.gender != null) {
            switch (this.gender) {
                case "male":
                    qqUser.gender = QQUser.GENDER_MALE;
                    break;
                case "female":
                    qqUser.gender = QQUser.GENDER_FEMALE;
                    break;
                default:
                    qqUser.gender = QQUser.GENDER_UNKNOWN;
                    break;
            }
        }
        if (this.birthday != null) {
            qqUser.birthday = String.format("%d-%d-%d", this.birthday.year, this.birthday.month, this.birthday.day);
        }
        qqUser.vipLevel = this.vip_info;
        qqUser.isDetail = true;
        return qqUser;
    }

    public static class Birthday {
        public int year;
        public int month;
        public int day;
    }
}
