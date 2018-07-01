package me.xuxiaoxiao.chatapi.qq.protocol;

import me.xuxiaoxiao.chatapi.qq.entity.contact.QQGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultGetGroupNameListMask {
    public ArrayList<GMask> gmasklist;
    public ArrayList<GName> gnamelist;
    public ArrayList<GMark> gmarklist;

    public HashMap<Long, QQGroup> convert() {
        HashMap<Long, QQGroup> groups = new HashMap<>();
        if (this.gnamelist != null) {
            for (GName gName : this.gnamelist) {
                QQGroup qqGroup = new QQGroup();
                qqGroup.id = gName.gid;
                qqGroup.name = gName.name;
                qqGroup.code = gName.code;
                groups.put(qqGroup.id, qqGroup);
            }
        }
        if (this.gmarklist != null) {
            for (GMark gMark : this.gmarklist) {
                groups.get(gMark.uin).remark = gMark.markname;
            }
        }
        return groups;
    }

    public static class GName {
        public long gid;
        public long code;
        public String name;
        public int flag;
    }

    public static class GMask {
        public long gid;
        public int mask;
    }

    public static class GMark {
        public long uin;
        public String markname;
    }
}
