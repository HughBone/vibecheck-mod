package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin<T extends LivingEntity> {

    @Inject(method = "render", at = @At("TAIL"))
    public void renderEnd(boolean tick, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world != null) {
            for (PlayerEntity p : MinecraftClient.getInstance().world.getPlayers()) {
                ((PlayerInterface) p).clearLockRender();
            }
        }
    }
}
