package me.xuxiaoxiao.chatapi.qq;

import me.xuxiaoxiao.chatapi.qq.entity.message.QQMessage;

import java.io.File;
import java.util.Scanner;

public class QQDemo {

    public static final QQClient QQ_CLIENT = new QQClient(new QQClient.QQChatListener() {
        @Override
        public void onQRCode(File qrCode) {
            System.out.println(String.format("获取到登录二维码：%s", qrCode.getAbsolutePath()));
        }

        @Override
        public void onAvatar(String base64Avatar) {
            System.out.println(String.format("获取到用户头像：%s", base64Avatar));
        }

        @Override
        public void onException(String reason) {
            System.out.println(String.format("程序异常：%s", reason));
        }

        @Override
        public void onLogin() {
            System.out.println("登录成功");
        }

        @Override
        public void onMessage(QQMessage qqMessage) {
            System.out.println(QQTools.GSON.toJson(qqMessage));
            if (qqMessage.fromGroup != null && qqMessage.fromGroupMember.id != QQ_CLIENT.userMe().id) {
                QQ_CLIENT.sendText(qqMessage.fromGroup, qqMessage.content);
            }
            if (qqMessage.fromDiscuss != null && qqMessage.fromDiscussMember.id != QQ_CLIENT.userMe().id) {
                QQ_CLIENT.sendText(qqMessage.fromDiscuss, qqMessage.content);
            }
            if (qqMessage.fromUser != null && qqMessage.fromUser.id != QQ_CLIENT.userMe().id) {
                QQ_CLIENT.sendText(qqMessage.fromUser, qqMessage.content);
            }
        }

        @Override
        public void onLogout() {
            System.out.println("退出登录");
        }
    });

    public static void main(String[] args) {
        QQ_CLIENT.startup();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入指令");
            switch (scanner.nextLine()) {
                case "sendFriend": {
                    System.out.println("friendId:");
                    long friendId = Long.parseLong(scanner.nextLine());
                    System.out.println("content:");
                    String content = scanner.nextLine();
                    QQ_CLIENT.sendText(QQ_CLIENT.userFriend(friendId), content);
                }
                break;
                case "sendGroup": {
                    System.out.println("groupId:");
                    long groupId = Long.parseLong(scanner.nextLine());
                    System.out.println("content:");
                    String content = scanner.nextLine();
                    QQ_CLIENT.sendText(QQ_CLIENT.userGroup(groupId), content);
                }
                break;
                case "sendDiscuss": {
                    System.out.println("discussId:");
                    long discussId = Long.parseLong(scanner.nextLine());
                    System.out.println("content:");
                    String content = scanner.nextLine();
                    QQ_CLIENT.sendText(QQ_CLIENT.userDiscuss(discussId), content);
                }
                break;
                case "quit":
                    System.out.println("logging out");
                    QQ_CLIENT.shutdown();
                    return;
                default:
                    System.out.println("未知指令");
                    break;
            }
        }
    }
}
