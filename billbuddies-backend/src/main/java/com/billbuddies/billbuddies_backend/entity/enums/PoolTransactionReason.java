package com.billbuddies.billbuddies_backend.entity.enums;

public enum PoolTransactionReason {
    DEPOSIT,    // member adds money to pool
    EXPENSE,    // pool pays for something
    ADJUSTMENT  // manual correction (future-safe)
}
