package me.xuxiaoxiao.chatapi.qq;

import me.xuxiaoxiao.chatapi.qq.entity.contact.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * QQ联系人
 */
final class QQContacts {
    final HashMap<Long, QQUser> friends = new HashMap<>();
    final HashMap<Long, QQGroup> groups = new HashMap<>();
    final HashMap<Long, QQDiscuss> discusses = new HashMap<>();

    ArrayList<QQCategory> categories;
    ArrayList<QQContact> recent;
    QQUser me;
}
