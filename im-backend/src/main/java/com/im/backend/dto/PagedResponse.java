package com.im.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 分页响应DTO
 */
@Schema(description = "分页响应")
public class PagedResponse<T> {

    @Schema(description = "数据列表")
    private List<T> content;

    @Schema(description = "当前页码", example = "0")
    private int page;

    @Schema(description = "每页大小", example = "20")
    private int size;

    @Schema(description = "总元素数", example = "100")
    private long totalElements;

    @Schema(description = "总页数", example = "5")
    private int totalPages;

    public PagedResponse() {
    }

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    // Getters and Setters

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
