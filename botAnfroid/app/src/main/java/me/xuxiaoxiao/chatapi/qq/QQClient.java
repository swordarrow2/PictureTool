package me.xuxiaoxiao.chatapi.qq;

import me.xuxiaoxiao.chatapi.qq.entity.contact.*;
import me.xuxiaoxiao.chatapi.qq.entity.message.QQMessage;
import me.xuxiaoxiao.chatapi.qq.entity.message.QQText;
import me.xuxiaoxiao.chatapi.qq.entity.message.QQUnknown;
import me.xuxiaoxiao.chatapi.qq.protocol.*;
import me.xuxiaoxiao.xtools.common.XTools;

import java.io.File;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import android.os.*;

/**
 * QQ客户端
 */
public class QQClient {
    public static final String LOGIN_EXCEPTION = "登陆异常";
    public static final String INIT_EXCEPTION = "初始化异常";
    public static final String LISTEN_EXCEPTION = "监听异常";

    public static final String STATUS_AWAY = "away";
    public static final String STATUS_ONLINE = "online";
    public static final String STATUS_CALLME = "callme";
    public static final String STATUS_BUSY = "busy";
    public static final String STATUS_SILENT = "silent";
    public static final String STATUS_HIDDEN = "hidden";
    public static final String STATUS_OFFLINE = "offline";

    private final QQAPI qqAPI = new QQAPI();
    private final QQThread qqThread = new QQThread();
    private final QQContacts qqContacts = new QQContacts();
    private final File folder;
    private final QQChatListener qqChatListener;
    private String checkSig;

    public QQClient(QQChatListener qqChatListener) {
        this(qqChatListener, null, null);
    }

    public QQClient(QQChatListener qqChatListener, File folder, Handler handler) {
        Objects.requireNonNull(qqChatListener);
        if (folder == null) {
            folder = new File(Environment.getExternalStorageDirectory().getPath());
        }
        if (handler == null) {
            handler = new ConsoleHandler();
            handler.setLevel(Level.FINER);
        }
        this.qqChatListener = qqChatListener;
        this.folder = folder;
        QQTools.LOGGER.setLevel(handler.getLevel());
        QQTools.LOGGER.setUseParentHandlers(false);
        QQTools.LOGGER.addHandler(handler);
    }

    /**
     * 启动QQ客户端，一个客户端实例只能被启动一次
     */
    public void startup() {
        qqThread.start();
    }

    /**
     * QQ客户端是否在运行中
     *
     * @return QQ客户端是否在运行中
     */
    public boolean isWorking() {
        return !qqThread.isInterrupted();
    }

    /**
     * 关闭QQ客户端，关闭后的客户端不能被再次打开
     */
    public void shutdown() {
        qqThread.interrupt();
    }

    /**
     * 获取我的信息
     *
     * @return 我的信息
     */
    public QQUser userMe() {
        return this.qqContacts.me;
    }

    /**
     * 根据好友id获取好友
     *
     * @param userId 好友id
     * @return 好友信息
     */
    public QQUser userFriend(Long userId) {
        return this.qqContacts.friends.get(userId);
    }

    /**
     * 获取所有好友
     *
     * @return 所有好友信息
     */
    public HashMap<Long, QQUser> userFriends() {
        return this.qqContacts.friends;
    }

    /**
     * 根据群id获取群
     *
     * @param groupId 群id
     * @return 群信息
     */
    public QQGroup userGroup(Long groupId) {
        return this.qqContacts.groups.get(groupId);
    }

    /**
     * 获取所有群
     *
     * @return 所有群信息
     */
    public HashMap<Long, QQGroup> userGroups() {
        return this.qqContacts.groups;
    }

    /**
     * 根据讨论组id获取讨论组
     *
     * @param discussId 讨论组id
     * @return 讨论组信息
     */
    public QQDiscuss userDiscuss(Long discussId) {
        return this.qqContacts.discusses.get(discussId);
    }

    /**
     * 获取所有讨论组
     *
     * @return 所有讨论组id
     */
    public HashMap<Long, QQDiscuss> userDiscuss() {
        return this.qqContacts.discusses;
    }

    /**
     * 获取好友分组和好友
     *
     * @return 好友分组信息
     */
    public ArrayList<QQCategory> userCategories() {
        return this.qqContacts.categories;
    }

