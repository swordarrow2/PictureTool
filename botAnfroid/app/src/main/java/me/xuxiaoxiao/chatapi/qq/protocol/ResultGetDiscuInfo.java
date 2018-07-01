package me.xuxiaoxiao.chatapi.qq.protocol;

import me.xuxiaoxiao.chatapi.qq.entity.contact.QQDiscuss;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultGetDiscuInfo {
    public Info info;
    public ArrayList<MemberInfo> mem_info;
    public ArrayList<MemberStatus> mem_status;

    public QQDiscuss convert() {
        QQDiscuss qqDiscuss = new QQDiscuss();
        qqDiscuss.id = this.info.did;
        qqDiscuss.name = this.info.discu_name;
        qqDiscuss.members = new HashMap<>();
        for (ResultGetDiscuInfo.Info.Member member : this.info.mem_list) {
            QQDiscuss.Member discussMember = new QQDiscuss.Member();
            discussMember.id = member.mem_uin;
            qqDiscuss.members.put(discussMember.id, discussMember);
        }
        for (ResultGetDiscuInfo.MemberInfo memberInfo : this.mem_info) {
            qqDiscuss.members.get(memberInfo.uin).name = memberInfo.nick;
        }
        return qqDiscuss;
    }

    public static class Info {
        public long did;
        public String discu_name;
        public ArrayList<Member> mem_list;

        public static class Member {
            public long mem_uin;
            public long ruin;
        }
    }

    public static class MemberInfo {
        public long uin;
        public String nick;
    }

    public static class MemberStatus {
        public long uin;
        public String status;
        public int client_type;
    }
}
