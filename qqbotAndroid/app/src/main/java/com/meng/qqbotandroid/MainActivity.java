package com.meng.qqbotandroid;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.widget.*;
import com.meng.mbrowser.tools.*;
import java.io.*;
import me.xuxiaoxiao.chatapi.qq.*;
import me.xuxiaoxiao.chatapi.qq.entity.message.*;


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
				//	Looper.prepare();
				//	Toast.makeText(MainActivity.this,"获取到登录二维码",Toast.LENGTH_SHORT).show();
					Intent i = new Intent(MainActivity.this, ShowQrCodeActivity.class);
					i.putExtra("url",qrCode.getAbsolutePath());
					Log.i("tag",qrCode.getAbsolutePath());
					MainActivity.this.startActivity(i);
				//	Looper.loop();
					//println();
					
				}

				@Override
				public void onAvatar(String base64Avatar){
					Looper.prepare();
					Toast.makeText(MainActivity.this,String.format("获取到用户头像：%s",base64Avatar),Toast.LENGTH_SHORT).show();
					Looper.loop();
				}

				@Override
				public void onException(String reason){
					Looper.prepare();
					Toast.makeText(MainActivity.this,String.format("程序异常：%s",reason),Toast.LENGTH_SHORT).show();
					Looper.loop();
				}

				@Override
				public void onLogin(){
					Looper.prepare();
					Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
					Looper.loop();
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
