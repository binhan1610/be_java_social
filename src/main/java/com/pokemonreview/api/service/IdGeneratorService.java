package com.pokemonreview.api.service;

import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorService {

    public enum IdentityType {
        BUILDING,      // 0
        ROOM,          // 1
        TENANT,        // 2
        CONTRACT,      // 3
        SERVICE,       // 4
        METER_READING, // 5
        INVOICE,       // 6
        WORKSPACE,     // 7
        USER           // 8
    }

    private static final AtomicLong counter = new AtomicLong(0);

    // Epoch: 2020-01-01 00:00:00 UTC (1577836800000L)
    private static final long DEFAULT_TIME = 1577836800000L;

    public static long generateNewId(IdentityType identityType) {
        long shardId = getShardId();
        long sequence = System.currentTimeMillis() - DEFAULT_TIME;

        return getObjectId(identityType, shardId, sequence);
    }

    private static long getObjectId(IdentityType identityType, long shardId, long sequence) {
        // identityCode (8-bit): Tối đa 255 loại bảng
        long identityCode = identityType.ordinal() & 0xFF;

        // uniqueCounter (14-bit): Tối đa 16,383 ID mỗi mili giây (Tránh tràn 64 bit)
        long uniqueCounter = counter.incrementAndGet() & 0x3FFF;

        // Tối ưu cấu trúc 64-bit ID:
        // [1 bit dấu][8 bit Type][10 bit Shard][31 bit Timestamp][14 bit Counter]
        return (identityCode << 55)            // Identity code (Bit 55-62)
                | ((shardId & 0x3FF) << 45)    // Shard ID (Bit 45-54) - Tối đa 1024 Shards
                | ((sequence & 0x7FFFFFFF) << 14) // Timestamp (31-bit) - Dùng được ~68 năm
                | (uniqueCounter & 0x3FFF);    // Counter (14-bit)
    }

    private static long getShardId() {
        // Có thể lấy từ Environment Variable hoặc Config file
        return 1L;
    }
}