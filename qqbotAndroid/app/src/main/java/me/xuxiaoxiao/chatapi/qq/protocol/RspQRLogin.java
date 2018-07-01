package me.xuxiaoxiao.chatapi.qq.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RspQRLogin {
    public int code;
    public int arg0;
    public String uri;
    public int arg1;
    public String info;
    public String nick;

    public RspQRLogin(String str) {
        Matcher matcher = Pattern.compile("ptuiCB\\((.*)\\)").matcher(str);
        if (matcher.find()) {
            String[] params = matcher.group(1).split(",");
            this.code = Integer.valueOf(params[0].substring(params[0].indexOf('\'') + 1, params[0].lastIndexOf('\'')));
            this.arg0 = Integer.valueOf(params[1].substring(params[1].indexOf('\'') + 1, params[1].lastIndexOf('\'')));
            this.uri = params[2].substring(params[2].indexOf('\'') + 1, params[2].lastIndexOf('\''));
            this.arg1 = Integer.valueOf(params[3].substring(params[3].indexOf('\'') + 1, params[3].lastIndexOf('\'')));
            this.info = params[4].substring(params[4].indexOf('\'') + 1, params[4].lastIndexOf('\''));
            this.nick = params[5].substring(params[5].indexOf('\'') + 1, params[5].lastIndexOf('\''));
        } else {
            throw new RuntimeException("不能正确解析QRLogin的返回值");
        }
    }
}
