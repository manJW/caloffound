package com.powergrid.foundation.core.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.powergrid.foundation.core.persistence.model.FoundationCalcRecordRow;

@Mapper
public interface FoundationCalcRecordMapper {
    void ensureCalcRecordTable();

    int insertCalcRecord(FoundationCalcRecordRow row);

    List<FoundationCalcRecordRow> selectCalcRecords(
            @Param("projectId") String projectId,
            @Param("nodeId") String nodeId
    );

    FoundationCalcRecordRow selectCalcRecord(@Param("recordId") String recordId);
}
