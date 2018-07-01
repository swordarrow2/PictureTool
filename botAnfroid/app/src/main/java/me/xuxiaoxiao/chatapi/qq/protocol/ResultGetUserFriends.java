package me.xuxiaoxiao.chatapi.qq.protocol;

import me.xuxiaoxiao.chatapi.qq.entity.contact.QQCategory;
import me.xuxiaoxiao.chatapi.qq.entity.contact.QQUser;

import java.util.ArrayList;

public class ResultGetUserFriends {
    public ArrayList<Friend> friends;
    public ArrayList<MarkName> marknames;
    public ArrayList<Category> categories;
    public ArrayList<VipInfo> vipinfo;
    public ArrayList<Info> info;

    public ArrayList<QQCategory> convert() {
        ArrayList<QQCategory> categories = new ArrayList<>();
        if (this.categories == null || this.categories.size() == 0) {
            this.categories = new ArrayList<>();
            Category category = new Category();
            category.index = 0;
            category.sort = 0;
            category.name = "我的好友";
            this.categories.add(category);
        }
        for (ResultGetUserFriends.Category category : this.categories) {
            QQCategory qqCategory = new QQCategory();
            qqCategory.index = category.index;
            qqCategory.name = category.name;
            if (category.sort >= categories.size()) {
                categories.add(qqCategory);
            } else {
                categories.add(category.sort, qqCategory);
            }
        }
        for (ResultGetUserFriends.Friend friend : this.friends) {
            QQUser qqUser = new QQUser();
            qqUser.id = friend.uin;
            if (this.marknames != null) {
                for (ResultGetUserFriends.MarkName markName : this.marknames) {
                    if (qqUser.id == markName.uin) {
                        qqUser.remark = markName.markName;
                    }
                }
            }
            if (this.vipinfo != null) {
                for (ResultGetUserFriends.VipInfo vipInfo : this.vipinfo) {
                    if (qqUser.id == vipInfo.u) {
                        qqUser.vipLevel = vipInfo.vip_level;
                    }
                }
            }
            if (this.info != null) {
                for (ResultGetUserFriends.Info info : this.info) {
                    if (qqUser.id == info.uin) {
                        qqUser.name = info.nick;
                    }
                }
            }
            for (QQCategory qqCategory : categories) {
                if (qqCategory.index == friend.categories) {
                    qqCategory.friends.put(qqUser.id, qqUser);
                    qqUser = null;
                    break;
                }
            }
            if (qqUser != null) {
                categories.get(0).friends.put(qqUser.id, qqUser);
            }
        }
        return categories;
    }

    public static class Category {
        public int index;
        public int sort;
        public String name;
    }

    public static class Friend {
        public int flag;
        public long uin;
        public int categories;
    }

    public static class MarkName {
        public long uin;
        public String markName;
        public int type;
    }

    public static class VipInfo {
        public long u;
        public int is_vip;
        public int vip_level;
    }

    public static class Info {
        public int face;
        public long flag;
        public String nick;
        public long uin;
    }
}
