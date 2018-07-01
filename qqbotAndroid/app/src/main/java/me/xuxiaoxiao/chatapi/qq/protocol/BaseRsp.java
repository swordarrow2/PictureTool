package me.xuxiaoxiao.chatapi.qq.protocol;

public class BaseRsp<T> {
    public int retcode;
    public String retmsg;
    public T result;
    public String errmsg;
}
