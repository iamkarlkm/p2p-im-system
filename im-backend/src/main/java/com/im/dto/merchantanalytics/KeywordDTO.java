// 关键词DTO
package com.im.dto.merchantanalytics;

import lombok.Data;

@Data
public class KeywordDTO {
    private String word;
    private Integer count;
    private Double sentiment; // 情感分数 -1到1
}
