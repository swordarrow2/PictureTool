package com.meng.picTools.lib;

import android.app.*;
import android.content.*;
import android.net.*;

import com.meng.picTools.helpers.SharedPreferenceHelper;
import com.meng.picTools.lib.javaBean.*;
import java.util.*;
import org.jsoup.*;

public class GithubUpdateManager {
    private Activity activity;

    public GithubUpdateManager(Activity activity, String userName, String nameOnGithub) {
        this.activity = activity;
        checkUpdate(userName, nameOnGithub);
	  }

    private void checkUpdate(final String userName, final String nameOnGithub) {
        new Thread(new Runnable() {

			  @Override
			  public void run() {
				  final UpdateInfo updateInfo = new UpdateInfo(activity);
				  try {
					  if (updateInfo.error) {
						  return;
						}
					  Connection connection = Jsoup.connect("https://github.com/" + userName + "/" + nameOnGithub + "/releases/latest");
					  connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0");
					  connection.ignoreContentType(true).method(Connection.Method.GET).followRedirects(false);
					  Connection.Response response = connection.execute();
					  Map<String, String> head = response.headers();
					  updateInfo.setNewVersionLink(head.get("Location"));
					} catch (Exception e) {
					  e.printStackTrace();
					  return;
					}
				  if (!SharedPreferenceHelper.getValue("newVersion", "0.0.0").equals(updateInfo.getVersionName())) {
					  activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (updateInfo.newFunction || updateInfo.optimize || updateInfo.bugFix) {

									new AlertDialog.Builder(activity)
									  .setTitle("发现新版本")
									  .setMessage(updateInfo.getUpdateNote())
									  .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
										  @Override
										  public void onClick(DialogInterface p1, int p2) {
											  Intent intent = new Intent();
											  intent.setAction("android.intent.action.VIEW");
											  Uri contentUrl = Uri.parse("https://github.com/" + userName + "/" + nameOnGithub + "/releases/download/" + updateInfo.getVersionName() + "/pictool.apk");
											  intent.setData(contentUrl);
											  activity.startActivity(intent);
                                            }
                                        }).setNeutralButton("下次提醒我", null)
									  .setNegativeButton("忽略本次更新", new DialogInterface.OnClickListener() {
										  @Override
										  public void onClick(DialogInterface dialog, int which) {
											  SharedPreferenceHelper.putValue("newVersion", updateInfo.getVersionName());
                                            }
                                        }).show();
								  }
							  }
						  });
					}
				}
			}).start();
	  }
  }

