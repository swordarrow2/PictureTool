package me.xuxiaoxiao.chatapi.qq.protocol;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ResultPoll extends ArrayList<ResultPoll.Item> {

    public static class Item {
        public String poll_type;
        public Message value;

        public static class Message {
            public long from_uin;
            public long group_code;
            public long did;
            public long msg_id;
            public int msg_type;
            public long send_uin;
            public long time;
            public long to_uin;
            public Content content;
        }

        public static class Content extends ArrayList<Object> {

            public static class Font {
                public String name = "宋体";
                public String color = "000000";
                public int size = 10;
                public int[] style = {0, 0, 0};
            }
        }
    }

    public static class ContentParser implements JsonDeserializer<Item.Content> {

        @Override
        public Item.Content deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray contentJson = jsonElement.getAsJsonArray();
            Item.Content content = new Item.Content();

            JsonObject fontJson = contentJson.get(0).getAsJsonArray().get(1).getAsJsonObject();
            Item.Content.Font font = new Item.Content.Font();
            font.name = fontJson.get("name").getAsString();
            font.color = fontJson.get("color").getAsString();
            font.size = fontJson.get("size").getAsInt();
            JsonArray styleArr = fontJson.get("style").getAsJsonArray();
            font.style = new int[styleArr.size()];
            for (int i = 0; i < font.style.length; i++) {
                font.style[i] = styleArr.get(i).getAsInt();
            }
            content.add(font);
            for (int i = 1; i < contentJson.size(); i++) {
                if (contentJson.get(i).isJsonPrimitive()) {
                    content.add(contentJson.get(i).getAsString());
                }
            }
            return content;
        }
    }
}
