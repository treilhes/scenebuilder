package com.gluonhq.jfxapps.boot.context;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MultipleProgressListener {

    private ProgressListener progressListener;
    private Map<UUID, Double> tasks = new ConcurrentHashMap<>();
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
            tasks.put(taskId, 0d);
        }
    }

    public void notifyProgress(UUID taskId, double progress) {
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
        tasks.put(taskId, 1d);
        progressListener.notifyProgress(computeProgress());
    }

    private double computeProgress() {
        int size = tasks.size();
        double sum = 0d;
        for (Double d:tasks.values()) {
            sum += d;
        }
        return sum / size;
    }
}
