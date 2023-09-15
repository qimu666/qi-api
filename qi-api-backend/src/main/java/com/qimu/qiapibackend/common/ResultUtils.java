package com.qimu.qiapibackend.common;

/**
 * 返回工具类
 *
 * @author qimu
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 错误
     * 失败
     *
     * @param errorCode 错误代码
     * @return {@link BaseResponse}
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 错误
     *
     * @param data    数据
     * @param message 消息
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> error(T data, String message) {
        return new BaseResponse<>(202, data, message);
    }

    /**
     * 错误
     * 失败
     *
     * @param code    密码
     * @param message 消息
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }


    /**
     * 错误
     *
     * @param data      数据
     * @param errorCode 错误代码
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> error(T data, ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), data, errorCode.getMessage());
    }

    /**
     * 错误
     *
     * @param data      数据
     * @param errorCode 错误代码
     * @param message   消息
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> error(T data, ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), data, message);
    }

    /**
     * 错误
     * 失败
     *
     * @param errorCode 错误代码
     * @param message   消息
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
