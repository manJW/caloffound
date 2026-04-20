package com.powergrid.foundation.core.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FoundationTowerReferenceMapper {
    String selectProjectTowerTypeId(
            @Param("projectId") String projectId,
            @Param("towerTypeName") String towerTypeName
    );

    String selectDefaultTowerBodyId(@Param("towerTypeId") String towerTypeId);
}
