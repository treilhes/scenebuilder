package com.gluonhq.jfxapps.core.api.job;

public interface Job {
    public void execute();
    public void undo();
    public void redo();
    public boolean isExecutable();
    public String getDescription();
}
