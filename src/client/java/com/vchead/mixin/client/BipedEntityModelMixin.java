package com.vchead.mixin.client;

import com.vchead.HeadPlayer;
import com.vchead.VoiceChatHead;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.time.Instant;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow public abstract ModelPart getHead();

    private static boolean temp = false;

    @Inject(method = "animateModel(Lnet/minecraft/entity/LivingEntity;FFF)V", at = @At("HEAD"))
    public void animateModel(T livingEntity, float f, float g, float h, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
            float headScale = ((HeadPlayer) player).getHead();
            ModelPart head = this.getHead();
            if (headScale == 1.0f) {
                head.xScale = 1f;
                head.yScale = 1f;
                head.zScale = 1f;
                return;
            }
            head.xScale = headScale;
            head.yScale = headScale;
            head.zScale = headScale;

//            if (headScale == 1.0f) return;

//            if (Instant.now().toEpochMilli() > ((HeadPlayer) player).getTime()) {
//                ((HeadPlayer) player).setHead(1.0f);
//                head.xScale = 1.0f;
//                head.yScale = 1.0f;
//                head.zScale = 1.0f;
//                return;
//            }


//            head.xScale = head.xScale + 0.01f;
//            head.yScale = head.xScale + 0.01f;
//            head.zScale = head.xScale + 0.01f;
        }
    }
}