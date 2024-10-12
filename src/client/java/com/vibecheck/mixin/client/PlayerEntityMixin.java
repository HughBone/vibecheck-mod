package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.time.Instant;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerInterface {
    @Shadow public abstract String getNameForScoreboard();

    @Unique
    private Queue<Float> scaleQueue = new LinkedList<>();
    @Unique
    private long resetScaleTime = Long.MAX_VALUE;
    @Unique
    private float currentScale = 1.0f;

    // Delay of 16 is about 60fps
    @Unique
    private static final float DELAY = 16;
    @Unique
    private long prevRenderTime = 0;
    // Prevent removing from queue twice in one render for this player
    @Unique
    private boolean lockScaleUpdate = false;

    @Override
    public void clearLockRender() {
        lockScaleUpdate = false;
    }

    @Override
    public void setCurrentScale() {
        if (lockScaleUpdate || (Instant.now().toEpochMilli() - prevRenderTime) < DELAY) {
            return;
        }

        // Lock until next render frame
        lockScaleUpdate = true;

        if (scaleQueue.isEmpty()) {
            if (currentScale != 1.0f && Instant.now().toEpochMilli() > this.resetScaleTime) {
                this.currentScale -= 0.05f;
                if (currentScale < 1.0f) {
                    currentScale = 1.0f;
                }
                prevRenderTime = Instant.now().toEpochMilli();
            }
        } else {
            prevRenderTime = Instant.now().toEpochMilli();
            float newScale;
            try {
                newScale = scaleQueue.remove();
            } catch (NoSuchElementException e) {
                System.out.println("Failed the vibe check - NoSuchElementException");
                return;
            }

            // Smooth jittery scaling / cap value
            float grewByValue = newScale - currentScale;
            if (Math.abs(grewByValue) > 0.15) {
                if (grewByValue > 0) {
                    currentScale += 0.15f;
                } else {
                    currentScale -= 0.15f;
                }
            } else {
                currentScale = newScale;
            }
        }
    }

    @Override
    public float getCurrentScale() {
        setCurrentScale();
        return currentScale;
    }

    @Override
    public void queueAdd(float audioScale, long time) {
        this.resetScaleTime = time;

        if (scaleQueue.size() < 3) {
            scaleQueue.add(audioScale);
        } else {
            // Framerate too slow! Clear queue and start at new scale
            try {
                scaleQueue.clear();
                scaleQueue.add(audioScale);
            } catch (Exception e) {
                System.out.println("Failed the vibe check - NoSuchElementException PART 2");
            }
        }
    }

}
