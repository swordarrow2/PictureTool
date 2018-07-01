package me.xuxiaoxiao.chatapi.qq;

import android.widget.*;
import com.google.gson.*;
import com.meng.botandroid.*;
import java.util.logging.*;
import me.xuxiaoxiao.chatapi.qq.protocol.*;

/**
 * QQ工具类
 */
public final class QQTools {
  //  static final Logger LOGGER = Logger.getLogger("me.xuxiaoxiao.chatapi.qq");
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResultPoll.Item.Content.class, new ResultPoll.ContentParser()).create();

    static String hash(String qqStr, String ptWebqq) {
        int[] ptWebqqHash = new int[4];
        for (int i = 0; i < ptWebqq.length(); i++) {
            ptWebqqHash[i % 4] ^= ptWebqq.charAt(i);
        }
        int[] qqStrHash = new int[4];
        long lb = Long.parseLong(qqStr);
        qqStrHash[0] = (int) (lb >> 24 & 255 ^ 'E');
        qqStrHash[1] = (int) (lb >> 16 & 255 ^ 'C');
        qqStrHash[2] = (int) (lb >> 8 & 255 ^ 'O');
        qqStrHash[3] = (int) (lb & 255 ^ 'K');
        int[] hash = new int[8];
        for (int i = 0; i < hash.length; i++) {
            hash[i] = i % 2 == 0 ? ptWebqqHash[i >> 1] : qqStrHash[i >> 1];
        }
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuilder str = new StringBuilder();
        for (int integer : hash) {
            str.append(hex[integer >> 4 & 15]);
            str.append(hex[integer & 15]);
        }
        return str.toString();
    }

    static int hashQRSig(String qrsig) {
        int e = 0, n = qrsig.length();
        for (int i = 0; n > i; ++i) {
            e += (e << 5) + qrsig.charAt(i);
        }
        return 2147483647 & e;
    }
	static class LOGGER{

		public static void warning(String format){
			// TODO: Implement this method
			settext(format);
		}

		public static void severe(String format){
			// TODO: Implement this method
			settext(format);
		}

		public static void finer(String format){
			// TODO: Implement this method
			settext(format);
		}

		public static void addHandler(Handler handler){
			// TODO: Implement this method
		}

		public static void fine(String format){
			// TODO: Implement this method
			settext(format);
		}

		public static void setUseParentHandlers(boolean p0){
			// TODO: Implement this method
		}
		


		public static void setLevel(Level level){
			// TODO: Implement this method
		}
		private static void settext(String s){
			
			Toast.makeText(MainActivity.instence.getApplicationContext(),s,Toast.LENGTH_SHORT).show();
			}
		}
}
