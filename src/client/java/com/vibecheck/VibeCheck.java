package com.vibecheck;

import com.vibecheck.integration.MyConfig;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientReceiveSoundEvent;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class VibeCheck implements VoicechatPlugin {

    public static HashMap<PlayerEntityRenderState, PlayerEntity> playerRenderMap = new HashMap<>();
    public static float AudioFPS = 18f;
    public static float audioTickDelta = (AudioFPS / 50.0f);

    @Override
    public String getPluginId() {
        return "vibecheck";
    }

    @Override
    public void initialize(VoicechatApi api) {
        MyConfig.HANDLER.load();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        // Audio from other players in group
        registration.registerEvent(ClientReceiveSoundEvent.StaticSound.class, event -> receivePlayerAudio(event.getRawAudio(), event.getId()));
        // Audio from other players
        registration.registerEvent(ClientReceiveSoundEvent.EntitySound.class, event -> receivePlayerAudio(event.getRawAudio(), event.getId()));
        // Audio from client
        registration.registerEvent(ClientSoundEvent.class, event -> {
            receivePlayerAudio(event.getRawAudio(), null);
            if (!MyConfig.sendAudioPackets) {
                event.cancel();
            }
        });
    }

    public void receivePlayerAudio(short[] rawAudio, UUID playerId) {
        if (playerId != null) {
            System.out.println("playerId is not null :O. If is from client, CONCERNING.: " + playerId.toString());
        }
        if (MinecraftClient.getInstance().world == null) return;

        PlayerEntity player = playerId == null ? MinecraftClient.getInstance().player
                : MinecraftClient.getInstance().world.getPlayerByUuid(playerId);
        if (player == null) return;

        float audioLevel = calculateAudioLevel(rawAudio);
        if (audioLevel == 1.0f) return;

        ((PlayerInterface) player).queueAdd(audioLevel);
    }

    public float calculateAudioLevel(short[] samples) {
        // find root mean square (RMS) amplitude
        // code yoinked from https://github.com/henkelmax/voicechat-interaction/blob/master/src/main/java/de/maxhenkel/vcinteraction/AudioUtils.java
        double rms = 0D;

        for (short value : samples) {
            if (value == 0) continue;
            double sample = (double) value / (double) Short.MAX_VALUE;
            rms += sample * sample;
        }

        int sampleCount = samples.length / 2;
        rms = 1.0D + ((sampleCount == 0) ? 0 : Math.sqrt(rms / sampleCount));

        // If the value is 1-ish, return as is.
        if (rms <= 1.01f) {
            return 1.0f;
        }

        // Calculate a scaling factor that increases with distance from 1 based on the steepness parameter.
        float scaleFactor = (float) Math.abs(Math.log(rms));

        // Adjust the sign of the scaling factor based on whether the original value was greater or less than 1.
        return 1.0f + (scaleFactor * MyConfig.scaleMultiplier);
    }

}
