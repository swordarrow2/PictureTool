package com.meng.qqbot;

import android.app.*;
import android.os.*;
import android.widget.*;
import com.meng.mbrowser.tools.*;
import java.io.*;
import me.xuxiaoxiao.chatapi.qq.*;
import me.xuxiaoxiao.chatapi.qq.entity.message.*;
import com.meng.botandroid.*;


public class MainActivity extends Activity{
	public static MainActivity instence;
	public TextView tv;
	QQClient QQ_CLIENT;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ExceptionCatcher.getInstance().init(this);

		tv=new TextView(this);
		instence=this;
		setContentView(tv);

		QQ_CLIENT=new QQClient(new QQClient.QQChatListener() {
				@Override
				public void onQRCode(File qrCode){
					Toast.makeText(MainActivity.this,String.format("获取到登录二维码"),Toast.LENGTH_SHORT).show();
					//println();
					File f=new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"11111");
					try{
						f.createNewFile();
					}catch(IOException e){}
				}

				@Override
				public void onAvatar(String base64Avatar){
					println(String.format("获取到用户头像：%s",base64Avatar));
				}

				@Override
				public void onException(String reason){
					println(String.format("程序异常：%s",reason));
				}

				@Override
				public void onLogin(){
					println("登录成功");
				}

				@Override
				public void onMessage(QQMessage qqMessage){
					println(QQTools.GSON.toJson(qqMessage));
					if(qqMessage.fromGroup!=null&&qqMessage.fromGroupMember.id!=QQ_CLIENT.userMe().id){
						QQ_CLIENT.sendText(qqMessage.fromGroup,qqMessage.content);
					}
					if(qqMessage.fromDiscuss!=null&&qqMessage.fromDiscussMember.id!=QQ_CLIENT.userMe().id){
						QQ_CLIENT.sendText(qqMessage.fromDiscuss,qqMessage.content);
					}
					if(qqMessage.fromUser!=null&&qqMessage.fromUser.id!=QQ_CLIENT.userMe().id){
						QQ_CLIENT.sendText(qqMessage.fromUser,qqMessage.content);
					}
				}

				@Override
				public void onLogout(){
					println("退出登录");
				}
			});
			QQ_CLIENT.startup();
		//	QQ_CLIENT.sendText(QQ_CLIENT.userGroup(groupId),content);
		//println("fjfjfjdj");
	}
	
	void println(String s){
		//tv.setText(tv.getText().toString()+"\n"+s);
		
	}
}
