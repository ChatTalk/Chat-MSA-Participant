//package com.example.chatserverparticipant.global.facade;
//
//import lombok.RequiredArgsConstructor;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
//@Component
//@RequiredArgsConstructor
//public class DistributedLockFacade {
//
//    private final RedissonClient redissonClient;
//
//    // 락 획득
//    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return false;
//        }
//    }
//
//    // 락 해제
//    public void unlock(String lockKey) {
//        RLock lock = redissonClient.getLock(lockKey);
//        if (lock.isHeldByCurrentThread()) {
//            lock.unlock();
//        }
//    }
//}
