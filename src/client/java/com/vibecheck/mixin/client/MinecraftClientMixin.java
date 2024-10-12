package com.vibecheck.mixin.client;

import com.vibecheck.VibeCheck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin<T extends LivingEntity> {

    @Unique
    private long prevTime = -1;

    @Inject(method = "render", at = @At("HEAD"))
    public void renderHead(boolean tick, CallbackInfo ci) {
        if (prevTime != -1) {
            VibeCheck.timeSinceLastRender += (int) (Instant.now().toEpochMilli() - prevTime);
        }
        prevTime = Instant.now().toEpochMilli();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderTail(boolean tick, CallbackInfo ci) {
        VibeCheck.lockRender = false;
    }
}
