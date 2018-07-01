package me.xuxiaoxiao.chatapi.qq.protocol;

import java.util.ArrayList;

public class ResultGetOnlineBuddies extends ArrayList<ResultGetOnlineBuddies.OnlineBuddy> {

    public static class OnlineBuddy {
        public int client_type;
        public String status;
        public long uin;
    }
}
