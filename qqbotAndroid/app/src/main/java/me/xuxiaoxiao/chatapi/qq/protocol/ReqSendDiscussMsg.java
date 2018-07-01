package me.xuxiaoxiao.chatapi.qq.protocol;

import com.google.gson.Gson;

public class ReqSendDiscussMsg {
    public long did;
    public String content;
    public int face = 522;
    public long clientid = 53999199;
    public long msg_id;
    public String psessionid;

    public ReqSendDiscussMsg(long discussId, String content, String psessionid) {
        this.did = discussId;
        this.content = new Gson().toJson(new Object[]{content, new Object[]{"font", new ResultPoll.Item.Content.Font()}});
        this.msg_id = BaseReq.MSGID++;
        this.psessionid = psessionid;
    }
}
