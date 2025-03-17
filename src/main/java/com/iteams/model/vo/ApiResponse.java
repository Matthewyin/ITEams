package com.iteams.model.vo;

import lombok.Data;

/**
 * 统一API响应结果视图对象
 * <p>
 * 该类封装了RESTful API的标准响应格式，
 * 提供了一致的接口返回结构，包含操作状态、业务状态码、提示消息和数据载荷。
 * 通过泛型支持各种不同类型的返回数据，同时记录响应时间戳，
 * 便于日志记录和排查问题。
 * </p>
 * <p>
 * 使用静态工厂方法创建成功或失败的响应对象，简化控制器代码：
 * <pre>
 *     // 成功响应示例
 *     return ApiResponse.success(data);
 *     
 *     // 失败响应示例
 *     return ApiResponse.error("数据验证失败");
 * </pre>
 * </p>
 * 
 * @param <T> 响应数据的类型
 */
@Data
public class ApiResponse<T> {

    /**
     * 操作是否成功
     * true表示操作成功，false表示操作失败
     */
    private boolean success;
    
    /**
     * 业务状态码
     * 成功固定为"200"，失败则使用自定义错误码
     */
    private String code;
    
    /**
     * 提示消息
     * 成功或失败的具体描述信息
     */
    private String message;
    
    /**
     * 响应数据
     * 成功时返回的业务数据
     */
    private T data;
    
    /**
     * 响应时间戳
     * 记录响应生成的时间点（毫秒级时间戳）
     */
    private long timestamp;

    /**
     * 私有构造方法，防止直接实例化
     * 初始化时间戳为当前系统时间
     */
    private ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 创建成功响应，带数据
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @return 成功的API响应对象，状态码200，默认消息"操作成功"
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 创建成功响应，带数据和自定义消息
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @param message 自定义成功消息
     * @return 成功的API响应对象，状态码200
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setCode("200");
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 创建失败响应，带错误消息
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 失败的API响应对象，默认状态码500
     */
    public static <T> ApiResponse<T> error(String message) {
        return error("500", message);
    }

    /**
     * 创建失败响应，带自定义错误码和错误消息
     * 
     * @param <T> 数据类型
     * @param code 自定义错误码
     * @param message 错误消息
     * @return 失败的API响应对象
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
} 