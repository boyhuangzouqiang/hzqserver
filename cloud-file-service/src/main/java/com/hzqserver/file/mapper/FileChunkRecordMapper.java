package com.hzqserver.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzqserver.file.entity.FileChunkRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分片上传记录Mapper
 */
@Mapper
public interface FileChunkRecordMapper extends BaseMapper<FileChunkRecord> {
    
    /**
     * 根据文件MD5查询已上传的分片列表
     */
    List<FileChunkRecord> selectByFileMd5(@Param("fileMd5") String fileMd5);
    
    /**
     * 删除指定文件的所有分片记录
     */
    void deleteByFileMd5(@Param("fileMd5") String fileMd5);
}
