package com.powergrid.foundation.core.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.powergrid.foundation.core.persistence.model.FoundationLegRuleRow;

@Mapper
public interface FoundationLegRuleMapper {
    FoundationLegRuleRow selectLegRule(@Param("projectId") String projectId);

    int replaceLegRule(
            @Param("ruleId") String ruleId,
            @Param("projectId") String projectId,
            @Param("extractHeight") Double extractHeight,
            @Param("heightAllow") Double heightAllow,
            @Param("heightStep") Double heightStep,
            @Param("minLzkt") Double minLzkt,
            @Param("maxLzkt") Double maxLzkt,
            @Param("dado") Integer dado
    );
}
