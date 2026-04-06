package com.hzqserver.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzqserver.file.entity.FileUploadRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文件上传记录Mapper
 */
@Mapper
public interface FileUploadRecordMapper extends BaseMapper<FileUploadRecord> {
    
    /**
     * 根据文件MD5查询上传记录
     */
    FileUploadRecord selectByFileMd5(@Param("fileMd5") String fileMd5);
    
    /**
     * 分页查询已完成的上传记录
     */
    @Select("SELECT * FROM file_upload_record WHERE status = 2 AND deleted = 0 ORDER BY create_time DESC")
    Page<FileUploadRecord> selectCompletedRecords(Page<FileUploadRecord> page);
}
