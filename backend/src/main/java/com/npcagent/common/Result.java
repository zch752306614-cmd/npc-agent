package com.npcagent.common;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 统一API返回结果
 *
 * 规范说明：
 * 1. code: 状态码，200表示成功，其他表示失败
 * 2. message: 提示信息
 * 3. data: 返回数据
 * 4. timestamp: 时间戳
 *
 * 使用示例：
 * - 成功：Result.success(data)
 * - 失败：Result.error(500, "服务器内部错误")
 */
@Data
public class Result<T> {

    /**
     * 状态码
     * 200 - 成功
     * 400 - 请求参数错误
     * 401 - 未授权
     * 403 - 禁止访问
     * 404 - 资源不存在
     * 500 - 服务器内部错误
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 成功返回结果（无数据）
     *
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 错误信息
     * @param <T>     数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    /**
     * 失败返回结果（默认500）
     *
     * @param message 错误信息
     * @param <T>     数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 参数错误
     *
     * @param message 错误信息
     * @param <T>     数据类型
     * @return Result对象
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 资源不存在
     *
     * @param message 错误信息
     * @param <T>     数据类型
     * @return Result对象
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * 判断是否成功
     *
     * @return true表示成功
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
