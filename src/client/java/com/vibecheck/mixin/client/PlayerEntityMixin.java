package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import com.vibecheck.VibeCheck;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerInterface {
    @Unique
    private Queue<Float> scaleQueue = new LinkedList<>();
    @Unique
    private long lastUpdateTime = Long.MAX_VALUE;
    @Unique
    private float currentScale = 1.0f;
    @Unique
    private boolean hadResetRender = false;
    @Unique
    private boolean shouldRender = false;

    @Override
    public void setCurrentScale() {
        if (VibeCheck.lockRender || !this.shouldRender) {
            return;
        }
        // Only cancel renderUpdate after we have had the reset to 1.0f render
        if (this.hadResetRender) {
            this.shouldRender = false;
            return;
        }

        VibeCheck.lockRender = true;

        if (scaleQueue.isEmpty()) {
            if (Instant.now().toEpochMilli() > this.lastUpdateTime) {
                this.currentScale = 1.0f;
                this.hadResetRender = true;
            }
        } else {
            this.currentScale = scaleQueue.remove();
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
        this.lastUpdateTime = time;

        if (scaleQueue.size() < 4) {
            scaleQueue.add(audioScale);
        }
    }

}
