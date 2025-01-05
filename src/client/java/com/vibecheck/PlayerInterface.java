package com.vibecheck;

public interface PlayerInterface {
    void clearLockRender();
    void setCurrentScale();
    float getCurrentScale();
    void queueAdd(float audioScale);
}
