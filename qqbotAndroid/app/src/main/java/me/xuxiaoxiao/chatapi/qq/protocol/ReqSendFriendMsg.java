package me.xuxiaoxiao.chatapi.qq.protocol;

import com.google.gson.Gson;

public class ReqSendFriendMsg {
    public long to;
    public String content;
    public int face;
    public long clientid = 53999199;
    public long msg_id;
    public String psessionid;

    public ReqSendFriendMsg(long friend, String content, String psessionid) {
        this.to = friend;
        this.content = new Gson().toJson(new Object[]{content, new Object[]{"font", new ResultPoll.Item.Content.Font()}});
        this.msg_id = BaseReq.MSGID++;
        this.psessionid = psessionid;
    }
}
