package com.vibecheck.mixin.client;

import com.vibecheck.PlayerInterface;
import com.vibecheck.VibeCheck;
import com.vibecheck.integration.AnimationEnum;
import com.vibecheck.integration.MyConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin extends BipedEntityModel<PlayerEntityRenderState> {

    public PlayerEntityModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V", at = @At("TAIL"))
    public void setAngles(PlayerEntityRenderState playerEntityRenderState, CallbackInfo ci) {
        PlayerEntity player = VibeCheck.playerRenderMap.get(playerEntityRenderState);
        if (player == null) return;
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
