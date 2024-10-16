package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import com.vibecheck.integration.AnimationEnum;
import com.vibecheck.integration.MyConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At("TAIL"))
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
            float currentScale = ((PlayerInterface) player).getCurrentScale();

            ModelPart head = this.head;
            head.xScale = 1.0f;
            head.yScale = 1.0f;
            head.zScale = 1.0f;
            if (MyConfig.scaleHeadToSound) {
                switch (MyConfig.headAnimation) {
                    case AnimationEnum.SCALE:
                        head.xScale = currentScale;
                        head.yScale = currentScale;
                        head.zScale = currentScale;
                        break;
                    case AnimationEnum.BOB_UP:
                        head.pitch -= (currentScale - 1.0f);
                        break;
                    case AnimationEnum.BOB_DOWN:
                        head.pitch += (currentScale - 1.0f);
                        break;
                }
            }

            if (currentScale != 1.0f && !(MinecraftClient.getInstance().player == player
                    && MinecraftClient.getInstance().options.getPerspective().isFirstPerson())
            ) {
                if (MyConfig.chicken) {
                    leftArm.roll -= (currentScale - 1.0f);
                    rightArm.roll += (currentScale - 1.0f);
                }
                if (MyConfig.canada) {
                    head.pivotY -= (currentScale - 1.0f) * 10f;
                }
            }
        }
    }
}