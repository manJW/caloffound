package com.powergrid.foundation.core.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.powergrid.foundation.core.persistence.model.FoundationTowerAssignmentRow;
import com.powergrid.foundation.core.persistence.model.FoundationTowerLegRow;

@Mapper
public interface FoundationTowerMapper {
    List<FoundationTowerAssignmentRow> selectTowerAssignments(@Param("projectId") String projectId);

    FoundationTowerAssignmentRow selectTowerAssignment(@Param("nodeId") String nodeId);

    List<FoundationTowerLegRow> selectTowerLegs(@Param("nodeId") String nodeId);

    int updateTowerAssignment(
            @Param("nodeId") String nodeId,
            @Param("towerBodyId") String towerBodyId,
            @Param("posHeightAdjust") Double posHeightAdjust
    );

    int insertTowerAssignment(
            @Param("electricId") String electricId,
            @Param("nodeId") String nodeId,
            @Param("towerType") String towerType,
            @Param("posHeight") Double posHeight,
            @Param("angle") Double angle,
            @Param("lon") Double lon,
            @Param("lat") Double lat,
            @Param("mark") String mark,
            @Param("towerBodyId") String towerBodyId,
            @Param("posHeightAdjust") Double posHeightAdjust
    );

    int deleteTowerAssignment(@Param("nodeId") String nodeId);

    int deleteTowerLegs(@Param("nodeId") String nodeId);

    int insertTowerLeg(
            @Param("legId") String legId,
            @Param("nodeId") String nodeId,
            @Param("legName") String legName,
            @Param("legReduce") String legReduce,
            @Param("legLength") Double legLength,
            @Param("exposedHeight") Double exposedHeight,
            @Param("topElevation") Double topElevation,
            @Param("slope") Double slope,
            @Param("mark") String mark
    );
}
