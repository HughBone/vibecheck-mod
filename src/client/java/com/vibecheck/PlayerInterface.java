package com.vibecheck;

public interface PlayerInterface {
    void setCurrentScale();
    float getCurrentScale();
    void queueAdd(float audioScale, long time);
}
