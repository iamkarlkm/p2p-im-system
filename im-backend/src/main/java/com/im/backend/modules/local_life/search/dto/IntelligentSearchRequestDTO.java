package com.im.backend.modules.local_life.search.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 智能搜索请求DTO
 */
@Data
public class IntelligentSearchRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索查询文本（自然语言）
     */
    @NotBlank(message = "搜索内容不能为空")
    @Size(max = 500, message = "搜索内容不能超过500字符")
    private String query;

    /**
     * 会话ID（多轮对话使用）
     */
    private String sessionId;

    /**
     * 用户当前经度
     */
    private Double longitude;

    /**
     * 用户当前纬度
     */
    private Double latitude;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 是否开启语音搜索
     */
    private Boolean voiceSearch = false;

    /**
     * 语音数据Base64（如果是语音搜索）
     */
    private String voiceData;

    /**
     * 排序方式
     * DEFAULT-默认, DISTANCE-距离, RATING-评分, HEAT-热度, PRICE_ASC-价格升序, PRICE_DESC-价格降序
     */
    private String sortBy = "DEFAULT";

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;

    /**
     * 搜索来源
     */
    private String source;

    /**
     * 设备ID
     */
    private String deviceId;
}
