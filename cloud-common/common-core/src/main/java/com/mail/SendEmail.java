//package com.mail;
//
///**
// * @description:
// * @author: huangzouqiang
// * @create: 2024-06-25 16:32
// * @Version 1.0
// **/
//
//import javax.mail.*;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.util.Properties;
//
//public class SendEmail {
//    public static void sendEmail(String host, String port,
//                                 final String userName, final String password, String toAddress,
//                                 String subject, String emailBody) throws AddressException,
//            MessagingException {
//        // 设置SMTP服务器属性
//        Properties properties = new Properties();
//        properties.put("mail.smtp.host", host);
//        // 默认端口号设置为587，也可以设置为465，具体取决于SMTP服务器要求的端口号
//        properties.put("mail.smtp.port", port);
//        // 需要请求认证
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
////        properties.put("mail.smtp.ssl.protocols", "TLSv1");
//
//        // 新建一个认证器
//        Authenticator auth = new Authenticator() {
//            public PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(userName, password);
//            }
//        };
//
//        // 新建一个会话
//        Session session = Session.getInstance(properties, auth);
//        //打印执行的command，方便开发调试
//        session.setDebug(true);
//
//        // 新建一个消息
//        MimeMessage message = new MimeMessage(session);
//
//        // 设置发件人
//        message.setFrom(new InternetAddress(userName));
//
//        // 设置收件人
//        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
//
//        // 设置邮件主题
//        message.setSubject(subject);
//
//        // 设置邮件内容
//        message.setText(emailBody);
//
//        // 发送邮件
//        Transport.send(message);
//
//        System.out.println("Email sent successfully!");
//    }
//
//    public static void main(String[] args) {
//        // SMTP服务器地址
//        String host = "192.168.23.129";
//        // SMTP服务器端口
//        String port = "587";
//        // 用户名
//        String userName = "hzx116@ewomail.com";
//        // 密码
//        String password = "Isc$2024.";
//        // 收件人地址
//        String toAddress = "aiceshi01@ewomail.com";
//        // 邮件主题
//        String subject = "Test Email";
//        // 邮件正文
//        String emailBody = "This is a test email.dddddddddddddddddddd";
//
//        try {
//            sendEmail(host, port, userName, password, toAddress, subject, emailBody);
//        } catch (AddressException e) {
//            e.printStackTrace();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//}
