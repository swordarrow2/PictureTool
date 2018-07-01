package me.xuxiaoxiao.chatapi.qq.protocol;

import java.util.ArrayList;

public class ResultGetRecentList extends ArrayList<ResultGetRecentList.Recent> {

    public static class Recent {
        public int type;
        public long uin;
    }
}