    /**
     * 发送文字消息
     *
     * @param contact 要发送的对象，可以是好友，群，讨论组
     * @param content 发送的内容
     */
    public void sendText(QQContact contact, String content) {
        if (contact instanceof QQUser) {
            QQTools.LOGGER.fine(String.format("向好友【%s】发送消息：%s", contact.name, content));
            qqAPI.send_buddy_msg2(contact.id, content);
        } else if (contact instanceof QQGroup) {
            QQTools.LOGGER.fine(String.format("向群【%s】发送消息：%s", contact.name, content));
            qqAPI.send_qun_msg2(contact.id, content);
        } else if (contact instanceof QQDiscuss) {
            QQTools.LOGGER.fine(String.format("向讨论组【%s】发送消息：%s", contact.name, content));
            qqAPI.send_discu_msg2(contact.id, content);
        } else {
            QQTools.LOGGER.fine(String.format("消息发送失败：联系人【%s】的类型未知", contact.name));
        }
    }

    /**
     * 改变自己的登录状态
     *
     * @param status 要改变的登录状态
     */
    public void changeStatus(String status) {
        QQTools.LOGGER.fine(String.format("改变我的在线状态为【%s】", status));
        qqAPI.change_status2(status);
    }

    public abstract static class QQChatListener {

        /**
         * 获取到登录二维码
         *
         * @param qrCode 登录二维码文件
         */
        public abstract void onQRCode(File qrCode);

        /**
         * 获取到登录头像
         *
         * @param base64Avatar Base64编码的登录头像
         */
        public void onAvatar(String base64Avatar) {
        }

        /**
         * 登录成功
         */
        public void onLogin() {
        }

        /**
         * 监听到消息
         *
         * @param qqMessage 监听到的qq消息
         */
        public void onMessage(QQMessage qqMessage) {
        }

        /**
         * 退出登录
         */
        public void onLogout() {
        }

        /**
         * 发生错误
         *
         * @param reason 错误原因
         */
        public void onException(String reason) {
        }
    }

    private class QQThread extends Thread {

        @Override
        public void run() {
            int loginCount = 0;
            while (!isInterrupted()) {
                //用户登录
                QQTools.LOGGER.finer(String.format("正在进行第%d次登录", loginCount));
                String loginErr = login();
                if (!XTools.strEmpty(loginErr)) {
                    qqChatListener.onException(loginErr);
                    return;
                }
                //用户初始化
                QQTools.LOGGER.finer("正在初始化");
                String initErr = initial();
                if (!XTools.strEmpty(initErr)) {
                    qqChatListener.onException(initErr);
                    return;
                }
                qqChatListener.onLogin();
                //同步消息
                QQTools.LOGGER.finer("正在监听消息");
                String listenErr = listen();
                if (!XTools.strEmpty(listenErr)) {
                    if (loginCount++ > 70) {
                        qqChatListener.onException(listenErr);
                        return;
                    } else {
                        continue;
                    }
                }
                //退出登录
                QQTools.LOGGER.finer("正在退出登录");
                qqChatListener.onLogout();
                return;
            }
        }

