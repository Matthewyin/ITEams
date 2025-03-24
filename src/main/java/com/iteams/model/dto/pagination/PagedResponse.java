package com.iteams.model.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应包装类
 * <p>
 * 用于包装Spring Data的分页结果，提供稳定的JSON序列化结构，
 * 避免直接序列化PageImpl导致的警告和潜在问题。
 * </p>
 *
 * @param <T> 数据项类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    /**
     * 当前页的数据内容
     */
    private List<T> content;
    
    /**
     * 分页元数据
     */
    private PageMetadata metadata;
    
    /**
     * 从Spring Data的Page对象创建分页响应
     *
     * @param page Spring Data Page对象
     * @param <T> 数据项类型
     * @return 分页响应
     */
    public static <T> PagedResponse<T> of(Page<T> page) {
        PageMetadata metadata = new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
        
        return new PagedResponse<>(page.getContent(), metadata);
    }
    
    /**
     * 分页元数据，包含页码、大小、总数等信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageMetadata {
        /**
         * 当前页码（从0开始）
         */
        private int page;
        
        /**
         * 每页大小
         */
        private int size;
        
        /**
         * 总记录数
         */
        private long totalElements;
        
        /**
         * 总页数
         */
        private int totalPages;
        
        /**
         * 是否为第一页
         */
        private boolean isFirst;
        
        /**
         * 是否为最后一页
         */
        private boolean isLast;
        
        /**
         * 是否有下一页
         */
        private boolean hasNext;
        
        /**
         * 是否有上一页
         */
        private boolean hasPrevious;
    }
} 