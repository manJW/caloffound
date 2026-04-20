package com.powergrid.foundation.core.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.powergrid.foundation.core.persistence.model.FoundationSurveyPointRow;

@Mapper
public interface FoundationSurveyMapper {
    String selectSurveyIdByNodeId(@Param("nodeId") String nodeId);

    String selectSurveyTypeByNodeId(@Param("nodeId") String nodeId);

    List<FoundationSurveyPointRow> selectSurveyPoints(@Param("surveyId") String surveyId);

    int insertSurveyHeader(
            @Param("surveyId") String surveyId,
            @Param("nodeId") String nodeId,
            @Param("surveyType") String surveyType,
            @Param("mark") String mark
    );

    int updateSurveyHeader(
            @Param("surveyId") String surveyId,
            @Param("surveyType") String surveyType,
            @Param("mark") String mark
    );

    int deleteSurveyPoints(@Param("surveyId") String surveyId);

    int deleteSurveyHeader(@Param("surveyId") String surveyId);

    int updateSurveyTif(
            @Param("surveyId") String surveyId,
            @Param("tifFile") byte[] tifFile,
            @Param("resolution") Double resolution
    );

    int clearSurveyTif(@Param("surveyId") String surveyId);

    int insertSurveyPoint(
            @Param("positionId") String positionId,
            @Param("surveyId") String surveyId,
            @Param("posX") Double posX,
            @Param("posY") Double posY,
            @Param("posZ") Double posZ
    );
}
