package com.paymen.utils;

import java.io.*;
import java.util.Objects;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-26 20:25
 * @Version 1.0
 **/
public class FileUtils {

    /**
     * 读取文件返回字节
     *
     * @param file
     * @return
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
            if (file.exists()) {
                file.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(fis)) {
                fis.close();
            }
            if (Objects.nonNull(bos)) {
                bos.close();
            }
        }
        return buffer;
    }

    /**
     * 读取字节返回文件
     *
     * @param byteArray
     * @param targetPath
     */
    public static void readByteToFile(byte[] byteArray, String targetPath) {
        InputStream in = new ByteArrayInputStream(byteArray);
        File file = new File(targetPath);
        String path = targetPath.substring(0, targetPath.lastIndexOf("/"));
        if (!file.exists()) {
            new File(path).mkdir();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
