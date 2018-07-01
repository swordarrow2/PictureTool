package me.xuxiaoxiao.chatapi.qq.protocol;

import java.util.Random;

public class BaseReq {
    public static int MSGID = new Random().nextInt(10000) * 10000;

    public long clientid = 53999199;
    public String key = "";
    public String status = "online";
    public String hash;
    public String ptwebqq;
    public String vfwebqq;
    public String psessionid;

    public BaseReq(String hash, String ptwebqq, String vfwebqq, String psessionid) {
        this.hash = hash;
        this.ptwebqq = ptwebqq;
        this.vfwebqq = vfwebqq;
        this.psessionid = psessionid;
    }
}
