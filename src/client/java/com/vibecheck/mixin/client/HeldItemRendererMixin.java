package com.vibecheck.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.vibecheck.PlayerInterface;
import com.vibecheck.integration.AnimationEnum;
import com.vibecheck.integration.MyConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        float currentScale = ((PlayerInterface) MinecraftClient.getInstance().player).getCurrentScale();
        currentScale = (currentScale - 1.0f) / 2.5f;

        switch (MyConfig.handAnimation) {
            case AnimationEnum.SCALE:
                if (MyConfig.scaleMainHandToSound && hand == Hand.MAIN_HAND) {
                    matrices.translate(-currentScale, currentScale, currentScale);
                } else if (MyConfig.scaleOffHandToSound && hand == Hand.OFF_HAND) {
                    matrices.translate(currentScale, currentScale, currentScale);
                }
                break;
            case AnimationEnum.BOB_UP:
                if (MyConfig.scaleMainHandToSound || MyConfig.scaleOffHandToSound) {
                    matrices.translate(0, currentScale / 2, 0);
                }
                break;
            case AnimationEnum.BOB_DOWN:
                if (MyConfig.scaleMainHandToSound || MyConfig.scaleOffHandToSound) {
                    matrices.translate(0, -currentScale / 2, 0);
                }
                break;
        }
    }

    // Code yoinked from https://codeberg.org/MicrocontrollersDev/OverlayTweaks
    @WrapWithCondition(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
    private boolean removeHandSway(MatrixStack instance, Quaternionf quaternion) {
        return false;
    }

}