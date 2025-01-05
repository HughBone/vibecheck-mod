package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import com.vibecheck.VibeCheck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
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
    private float prevScale = 1.0f;

    // Delay of 16 is about 60fps
    @Unique
    private static final float DELAY = 16.0f;
    @Unique
    private long prevRenderTime = 0;
    // Prevent removing from queue twice in one render for this player
    @Unique
    private boolean lockScaleUpdate = false;
    @Unique
    private boolean prevGoingUp = true;

    @Override
    public void clearLockRender() {
        lockScaleUpdate = false;
    }

    @Override
    public void setCurrentScale() {
        if (lockScaleUpdate) {
            return;
        }

        // IDK why i put this above the lock but im too scared to change it
        if ((Instant.now().toEpochMilli() - prevRenderTime) < DELAY) {
            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
            prevScale = MathHelper.lerp(tickDelta * VibeCheck.audioTickDelta, prevScale, currentScale);
            return;
        }

        // Lock until next render frame
        lockScaleUpdate = true;

        if (scaleQueue.isEmpty()) {
            if (currentScale != 1.0f) {
                prevScale = currentScale;
                prevRenderTime = Instant.now().toEpochMilli();
                if (Instant.now().toEpochMilli() > this.resetScaleTime) {
                    this.currentScale -= 0.05f;
                    if (currentScale <= 1.0f) {
                        currentScale = 1.0f;
                        prevGoingUp = true;
                    }
                } else if (prevGoingUp) {
                    currentScale += 0.005f;
                } else {
                    currentScale -= 0.005f;
                }
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

            prevScale = currentScale;

            // Smooth scaling / fix jitteryness.
            // limit small values and cap max value
            float grewByValue = newScale - currentScale;
            float absGrewVal = Math.abs(grewByValue);
            if (absGrewVal > 0.02) {
                if (absGrewVal > 0.15) {
                    if (grewByValue > 0) {
                        currentScale += 0.15f;
                    } else {
                        currentScale -= 0.15f;
                    }
                } else {
                    currentScale = newScale;
                }
            } else if (prevGoingUp) {
                currentScale += 0.005f;
            } else {
                currentScale -= 0.005f;
            }

            prevGoingUp = newScale > prevScale;
        }

        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        prevScale = MathHelper.lerp(tickDelta * VibeCheck.audioTickDelta, prevScale, currentScale);
    }

    @Override
    public float getCurrentScale() {
        setCurrentScale();

        if (prevScale <= 1.001f) {
            prevScale = 1.0f;
        }

        return prevScale;
    }

    @Override
    public void queueAdd(float audioScale) {
        this.resetScaleTime = Instant.now().toEpochMilli() + 100;

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