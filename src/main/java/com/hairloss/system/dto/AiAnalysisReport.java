package com.hairloss.system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.volcengine.ark.runtime.model.responses.usage.Usage;
import lombok.Data;

/**
 * AI 毛发分析报告 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAnalysisReport {

    /**
     * 基本信息
     */
    @JsonProperty("basicInfo")
    private BasicInfo basicInfo;

    /**
     * 评分信息
     */
    @JsonProperty("score")
    private ScoreInfo scoreInfo;

    /**
     * 趋势分析
     */
    @JsonProperty("trend")
    private TrendInfo trendInfo;

    /**
     * 详细分析
     */
    @JsonProperty("analysis")
    private AnalysisInfo analysisInfo;

    /**
     * 建议方案
     */
    @JsonProperty("suggestion")
    private SuggestionInfo suggestionInfo;

    /**
     * 总体结论
     */
    @JsonProperty("conclusion")
    private String conclusion;

    /**
     * 基本信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BasicInfo {
        @JsonProperty("part")
        private String part;
        
        @JsonProperty("timeInterval")
        private String timeInterval;
        
        @JsonProperty("image1Time")
        private String image1Time;
        
        @JsonProperty("image2Time")
        private String image2Time;
    }

    /**
     * 评分信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScoreInfo {
        @JsonProperty("hairDensityScore")
        private Integer hairDensityScore;
        
        @JsonProperty("hairLossImproveScore")
        private Integer hairLossImproveScore;
    }

    /**
     * 趋势分析
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrendInfo {
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("description")
        private String description;
    }

    /**
     * 详细分析
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalysisInfo {
        @JsonProperty("compareResult")
        private String compareResult;
        
        @JsonProperty("keyChanges")
        private String keyChanges;
    }

    /**
     * 建议方案
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SuggestionInfo {
        @JsonProperty("treatmentSuggestion")
        private String treatmentSuggestion;
        
        @JsonProperty("dailyCare")
        private String dailyCare;
        
        @JsonProperty("nextStep")
        private String nextStep;
    }


    private Usage usageInfo;
}
