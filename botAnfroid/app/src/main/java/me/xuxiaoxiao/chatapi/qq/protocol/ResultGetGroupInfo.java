package me.xuxiaoxiao.chatapi.qq.protocol;

import me.xuxiaoxiao.chatapi.qq.entity.contact.QQGroup;
import me.xuxiaoxiao.chatapi.qq.entity.contact.QQUser;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultGetGroupInfo {
    public GInfo ginfo;
    public ArrayList<Stat> stats;
    public ArrayList<MInfo> minfo;
    public ArrayList<Card> cards;
    public ArrayList<VipInfo> vipinfo;

    public QQGroup convert() {
        QQGroup qqGroup = new QQGroup();
        qqGroup.id = this.ginfo.gid;
        qqGroup.name = this.ginfo.name;
        qqGroup.remark = this.ginfo.markname;
        qqGroup.code = this.ginfo.code;
        qqGroup.owner = this.ginfo.owner;
        qqGroup.notice = this.ginfo.memo;
        qqGroup.createtime = this.ginfo.createtime;
        qqGroup.members = new HashMap<>();
        for (ResultGetGroupInfo.GInfo.Member member : ginfo.members) {
            QQGroup.Member groupMember = new QQGroup.Member();
            groupMember.id = member.muin;
            qqGroup.members.put(groupMember.id, groupMember);
        }
        if (this.minfo != null) {
            for (ResultGetGroupInfo.MInfo mInfo : this.minfo) {
                QQGroup.Member groupMember = qqGroup.members.get(mInfo.uin);
                groupMember.name = mInfo.nick;
                groupMember.country = mInfo.country;
                groupMember.province = mInfo.province;
                groupMember.city = mInfo.city;
                if (mInfo.gender != null) {
                    switch (mInfo.gender) {
                        case "male":
                            groupMember.gender = QQUser.GENDER_MALE;
                            break;
                        case "female":
                            groupMember.gender = QQUser.GENDER_FEMALE;
                            break;
                        default:
                            groupMember.gender = QQUser.GENDER_UNKNOWN;
                            break;
                    }
                }
            }
        }
        if (this.cards != null) {
            for (ResultGetGroupInfo.Card card : this.cards) {
                qqGroup.members.get(card.muin).display = card.card;
            }
        }
        if (this.vipinfo != null) {
            for (ResultGetGroupInfo.VipInfo vipInfo : this.vipinfo) {
                qqGroup.members.get(vipInfo.u).vipLevel = vipInfo.vip_level;
            }
        }
        return qqGroup;
    }

    public static class Stat {
        public int client_type;
        public long uin;
        public int stat;
    }

    public static class MInfo {
        public long uin;
        public String nick;
        public String gender;
        public String country;
        public String province;
        public String city;
    }

    public static class GInfo {
        public int face;
        public String memo;
        public String fingermemo;
        public long code;
        public long createtime;
        public long flag;
        public int level;
        public String name;
        public String markname;
        public long gid;
        public long owner;
        public ArrayList<Member> members;
        public int option;

        public static class Member {
            public long muin;
            public int mflag;
        }
    }

    public static class Card {
        public long muin;
        public String card;
    }

    public static class VipInfo {
        public int vip_level;
        public long u;
        public int is_vip;
    }
}
