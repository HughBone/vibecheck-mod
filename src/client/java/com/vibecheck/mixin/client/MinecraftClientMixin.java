package com.vibecheck.mixin.client;

import com.vibecheck.VibeCheck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin<T extends LivingEntity> {

    @Inject(method = "render", at = @At("TAIL"))
    public void renderTail(boolean tick, CallbackInfo ci) {
        VibeCheck.lockRender = false;
    }
}
