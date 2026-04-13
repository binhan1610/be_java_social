package com.pokemonreview.api.service;

import lombok.Getter;
import java.util.concurrent.atomic.AtomicLong;

public class IdGeneratorService {

    // 1. Thêm int prefix vào Enum
    @Getter
    public enum IdentityType {
        WORKSPACE(1),
        BUILDING(2),
        ROOM(3),
        TENANT(4),
        CONTRACT(5),
        SERVICE(6),
        METER_READING(7),
        INVOICE(8),
        USER(9);

        private final int prefix;

        IdentityType(int prefix) {
            this.prefix = prefix;
        }
    }

    private static final AtomicLong counter = new AtomicLong(0);
    private static final long DEFAULT_TIME = 1577836800000L; // Epoch 2020

    public static long generateNewId(IdentityType identityType) {
        long shardId = getShardId();
        long sequence = (System.currentTimeMillis() - DEFAULT_TIME) / 1000;

        return getObjectId(identityType.getPrefix(), shardId, sequence);
    }

    private static long getObjectId(int prefix, long shardId, long sequence) {
        long uniqueCounter = counter.incrementAndGet() & 0xFFF;


        return ((long) (prefix & 0x7F) << 56)
                | ((shardId & 0x3FF) << 46)             // Shard ID (10-bit)
                | ((sequence & 0x3FFFFFFFFL) << 12)     // Timestamp (34-bit) ~ 500 năm
                | (uniqueCounter & 0xFFF);              // Counter (12-bit)
    }

    private static long getShardId() {
        return 1L;
    }
}