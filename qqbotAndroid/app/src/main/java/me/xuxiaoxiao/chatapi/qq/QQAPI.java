package me.xuxiaoxiao.chatapi.qq;

import com.google.gson.reflect.TypeToken;
import me.xuxiaoxiao.chatapi.qq.protocol.*;
import me.xuxiaoxiao.xtools.common.XTools;
import me.xuxiaoxiao.xtools.common.http.XOption;
import me.xuxiaoxiao.xtools.common.http.XRequest;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

/**
 * 网页版QQ接口
 */
final class QQAPI {
    String pt_login_sig;
    String qrsig;
    String vfwebqq;
    String qqStr;
    String psessionid;
    XOption httpOption = new XOption(60000, 90000) {
        @Override
        public CookieManager cookieManager() {
            return new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        }
    };

    /**
     * 获取登录所需的各参数。这些参数都在Cookie中返回。
     * 获取登录二维码和轮询登录状态时用到。
     */
    void xlogin() {
        XRequest request = XRequest.GET("https://xui.ptlogin2.qq.com/cgi-bin/xlogin");
        request.query("appid", 501004106);
        request.query("daid", 164);
        request.query("enable_qlogin", 0);
        request.query("f_url", "loginerroralert");
        request.query("login_state", 10);
        request.query("mibao_css", "m_webqq");
        request.query("no_verifyimg", 1);
        request.query("pt_disable_pwd", 1);
        request.query("s_url", "http://web2.qq.com/proxy.html");
        request.query("strong_login", 1);
        request.query("style", 40);
        request.query("t", 20131024001L);
        request.query("target", "self");
        request.header("Referer", "http://web2.qq.com/");
        XTools.http(httpOption, request);
    }

    /**
     * 获取登录二维码文件
     *
     * @param path 文件保存的路径
     * @return 获取到的登录二维码
     */
    File ptqrshow(String path) {
        XRequest request = XRequest.GET("https://ssl.ptlogin2.qq.com/ptqrshow");
        request.query("appid", 501004106);
        request.query("d", 72);
        request.query("daid", 164);
        request.query("e", 2);
        request.query("l", "M");
        request.query("pt_3rd_aid", 0);
        request.query("s", 3);
        request.query("t", Math.random());
        request.query("v", 4);
        request.header("Referer", "https://xui.ptlogin2.qq.com/cgi-bin/xlogin?daid=164&target=self&style=40&pt_disable_pwd=1&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http://web2.qq.com/proxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20131024001");
        return XTools.http(httpOption, request).file(path);
    }

    /**
     * 轮询获取登录状态，轮询间隔时间2秒，状态包括1：等待操作中（未扫描二维码和未确认登录），2：扫描二维码，3：确认登录，4：二维码过期
     *
     * @return 登录状态
     */
    RspQRLogin ptqrlogin() {
        XRequest request = XRequest.GET("https://ssl.ptlogin2.qq.com/ptqrlogin");
        request.query("action", String.format("0-0-%d", System.currentTimeMillis()));
        request.query("aid", 501004106);
        request.query("daid", 164);
        request.query("from_ui", 1);
        request.query("g", 1);
        request.query("h", 1);
        request.query("js_type", 1);
        request.query("js_ver", 10270);
        request.query("login_sig", pt_login_sig);
        request.query("mibao_css", "m_webqq");
        request.query("pt_uistyle", 40);
        request.query("ptlang", 2052);
        request.query("ptqrtoken", QQTools.hashQRSig(qrsig));
        request.query("ptredirect", 0);
        request.query("t", 1);
        request.query("u1", "http://web2.qq.com/proxy.html");
        request.header("Referer", "https://xui.ptlogin2.qq.com/cgi-bin/xlogin?daid=164&target=self&style=40&pt_disable_pwd=1&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fweb2.qq.com%2Fproxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20131024001");
        return new RspQRLogin(XTools.http(httpOption, request).string());
    }

    /**
     * 授权登录网页qq后，在返回的信息里包含了一个uri，通过访问这个uri可以获得登录相关的cookie
     *
     * @param uri 授权登录返回信息里的uri
     */
    void check_sig(String uri) {
        XRequest request = XRequest.GET(uri);
        XTools.http(httpOption, request);
    }

