package com.pokemonreview.api.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorService {


    public enum IdentityType {
        USER,
        PROFILE,
        FRIEND
    }

    private static final AtomicLong counter = new AtomicLong(0);


    public static long generateNewId(IdentityType identityType) throws Exception {
        long shardId = getShardId(); // Lấy shardId từ cấu hình hệ thống
        long reference = 0L; // Tham chiếu mặc định
        long sequence = new Date().getTime() - getDefaultTime(); // Sequence dựa trên thời gian hiện tại

        return getObjectId(identityType, shardId, reference, sequence);
    }


    private static long getObjectId(IdentityType identityType, long shardId, long reference, long sequence) {
        // Mã hóa loại định danh dựa trên ordinal của IdentityType
        long identityCode = identityType.ordinal() & 0xFF; // Lấy giá trị tối đa 255

        // Lấy giá trị counter duy nhất
        long uniqueCounter = counter.incrementAndGet() & 0xFFFFFF; // Counter lưu tối đa 24 bit (16 triệu giá trị)

        // Tổng hợp ID dựa trên các tham số:
        // Cấu trúc: | 8-bit identityCode | 12-bit shardId | 24-bit uniqueCounter | 20-bit timestamp-based sequence |
        return (identityCode << 56)         // Identity code (8-bit)
                | ((shardId & 0xFFF) << 44) // Shard ID (12-bit)
                | ((uniqueCounter & 0xFFFFFF) << 20) // Unique counter (24-bit)
                | (sequence & 0xFFFFF);     // Sequence based on timestamp (20-bit)
    }


    private static long getShardId() {
        // Giả định đây là shardId được lấy từ cấu hình hệ thống
        return 1L;
    }


    private static long getDefaultTime() {
        // Giả định đây là thời điểm ban đầu từ cấu hình hệ thống (01-01-2020)
        return 1577836800000L; // 01-01-2020 (epoch time in milliseconds)
    }
}