        private String login() {
            try {
                //获取pt_login_sig
                qqAPI.xlogin();
                for (HttpCookie cookie : qqAPI.httpOption.cookieManager.getCookieStore().getCookies()) {
                    if ("pt_login_sig".equals(cookie.getName())) {
                        qqAPI.pt_login_sig = cookie.getValue();
                    }
                }
                if (XTools.strEmpty(checkSig)) {
                    //首次登录，获取登录二维码文件和qrsig
                    qqChatListener.onQRCode(qqAPI.ptqrshow(String.format("%s%sqrcode-%d-%d.jpg", folder.getAbsolutePath(), File.separator, System.currentTimeMillis(), (int) (Math.random() * 1000))));
                    for (HttpCookie cookie : qqAPI.httpOption.cookieManager.getCookieStore().getCookies()) {
                        if ("qrsig".equals(cookie.getName())) {
                            qqAPI.qrsig = cookie.getValue();
                            break;
                        }
                    }
                    //每隔两秒获取一次登录状态
                    while (true) {
                        Thread.sleep(2000);
                        RspQRLogin rspQRLogin = qqAPI.ptqrlogin();
                        switch (rspQRLogin.code) {
                            case 0:
                                QQTools.LOGGER.finer("已授权登录");
                                checkSig = rspQRLogin.uri;
                                qqAPI.check_sig(rspQRLogin.uri);
                                return null;
                            case 66:
                                QQTools.LOGGER.finer("等待操作中");
                                break;
                            case 67:
                                QQTools.LOGGER.finer("等待授权登录");
                                break;
                            default:
                                QQTools.LOGGER.finer("二维码已失效");
                                return LOGIN_EXCEPTION;
                        }
                    }
                } else {
                    //已经登录过，可直接进入初始化流程
                    qqAPI.check_sig(checkSig);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                QQTools.LOGGER.severe(String.format("登录异常：%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                return LOGIN_EXCEPTION;
            }
        }

        private String initial() {
            try {
                //登录初始化
                qqAPI.getvfwebqq();
                qqAPI.login2();
                //获取自身信息
                qqContacts.me = qqAPI.get_self_info2().result.convert();
                QQTools.LOGGER.finer(String.format("获取到自身信息：%s", QQTools.GSON.toJson(qqContacts.me)));
                //获取好友列表
                qqContacts.categories = qqAPI.get_user_friends2().result.convert();
                for (QQCategory qqCategory : qqContacts.categories) {
                    qqContacts.friends.putAll(qqCategory.friends);
                }
                QQTools.LOGGER.finer(String.format("获取到好友 %d 个", qqContacts.friends.size()));
                //获取群列表
                qqContacts.groups.putAll(qqAPI.get_group_name_list_mask2().result.convert());
                QQTools.LOGGER.finer(String.format("获取到群 %d 个", qqContacts.groups.size()));
                //获取和整理讨论组列表
                qqContacts.discusses.putAll(qqAPI.get_discus_list().result.convert());
                QQTools.LOGGER.finer(String.format("获取到讨论组 %d 个", qqContacts.discusses.size()));
                //获取在线的好友信息
                ResultGetOnlineBuddies resultOnline = qqAPI.get_online_buddies2().result;
                if (resultOnline != null) {
                    for (ResultGetOnlineBuddies.OnlineBuddy onlineBuddy : resultOnline) {
                        if (qqContacts.friends.containsKey(onlineBuddy.uin)) {
                            qqContacts.friends.get(onlineBuddy.uin).status = onlineBuddy.status;
                        }
                    }
                }
                //获取最近联系人
                qqContacts.recent = new ArrayList<>();
                ResultGetRecentList resultRecent = qqAPI.get_recent_list2().result;
                if (resultRecent != null) {
                    for (ResultGetRecentList.Recent recent : resultRecent) {
                        if (recent.type == 0) {
                            qqContacts.recent.add(qqContacts.friends.get(recent.uin));
                        } else if (recent.type == 1) {
                            qqContacts.recent.add(qqContacts.groups.get(recent.uin));
                        } else if (recent.type == 2) {
                            qqContacts.recent.add(qqContacts.discusses.get(recent.uin));
                        }
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                QQTools.LOGGER.severe(String.format("初始化异常：%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                return INIT_EXCEPTION;
            }
        }

        private String listen() {
            try {
                //上次空轮询时间，7秒内空轮询则判定为快速空轮询
                long lastEmpty = 0;
                //快速空轮询次数，超过7次则判定当前连接失效，重新登录
                int emptyCount = 0;
                //单次请求重试次数，超过5次则判定当前连接失效，重新登录
                int retryCount = 0;
                while (!isInterrupted()) {
                    try {
                        BaseRsp<ResultPoll> rspPoll = qqAPI.poll2();
                        if (rspPoll.result != null) {
                            for (ResultPoll.Item item : rspPoll.result) {
                                QQMessage qqMessage;
                                try {
                                    switch (item.poll_type) {
                                        case "message": {
                                            QQUser qqUser = item.value.from_uin == qqContacts.me.id ? qqContacts.me : qqContacts.friends.get(item.value.from_uin);
                                            if (qqUser == null || !qqUser.isDetail) {
                                                ResultUser resultUser = qqAPI.get_friend_info2(item.value.from_uin).result;
                                                if (resultUser != null) {
                                                    qqUser = resultUser.convert();
                                                    ArrayList<ResultLongNick> signatures = qqAPI.get_single_long_nick2(qqUser.id).result;
                                                    if (signatures != null && signatures.size() > 0) {
                                                        qqUser.signature = signatures.get(0).lnick;
                                                    }
                                                    qqContacts.friends.put(qqUser.id, qqUser);
                                                }
                                            }
                                            qqMessage = parseCommon(item.value, new QQText());
                                            break;
                                        }
                                        case "group_message": {
                                            QQGroup qqGroup = qqContacts.groups.get(item.value.group_code);
                                            if (qqGroup == null) {
                                                //如果被拉进群的话，群消息没有携带群code，所以无法获取群信息，需要重新初始化才行
                                                QQTools.LOGGER.warning("检测到未知的群，需要重新初始化");
                                                return LISTEN_EXCEPTION;
                                            } else if (qqGroup.members == null || !qqGroup.members.containsKey(item.value.send_uin)) {
                                                qqGroup = qqAPI.get_group_info_ext2(qqGroup.code).result.convert();
                                                qqContacts.groups.put(qqGroup.id, qqGroup);
                                            }
                                            qqMessage = parseCommon(item.value, new QQText());
                                            break;
                                        }
                                        case "discu_message": {
                                            QQDiscuss qqDiscuss = qqContacts.discusses.get(item.value.did);
                                            if (qqDiscuss == null || qqDiscuss.members == null || !qqDiscuss.members.containsKey(item.value.send_uin)) {
                                                ResultGetDiscuInfo resultGetDiscuInfo = qqAPI.get_discu_info(item.value.did).result;
                                                if (resultGetDiscuInfo != null) {
                                                    qqDiscuss = resultGetDiscuInfo.convert();
                                                    qqContacts.discusses.put(qqDiscuss.id, qqDiscuss);
                                                }
                                            }
                                            qqMessage = parseCommon(item.value, new QQText());
                                            break;
                                        }
                                        default: {
                                            qqMessage = parseCommon(item.value, new QQUnknown());
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    QQTools.LOGGER.warning(String.format("解析消息出错：%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                                    qqMessage = parseCommon(item.value, new QQUnknown());
                                }
                                if (qqMessage instanceof QQText) {
                                    if (qqMessage.fromGroup != null) {
                                        if (qqMessage.fromGroupMember.id == qqContacts.me.id) {
                                            QQTools.LOGGER.fine(String.format("我在群【%s】里说：%s", qqMessage.fromGroup.name, qqMessage.content));
                                        } else {
                                            QQTools.LOGGER.fine(String.format("群【%s】里的【%s】说：%s", qqMessage.fromGroup.name, qqMessage.fromGroupMember.name, qqMessage.content));
                                        }
                                    }
                                    if (qqMessage.fromDiscuss != null) {
                                        if (qqMessage.fromDiscussMember.id == qqContacts.me.id) {
                                            QQTools.LOGGER.fine(String.format("我在讨论组【%s】里说：%s", qqMessage.fromDiscuss.name, qqMessage.content));
                                        } else {
                                            QQTools.LOGGER.fine(String.format("讨论组【%s】里的【%s】说：%s", qqMessage.fromDiscuss.name, qqMessage.fromDiscussMember.name, qqMessage.content));
                                        }
                                    }
                                    if (qqMessage.fromUser != null) {
                                        if (qqMessage.fromUser.id == qqContacts.me.id) {
                                            QQTools.LOGGER.fine(String.format("我对好友【%s】说：%s", qqMessage.toUser.name, qqMessage.content));
                                        } else {
                                            QQTools.LOGGER.fine(String.format("好友【%s】对我说：%s", qqMessage.fromUser.name, qqMessage.content));
                                        }
                                    }
                                    qqChatListener.onMessage(qqMessage);
                                } else if (qqMessage != null) {
                                    QQTools.LOGGER.fine("收到了未知类型的消息");
                                    qqChatListener.onMessage(qqMessage);
                                }
                            }
                            emptyCount = 0;
                        } else {
                            if (System.currentTimeMillis() - lastEmpty < 7000) {
                                emptyCount++;
                            }
                            if (emptyCount > 7) {
                                QQTools.LOGGER.severe("连接已失效：快速空轮询过多");
                                return LISTEN_EXCEPTION;
                            } else {
                                QQTools.LOGGER.finer("暂无信息");
                                lastEmpty = System.currentTimeMillis();
                            }
                        }
                        retryCount = 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (retryCount++ < 5) {
                            QQTools.LOGGER.warning(String.format("监听或解析失败，重试第%d次", retryCount));
                        } else {
                            QQTools.LOGGER.severe(String.format("监听或解析失败：%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                            QQTools.LOGGER.severe("连接已失效：监听或解析重试次数过多");
                            return LISTEN_EXCEPTION;
                        }
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                QQTools.LOGGER.severe(String.format("监听异常：%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
                return LISTEN_EXCEPTION;
            }
        }

        private <T extends QQMessage> T parseCommon(ResultPoll.Item.Message message, T t) {
            t.id = message.msg_id;
            if (message.group_code > 0) {
                t.fromGroup = qqContacts.groups.get(message.group_code);
                t.fromGroupMember = t.fromGroup != null ? (t.fromGroup.members != null ? t.fromGroup.members.get(message.send_uin) : null) : null;
            }
            if (message.did > 0) {
                t.fromDiscuss = qqContacts.discusses.get(message.did);
                t.fromDiscussMember = t.fromDiscuss != null ? (t.fromDiscuss.members != null ? t.fromDiscuss.members.get(message.send_uin) : null) : null;
            }
            if (t.fromGroup == null && t.fromDiscuss == null) {
                t.fromUser = message.send_uin == qqContacts.me.id ? qqContacts.me : qqContacts.friends.get(message.from_uin);
            }
            t.toUser = message.to_uin == qqContacts.me.id ? qqContacts.me : qqContacts.friends.get(message.to_uin);
            t.timestamp = message.time * 1000;
            StringBuilder sbContent = new StringBuilder();
            for (int i = 1; i < message.content.size(); i++) {
                if (sbContent.length() > 0) {
                    sbContent.append(" ");
                }
                sbContent.append(message.content.get(i));
            }
            t.content = sbContent.toString();
            return t;
        }
    }
}