    /**
     * 获取重要参数vfwebqq
     */
    void getvfwebqq() {
        XRequest request = XRequest.GET("http://s.web2.qq.com/api/getvfwebqq");
        request.query("clientid", 53999199);
        request.query("psessionid", "");
        request.query("ptwebqq", "");
        request.query("t", System.currentTimeMillis());
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        BaseRsp<ResultVFWebqq> resultVFWebqq = QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultVFWebqq>>() {
        }.getType());
        this.vfwebqq = resultVFWebqq.result.vfwebqq;
    }

    /**
     * 登录，返回qq号和重要参数psessionid
     *
     * @return 登录返回信息
     */
    BaseRsp<ResultLogin> login2() {
        XRequest request = XRequest.POST("http://d1.web2.qq.com/channel/login2");
        request.header("Origin", "http://d1.web2.qq.com");
        request.header("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        request.content("r", QQTools.GSON.toJson(new BaseReq(null, "", vfwebqq, "")));
        BaseRsp<ResultLogin> resultLogin = QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultLogin>>() {
        }.getType());
        this.qqStr = String.valueOf(resultLogin.result.uin);
        this.psessionid = resultLogin.result.psessionid;
        return resultLogin;
    }

    /**
     * 获取自身信息
     *
     * @return 获取自身信息返回信息
     */
    BaseRsp<ResultUser> get_self_info2() {
        XRequest request = XRequest.GET("http://s.web2.qq.com/api/get_self_info2");
        request.query("t", System.currentTimeMillis());
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultUser>>() {
        }.getType());
    }

    /**
     * 获取用户好友列表
     *
     * @return 获取用户好友列表返回信息
     */
    BaseRsp<ResultGetUserFriends> get_user_friends2() {
        XRequest request = XRequest.POST("http://s.web2.qq.com/api/get_user_friends2");
        request.header("Origin", "http://s.web2.qq.com");
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        request.content("r", QQTools.GSON.toJson(new BaseReq(QQTools.hash(qqStr, ""), null, vfwebqq, null)));
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultGetUserFriends>>() {
        }.getType());
    }

    /**
     * 获取用户群列表
     *
     * @return 获取用户群列表返回信息
     */
    BaseRsp<ResultGetGroupNameListMask> get_group_name_list_mask2() {
        XRequest request = XRequest.POST("http://s.web2.qq.com/api/get_group_name_list_mask2");
        request.header("Origin", "http://s.web2.qq.com");
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        request.content("r", QQTools.GSON.toJson(new BaseReq(QQTools.hash(qqStr, ""), null, vfwebqq, null)));
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultGetGroupNameListMask>>() {
        }.getType());
    }

    /**
     * 获取用户讨论组列表
     *
     * @return 获取用户讨论组列表返回信息
     */
    BaseRsp<ResultGetDiscusList> get_discus_list() {
        XRequest request = XRequest.GET("http://s.web2.qq.com/api/get_discus_list");
        request.query("clientid", 53999199);
        request.query("psessionid", psessionid);
        request.query("t", System.currentTimeMillis());
        request.query("vfwebqq", vfwebqq);
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultGetDiscusList>>() {
        }.getType());
    }

    /**
     * 获取最近联系人列表
     *
     * @return 获取最近联系人列表返回信息
     */
    BaseRsp<ResultGetRecentList> get_recent_list2() {
        XRequest request = XRequest.POST("http://d1.web2.qq.com/channel/get_recent_list2");
        request.header("Origin", "http://d1.web2.qq.com");
        request.header("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        request.content("r", QQTools.GSON.toJson(new BaseReq(null, null, vfwebqq, psessionid)));

        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultGetRecentList>>() {
        }.getType());
    }

    /**
     * 获取在线好友列表
     *
     * @return 获取在线好友列表返回信息
     */
    BaseRsp<ResultGetOnlineBuddies> get_online_buddies2() {
        XRequest request = XRequest.GET("http://d1.web2.qq.com/channel/get_online_buddies2");
        request.query("clientid", 53999199);
        request.query("psessionid", psessionid);
        request.query("t", System.currentTimeMillis());
        request.query("vfwebqq", vfwebqq);
        request.header("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultGetOnlineBuddies>>() {
        }.getType());
    }

    /**
     * 监听消息，服务端会保持一分钟，期间有新的消息时，会立刻返回
     *
     * @return 监听消息返回信息
     */
    BaseRsp<ResultPoll> poll2() {
        XRequest request = XRequest.POST("http://d1.web2.qq.com/channel/poll2");
        request.header("Origin", "http://d1.web2.qq.com");
        request.header("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        request.content("r", QQTools.GSON.toJson(new BaseReq(null, "", null, psessionid)));
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultPoll>>() {
        }.getType());
    }

    /**
     * 获取单个用户信息
     *
     * @param friendId 好友id
     * @return 用户信息
     */
    BaseRsp<ResultUser> get_friend_info2(long friendId) {
        XRequest request = XRequest.GET("http://s.web2.qq.com/api/get_friend_info2");
        request.query("clientid", 53999199);
        request.query("psessionid", psessionid);
        request.query("t", System.currentTimeMillis());
        request.query("tuin", friendId);
        request.query("vfwebqq", vfwebqq);
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultUser>>() {
        }.getType());
    }

    /**
     * 获取用户个性签名
     *
     * @param friendId 好友id
     * @return 用户个性签名
     */
    BaseRsp<ArrayList<ResultLongNick>> get_single_long_nick2(long friendId) {
        XRequest request = XRequest.GET("http://s.web2.qq.com/api/get_single_long_nick2");
        request.query("t", System.currentTimeMillis());
        request.query("tuin", friendId);
        request.query("vfwebqq", vfwebqq);
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ArrayList<ResultLongNick>>>() {
        }.getType());
    }

    /**
     * 获取群信息
     *
     * @param groupCode 群code
     * @return 群信息
     */
    BaseRsp<ResultGetGroupInfo> get_group_info_ext2(long groupCode) {
        XRequest request = XRequest.GET("http://s.web2.qq.com/api/get_group_info_ext2");
        request.query("gcode", groupCode);
        request.query("t", System.currentTimeMillis());
        request.query("vfwebqq", this.vfwebqq);
        request.header("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultGetGroupInfo>>() {
        }.getType());
    }

    /**
     * 获取讨论组信息
     *
     * @param discussId 讨论组id
     * @return 讨论组信息
     */
    BaseRsp<ResultGetDiscuInfo> get_discu_info(long discussId) {
        XRequest request = XRequest.GET("http://d1.web2.qq.com/channel/get_discu_info");
        request.query("clientid", 53999199);
        request.query("did", discussId);
        request.query("psessionid", psessionid);
        request.query("t", System.currentTimeMillis());
        request.query("vfwebqq", vfwebqq);
        request.header("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), new TypeToken<BaseRsp<ResultGetDiscuInfo>>() {
        }.getType());
    }

    /**
     * 发送好友消息
     *
     * @param friendId 好友id
     * @param content  要发送的消息
     * @return 发送好友消息返回信息
     */
    BaseRsp send_buddy_msg2(long friendId, String content) {
        XRequest request = XRequest.POST("http://d1.web2.qq.com/channel/send_buddy_msg2");
        request.header("Origin", "http://d1.web2.qq.com");
        request.header("Referer", "http://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1");
        request.content("r", QQTools.GSON.toJson(new ReqSendFriendMsg(friendId, content, psessionid)));
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), BaseRsp.class);
    }

    /**
     * 发送群消息
     *
     * @param groupId 群id
     * @param content 要发送的信息
     * @return 发送群消息返回信息
     */
    BaseRsp send_qun_msg2(long groupId, String content) {
        XRequest request = XRequest.POST("http://d1.web2.qq.com/channel/send_qun_msg2");
        request.header("Origin", "http://d1.web2.qq.com");
        request.header("Referer", "http://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1");
        request.content("r", QQTools.GSON.toJson(new ReqSendGroupMsg(groupId, content, psessionid)));
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), BaseRsp.class);
    }

    /**
     * 发送讨论组消息
     *
     * @param discussId 讨论组id
     * @param content   要发送的消息
     * @return 发送讨论组消息返回信息
     */
    BaseRsp send_discu_msg2(long discussId, String content) {
        XRequest request = XRequest.POST("http://d1.web2.qq.com/channel/send_discu_msg2");
        request.header("Referer", "http://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1");
        request.content("r", QQTools.GSON.toJson(new ReqSendDiscussMsg(discussId, content, psessionid)));
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), BaseRsp.class);
    }

    /**
     * 更改登录状态
     *
     * @param status 状态字符串
     * @return 更改登录状态返回信息
     */
    BaseRsp change_status2(String status) {
        XRequest request = XRequest.GET("http://d1.web2.qq.com/channel/change_status2");
        request.query("clientid", 53999199);
        request.query("newstatus", status);
        request.query("psessionid", psessionid);
        request.query("t", System.currentTimeMillis());
        request.header("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
        return QQTools.GSON.fromJson(XTools.http(httpOption, request).string(), BaseRsp.class);
    }
}