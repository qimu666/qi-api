package com.qimu.qiapigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qimu.qiapicommon.common.BaseResponse;
import com.qimu.qiapicommon.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Author: QiMu
 * @Date: 2023/09/10 09:35:08
 * @Version: 1.0
 * @Description: 错误web异常处理程序
 */
@Configuration
@Slf4j
@Order(-1)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        DataBufferFactory bufferFactory = response.bufferFactory();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        BaseResponse<String> error = ResultUtils.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        log.error("【网关异常】：{}", error);
        try {
            byte[] errorBytes = objectMapper.writeValueAsBytes(error);
            DataBuffer dataBuffer = bufferFactory.wrap(errorBytes);
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化异常：{}", e.getMessage());
            return Mono.error(e);
        }
    }
}