package com.qimu.qiapigateway;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static icu.qimuu.qiapisdk.utils.SignUtils.getSign;


/**
 * @Author: QiMu
 * @Date: 2023/09/14 10:42:06
 * @Version: 1.0
 * @Description: 网关全局过滤器
 */
@Component
@Slf4j
public class GatewayGlobalFilter implements GlobalFilter, Ordered {
    /**
     * 请求白名单
     */
    private final static List<String> WHITE_HOST_LIST = Collections.singletonList("127.0.0.1");
    /**
     * 五分钟过期时间
     */
    private static final long FIVE_MINUTES = 5L * 60;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            log.info("请求唯一id：" + request.getId());
            log.info("请求参数：" + request.getQueryParams());
            log.info("请求方法：" + request.getMethod());
            log.info("请求路径：" + request.getPath());
            log.info("网关本地地址：" + request.getLocalAddress());
            log.info("请求远程地址：" + request.getRemoteAddress());
            // 黑白名单
            if (!WHITE_HOST_LIST.contains(Objects.requireNonNull(request.getRemoteAddress()).getHostString())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
            }
            HttpHeaders headers = request.getHeaders();
            String body = headers.getFirst("body");
            String accessKey = headers.getFirst("accessKey");
            String timestamp = headers.getFirst("timestamp");
            String sign = headers.getFirst("sign");
            // 请求头中参数必须完整
            if (StringUtils.isAnyBlank(body, sign, accessKey, timestamp)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
            }
            // 防重发XHR
            long currentTime = System.currentTimeMillis() / 1000;
            assert timestamp != null;
            if (currentTime - Long.parseLong(timestamp) >= FIVE_MINUTES) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "会话已过期,请重试！");
            }
            // 校验accessKey
            if (!"7052a8594339a519e0ba5eb04a267a60".equals(accessKey)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 校验签名
            if (!getSign(body, "d8d6df60ab209385a09ac796f1dfe3e1").equals(sign)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // todo 校验请求的接口是否存在，url ,请求方法是否匹配

            return handleResponse(exchange, chain);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        }
    }

    /**
     * 处理响应
     *
     * @param exchange 交换
     * @param chain    链条
     * @return {@link Mono}<{@link Void}>
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        // 缓存数据的工厂
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        // 拿到响应码
        HttpStatus statusCode = originalResponse.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            // 装饰，增强能力
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                // 等调用完转发的接口后才会执行
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    log.info("body instanceof Flux: {}", (body instanceof Flux));
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        // 往返回值里写数据
                        return super.writeWith(
                                fluxBody.map(dataBuffer -> {
                                    // 扣除积分
                                    byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    // 释放掉内存
                                    DataBufferUtils.release(dataBuffer);
                                    String data = new String(content, StandardCharsets.UTF_8); // data
                                    // 打印日志
                                    log.info("响应结果：" + data);
                                    return bufferFactory.wrap(content);
                                }));
                    } else {
                        // 8. 调用失败，返回一个规范的错误码
                        log.error("<--- {} 响应code异常", getStatusCode());
                    }
                    return super.writeWith(body);
                }
            };
            // 设置 response 对象为装饰过的
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        }
        // 降级处理返回数据
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}