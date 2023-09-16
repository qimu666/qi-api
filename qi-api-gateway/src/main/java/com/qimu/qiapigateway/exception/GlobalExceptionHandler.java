package com.qimu.qiapigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qimu.qiapicommon.common.BaseResponse;
import com.qimu.qiapicommon.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

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
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (response.isCommitted()) {
            return Mono.error(ex);
        } else {
            return response.writeWith(Mono.fromSupplier(() -> {
                DataBufferFactory bufferFactory = response.bufferFactory();
                try {
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    BaseResponse<String> error = ResultUtils.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
                    log.error("【网关异常】：{}", error);
                    return bufferFactory.wrap(objectMapper.writeValueAsBytes(error));
                } catch (JsonProcessingException e) {
                    return bufferFactory.wrap(new byte[0]);
                }
            }));
        }
    }
}