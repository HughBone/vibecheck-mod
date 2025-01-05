package com.vibecheck.mixin.client;

import com.vibecheck.AudioHelper;
import com.vibecheck.PlayerInterface;
import com.vibecheck.VibeCheck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerInterface {

    @Unique
    private long resetScaleTime = Long.MAX_VALUE;
    @Unique
    private float currentScale = 1.0f;

    // Prevent removing from queue twice in one render for this player
    @Unique
    private boolean waitNextFrame = false;

    private int prevTick = -1;

    //    @Unique
//    private static long DELAY = 20;
    @Unique
    AudioHelper audio = new AudioHelper();
    private long animationStartTime = -1;
    private long animationEndTime = -1;

    @Override
    public void clearLockRender() {
        waitNextFrame = false;
    }

    @Override
    public void setCurrentScale() {
        if (waitNextFrame) {
            return;
        }
        // Lock until next render frame
        waitNextFrame = true;

        if (audio.replaceMe) {
            if (currentScale != 1.0f && System.currentTimeMillis() > this.resetScaleTime) {
                if (currentScale > 1.0f) {
                    currentScale -= 0.05f;
                    if (currentScale < 1.0f) {
                        currentScale = 1.0f;
                    }
                } else {
                    currentScale += 0.05f;
                    if (currentScale > 1.0f) {
                        currentScale = 1.0f;
                    }
                }
            }
            return;
        }

        // Do a lil squash animation
        if (audio.startSquashed) {
            int tick = VibeCheck.tickTracker;
            if (prevTick != tick) {
                if (tick >= 5) {
                    VibeCheck.tickTracker = 0;
                }

                float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

                if (tick < 2) {
                    float progress = (tick + tickDelta) / 2;
                    currentScale = MathHelper.lerp(progress, currentScale, 0.8f);
                } else if (tick < 8) {
                    float progress = (tick + tickDelta) / 6;
                    currentScale = MathHelper.lerp(progress, currentScale, 1.05f);
                    audio.startSquashed = false;
                }

                prevTick = tick;
            }

            return;
        }

        // Animation started
        if (animationStartTime == -1) {
            animationStartTime = System.currentTimeMillis();
        }

        long currentTime = System.currentTimeMillis();
        animationEndTime = animationStartTime + audio.delay;
        float elapsedTime = currentTime - animationStartTime;
        float progress = 1.0f - (elapsedTime / (animationEndTime - animationStartTime));

        // Animation is done
        if (currentTime >= animationEndTime) {
            animationStartTime = -1;
            audio.replaceMe = true;
            return;
        }

        // Smooth jittery scaling / cap value
        float newScale = audio.scale;
        float grewByValue = newScale - currentScale;
        if (Math.abs(grewByValue) > 0.07) {
            if (grewByValue > 0) {
                newScale = currentScale + 0.07f;
            } else {
                newScale = currentScale - 0.07f;
            }
        }

        currentScale = MathHelper.lerp(progress, currentScale, newScale);
    }

    @Override
    public float getCurrentScale() {
        setCurrentScale();
        return currentScale;
    }

    @Override
    public void queueAdd(float audioScale) {
        if (audio.replaceMe || (Math.abs(audio.scale - audioScale)) > 0.01f) {
            long currentTime = System.currentTimeMillis();
            audio.scale = audioScale;
            audio.updateTimeSinceLastAudio(currentTime);
            this.resetScaleTime = currentTime + 75;
        }
    }
}
