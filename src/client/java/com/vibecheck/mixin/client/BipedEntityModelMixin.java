package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import com.vibecheck.integration.MyConfig;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow public abstract ModelPart getHead();

    @Inject(method = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateModel(Lnet/minecraft/entity/LivingEntity;FFF)V",
            at = @At("HEAD"))
    public void animateModel(T livingEntity, float f, float g, float h, CallbackInfo ci) {
        if (MyConfig.scaleHeadToSound && livingEntity instanceof PlayerEntity player) {
            float currentScale = ((PlayerInterface) player).getCurrentScale();
            if (currentScale == -1) {
                return;
            }
            ModelPart head = this.getHead();
            head.xScale = currentScale;
            head.yScale = currentScale;
            head.zScale = currentScale;
        }
    }

}