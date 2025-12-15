package com.eyecommer.Backend.service;

import com.eyecommer.Backend.utils.SnapshotCancelReason;

public interface OrderSnapshotService {
    void confirmSnapshot(String orderCode);

    void cancelSnapshot(String orderCode, SnapshotCancelReason reason);
}
