package com.iteams.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API统一响应格式
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应码，0表示成功，非0表示失败
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     *
     * @return API响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(0, "操作成功", null);
    }

    /**
     * 成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(0, message, data);
    }

    /**
     * 错误响应
     *
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(1, message, null);
    }

    /**
     * 错误响应（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
