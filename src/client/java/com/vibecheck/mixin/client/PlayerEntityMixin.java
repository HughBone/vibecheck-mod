package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.time.Instant;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerInterface {
    @Unique
    private Queue<Float> scaleQueue = new LinkedList<>();
    @Unique
    private long resetScaleTime = Long.MAX_VALUE;
    @Unique
    private float currentScale = 1.0f;
    @Unique
    private boolean hadResetRender = false;
    @Unique
    private boolean shouldRender = false;

    @Unique
    private float grewByValue = 0.0f;

    // About 60fps
    private static final float DELAY = 16;

    private long prevRenderTime = 0;

    // Prevent removing from queue twice
    private boolean lockRender = false;

    @Override
    public void clearLockRender() {
        lockRender = false;
    }

    @Override
    public void setCurrentScale() {
        if ((Instant.now().toEpochMilli() - prevRenderTime) < DELAY || lockRender || !shouldRender) {
            return;
        }
        // Only cancel renderUpdate after we have had the reset to 1.0f render
        if (this.hadResetRender) {
            this.shouldRender = false;
            return;
        }

        lockRender = true;
        prevRenderTime = Instant.now().toEpochMilli();

        if (scaleQueue.isEmpty()) {
            if (Instant.now().toEpochMilli() > this.resetScaleTime) {
                this.currentScale -= 0.05f;
                if (currentScale <= 1.0f) {
                    currentScale = 1.0f;
                    this.hadResetRender = true;
                }
            }
        } else {
            float newScale;
            try {
                newScale = scaleQueue.remove();
            } catch (NoSuchElementException e) {
                System.out.println("Failed the vibe check - NoSuchElementException");
                return;
            }
            this.grewByValue = newScale - currentScale;

            if (Math.abs(grewByValue) > 0.15) {
                if (grewByValue > 0) {
                    grewByValue = 0.15f;
                } else {
                    grewByValue = -0.15f;
                }
                currentScale += grewByValue;
            } else {
                currentScale = newScale;
            }
        }
    }

    @Override
    public float getCurrentScale() {
        if (!this.shouldRender) {
            return -1;
        }

        setCurrentScale();
        return currentScale;
    }

    @Override
    public void queueAdd(float audioScale, long time) {
        this.shouldRender = true;
        this.hadResetRender = false;
        this.resetScaleTime = time;

        if (scaleQueue.size() < 3) {
            scaleQueue.add(audioScale);
        } else {
            try {
                scaleQueue.clear();
                scaleQueue.add(audioScale);
            } catch (Exception e) {
                System.out.println("Failed the vibe check - NoSuchElementException PART 2");
            }
        }
    }

}
