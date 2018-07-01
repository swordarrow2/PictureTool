package me.xuxiaoxiao.chatapi.qq.protocol;

import me.xuxiaoxiao.chatapi.qq.entity.contact.QQDiscuss;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultGetDiscusList {
    public ArrayList<DName> dnamelist;

    public HashMap<Long, QQDiscuss> convert() {
        HashMap<Long, QQDiscuss> discusses = new HashMap<>();
        if (this.dnamelist != null) {
            for (DName dName : this.dnamelist) {
                QQDiscuss qqDiscuss = new QQDiscuss();
                qqDiscuss.id = dName.did;
                qqDiscuss.name = dName.name;
                discusses.put(qqDiscuss.id, qqDiscuss);
            }
        }
        return discusses;
    }

    public static class DName {
        public long did;
        public String name;
    }
}
