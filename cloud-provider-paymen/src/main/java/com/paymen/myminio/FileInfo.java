package com.paymen.myminio;

import lombok.Getter;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-26 20:19
 * @Version 1.0
 **/
@Getter
public class FileInfo {
    //文件的大小
    private long fSize;
    //文件的名称
    private String fName;

    public FileInfo(long fSize, String fName) {
        this.fSize = fSize;
        this.fName = fName;
    }
}
