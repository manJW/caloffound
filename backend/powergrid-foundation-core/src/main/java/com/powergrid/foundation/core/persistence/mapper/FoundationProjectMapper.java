package com.powergrid.foundation.core.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.powergrid.foundation.core.persistence.model.FoundationProjectNodeRow;
import com.powergrid.foundation.core.persistence.model.FoundationProjectSummaryRow;

@Mapper
public interface FoundationProjectMapper {
    List<FoundationProjectSummaryRow> selectProjectSummaries();

    FoundationProjectSummaryRow selectProjectSummary(@Param("projectId") String projectId);

    List<FoundationProjectNodeRow> selectProjectNodes(@Param("projectId") String projectId);

    Integer selectMaxNodeOrder(@Param("projectId") String projectId);

    int insertProject(
            @Param("projectId") String projectId,
            @Param("projectCode") String projectCode,
            @Param("projectName") String projectName,
            @Param("startAddress") String startAddress,
            @Param("endAddress") String endAddress
    );

    int updateProject(
            @Param("projectId") String projectId,
            @Param("projectCode") String projectCode,
            @Param("projectName") String projectName,
            @Param("startAddress") String startAddress,
            @Param("endAddress") String endAddress
    );

    int insertProjectNode(
            @Param("nodeId") String nodeId,
            @Param("projectId") String projectId,
            @Param("towerNo") String towerNo,
            @Param("stake") String stake,
            @Param("nodeOrder") Integer nodeOrder
    );

    int updateProjectNode(
            @Param("nodeId") String nodeId,
            @Param("towerNo") String towerNo,
            @Param("stake") String stake,
            @Param("nodeOrder") Integer nodeOrder
    );

    int deleteProject(@Param("projectId") String projectId);

    int deleteProjectNode(@Param("nodeId") String nodeId);
}
