package com.gluonhq.jfxapps.boot.api.context;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.gluonhq.jfxapps.boot.api.utils.ProgressListener;

public class MultipleProgressListener {

    private ProgressListener progressListener;
    private Map<UUID, Float> tasks = new ConcurrentHashMap<>();
    private final boolean inactive;

    public MultipleProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.inactive = progressListener == null;
    }

    public void notifyStart(UUID taskId) {
        if (inactive) {
            return;
        }
        if (!tasks.containsKey(taskId)) {
            tasks.put(taskId, 0f);
        }
    }

    public void notifyProgress(UUID taskId, float progress) {
        if (inactive) {
            return;
        }
        tasks.put(taskId, progress);
        progressListener.notifyProgress(computeProgress());
    }

    public void notifyDone(UUID taskId) {
        if (inactive) {
            return;
        }
        tasks.put(taskId, 1f);
        progressListener.notifyProgress(computeProgress());
    }

    private float computeProgress() {
        int size = tasks.size();
        float sum = 0f;
        for (Float d:tasks.values()) {
            sum += d;
        }
        return sum / size;
    }
}
