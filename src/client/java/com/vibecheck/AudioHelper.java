package com.vibecheck;

public class AudioHelper {
    private long timeSinceLastAudio = -1;

    public long delay = 0;
    public float scale = 1.0f;
    public boolean replaceMe = true;
    public boolean startSquashed = true;

    // Update delay based on how long it took to receive the last audio packet
    public void updateTimeSinceLastAudio(long currentTime) {
        long newDelay = currentTime - timeSinceLastAudio;
        if (newDelay > 250) {
            startSquashed = true;
        }

        if (this.timeSinceLastAudio == -1 || newDelay > 20) {
            // 5 looks reaaly smooth here
            // Was going to do 20, but that looks worse
            this.delay = 5;
        } else {
            this.delay = newDelay;
        }

        this.replaceMe = false;
        this.timeSinceLastAudio = currentTime;
    }
}
