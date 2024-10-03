package com.vchead.mixin.client;

import com.vchead.HeadPlayer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements HeadPlayer {
    @Unique
    private float head = 1.0f;

    @Unique
    private long lastUpdateTime = Long.MAX_VALUE;

    public float getHead() {
        return this.head;
    }

    public void setHead(float head) {
        this.head = head;
    }

    public void setTime(long time) {
        this.lastUpdateTime = time;
    }

    public long getTime() {
        return this.lastUpdateTime;
    }
}
