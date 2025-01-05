package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import com.vibecheck.integration.MyConfig;
import com.vibecheck.integration.SquashAndStretchEnum;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Unique
    boolean playerFound = false;

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    ordinal = 0))
    public <E extends Entity, S extends EntityRenderState> void renderStart(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            float currentScale = ((PlayerInterface) abstractClientPlayerEntity).getCurrentScale();
            currentScale = (currentScale - 1.0f) / 2.5f;

            matrices.push();

            // Stretch
            switch (MyConfig.squashAndStretch) {
                case SquashAndStretchEnum.SQUASH:
                    matrices.scale(
                            1.0f + currentScale,
                            1.0f - currentScale,
                            1.0f + currentScale
                    );
                    break;
                case SquashAndStretchEnum.STRETCH:
                    matrices.scale(
                            1.0f - currentScale,
                            1.0f + currentScale,
                            1.0f - currentScale
                    );
                    break;
            }

            playerFound = true;
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    public <E extends Entity, S extends EntityRenderState> void renderEnd(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo ci) {
        if (playerFound) {
            matrices.pop();
            playerFound = false;
        }
    }

}
