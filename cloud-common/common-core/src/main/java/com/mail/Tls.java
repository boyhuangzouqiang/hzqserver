//package com.mail;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocket;
//import javax.net.ssl.SSLSocketFactory;
//
///**
// * @description:
// * @author: huangzouqiang
// * @create: 2024-06-25 17:27
// * @Version 1.0
// **/
//public class Tls {
//
//    /**
//     * 查看 本地开发端 (jdk支持的TLS协议版本)
//     *
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        SSLContext context = SSLContext.getInstance("TLS");
//        context.init(null, null, null);
//
//        SSLSocketFactory factory = (SSLSocketFactory) context.getSocketFactory();
//        SSLSocket socket = (SSLSocket) factory.createSocket();
//
//        String[] protocols = socket.getSupportedProtocols();
//
//        System.out.println("Supported Protocols: " + protocols.length);
//        for (int i = 0; i < protocols.length; i++) {
//            System.out.println(" " + protocols[i]);
//        }
//
//        protocols = socket.getEnabledProtocols();
//
//        System.out.println("Enabled Protocols: " + protocols.length);
//        for (int i = 0; i < protocols.length; i++) {
//            System.out.println(" " + protocols[i]);
//        }
//
//    }
//}
