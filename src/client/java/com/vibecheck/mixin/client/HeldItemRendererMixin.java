package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import com.vibecheck.integration.MyConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        float currentScale = ((PlayerInterface) player).getCurrentScale();

        float newScale = (currentScale - 1.0f) / 2.5f;

        if (MyConfig.scaleMainHandToSound && hand == Hand.MAIN_HAND) {
            matrices.translate(-newScale, newScale, newScale);
        } else if (MyConfig.scaleOffHandToSound && hand == Hand.OFF_HAND) {
            matrices.translate(newScale, newScale, newScale);
        }
    }
}
