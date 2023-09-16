package com.qimu.qiapigateway.utils;

import com.qimu.qiapicommon.common.ErrorCode;
import com.qimu.qiapigateway.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @Author: QiMu
 * @Date: 2023/09/11 11:11:26
 * @Version: 1.0
 * @Description: Redisson Lock Utils
 */
@Slf4j
@Component
public class RedissonLockUtil {

    @Resource
    public RedissonClient redissonClient;

    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param supplier     供应商
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                return supplier.get();
            }
            throw new BusinessException(errorCode.getCode(), errorMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.error("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param waitTime     等待时间
     * @param leaseTime    租赁时间
     * @param unit         单元
     * @param lockName     锁名称
     * @param supplier     供应商
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @param args         args
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(long waitTime, long leaseTime, TimeUnit unit, String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage, Object... args) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(waitTime, leaseTime, unit)) {
                return supplier.get();
            }
            throw new BusinessException(errorCode.getCode(), errorMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param unit         时间单位
     * @param lockName     锁名称
     * @param supplier     供应商
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @param time         时间
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(long time, TimeUnit unit, String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(time, unit)) {
                return supplier.get();
            }
            throw new BusinessException(errorCode.getCode(), errorMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }


    /**
     * redisson分布式锁
     *
     * @param lockName  锁名称
     * @param supplier  供应商
     * @param errorCode 错误代码
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, ErrorCode errorCode) {
        return redissonDistributedLocks(lockName, supplier, errorCode, errorCode.getMessage());
    }

    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param supplier     供应商
     * @param errorMessage 错误消息
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, String errorMessage) {
        return redissonDistributedLocks(lockName, supplier, ErrorCode.OPERATION_ERROR, errorMessage);
    }

    /**
     * redisson分布式锁
     *
     * @param lockName 锁名称
     * @param supplier 供应商
     * @return {@link T}
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier) {
        return redissonDistributedLocks(lockName, supplier, ErrorCode.OPERATION_ERROR);
    }

    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param runnable     可运行
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                runnable.run();
            } else {
                throw new BusinessException(errorCode.getCode(), errorMessage);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("lockName:{},unLockId:{} ", lockName, Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param lockName  锁名称
     * @param runnable  可运行
     * @param errorCode 错误代码
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, ErrorCode errorCode) {
        redissonDistributedLocks(lockName, runnable, errorCode, errorCode.getMessage());
    }

    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param runnable     可运行
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable, String errorMessage) {
        redissonDistributedLocks(lockName, runnable, ErrorCode.OPERATION_ERROR, errorMessage);
    }

    /**
     * redisson分布式锁
     *
     * @param lockName 锁名称
     * @param runnable 可运行
     */
    public void redissonDistributedLocks(String lockName, Runnable runnable) {
        redissonDistributedLocks(lockName, runnable, ErrorCode.OPERATION_ERROR, ErrorCode.OPERATION_ERROR.getMessage());
    }


    /**
     * redisson分布式锁 可自定义 waitTime 、leaseTime、TimeUnit
     *
     * @param waitTime     等待时间
     * @param leaseTime    租赁时间
     * @param unit         时间单位
     * @param lockName     锁名称
     * @param runnable     可运行
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(long waitTime, long leaseTime, TimeUnit unit, String lockName, Runnable runnable, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(waitTime, leaseTime, unit)) {
                runnable.run();
            } else {
                throw new BusinessException(errorCode.getCode(), errorMessage);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁 可自定义 time 、unit
     *
     * @param time         时间
     * @param unit         时间单位
     * @param lockName     锁名称
     * @param runnable     可运行
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public void redissonDistributedLocks(long time, TimeUnit unit, String lockName, Runnable runnable, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(time, unit)) {
                runnable.run();
            } else {
                throw new BusinessException(errorCode.getCode(), errorMessage);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }
}