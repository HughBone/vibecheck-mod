package com.vibecheck.integration;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MyConfig {
    public static ConfigClassHandler<MyConfig> HANDLER = ConfigClassHandler.createBuilder(MyConfig.class)
            .id(Identifier.of("vibecheck", "vibecheck_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("vibecheck_config.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public static boolean scaleHeadToSound = true;

    @SerialEntry
    public static boolean scaleMainHandToSound = false;

    @SerialEntry
    public static boolean scaleOffHandToSound = false;

    @SerialEntry
    public static float scaleMultiplier = 1.0f;

    @SerialEntry
    public static AnimationEnum headAnimation = AnimationEnum.SCALE;

    @SerialEntry
    public static AnimationEnum handAnimation = AnimationEnum.SCALE;

    // Goofy toggles
    @SerialEntry
    public static SquashAndStretchEnum squashAndStretch = SquashAndStretchEnum.DISABLED;

    @SerialEntry
    public static boolean canada = false;

    @SerialEntry
    public static boolean chicken = false;

    // Getters methods for configuration settings
    public static Option<Boolean> getScaleHeadToSound() {
        return Option.<Boolean>createBuilder()
                .name(Text.of("Scale Head to Sound"))
                .binding(true, () -> MyConfig.scaleHeadToSound, newVal -> {
                    MyConfig.scaleHeadToSound = newVal;
                })
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    public static Option<Boolean> getScaleMainHandToSound() {
        return Option.<Boolean>createBuilder()
                .name(Text.of("Scale Main Hand to Sound"))
                .description(OptionDescription.of(Text.of("Note: This looks better with 'view bobbing' disabled.")))
                .binding(false, () -> MyConfig.scaleMainHandToSound, newVal -> MyConfig.scaleMainHandToSound = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    public static Option<Boolean> getScaleOffHandToSound() {
        return Option.<Boolean>createBuilder()
                .name(Text.of("Scale Off Hand to Sound"))
                .description(OptionDescription.of(Text.of("Note: This looks better with 'view bobbing' disabled.")))
                .binding(false, () -> MyConfig.scaleOffHandToSound, newVal -> MyConfig.scaleOffHandToSound = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    public static Option<Float> getScaleMultiplier() {
        return Option.<Float>createBuilder()
                .name(Text.of("Animation Strength"))
                .description(OptionDescription.of(Text.of("How strong or weak you want the audio animation to be.")))
                .binding(1f, () -> MyConfig.scaleMultiplier, newVal -> MyConfig.scaleMultiplier = newVal)
                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                        .range(0f, 5f)
                        .step(0.1f))
                .build();
    }

    public static Option<AnimationEnum> getHeadAnimation() {
        return Option.<AnimationEnum>createBuilder()
                .name(Text.of("Head Animation Type:"))
                .description(OptionDescription.of(Text.of("Animation based on audio level for player heads.")))
                .binding(AnimationEnum.SCALE, () -> MyConfig.headAnimation, newVal -> MyConfig.headAnimation = newVal)
                .controller(opt -> EnumControllerBuilder.create(opt)
                        .enumClass(AnimationEnum.class))
                .build();
    }

    public static Option<AnimationEnum> getHandAnimation() {
        return Option.<AnimationEnum>createBuilder()
                .name(Text.of("Hand Animation Type:"))
                .description(OptionDescription.of(Text.of("Animation based on audio level for player arms + held items in first person mode.")))
                .binding(AnimationEnum.SCALE, () -> MyConfig.handAnimation, newVal -> MyConfig.handAnimation = newVal)
                .controller(opt -> EnumControllerBuilder.create(opt)
                        .enumClass(AnimationEnum.class))
                .build();
    }

    public static Option<SquashAndStretchEnum> getSquashAndStretch() {
        return Option.<SquashAndStretchEnum>createBuilder()
                .name(Text.of("Squish mode:"))
                .binding(SquashAndStretchEnum.DISABLED, () -> MyConfig.squashAndStretch, newVal -> MyConfig.squashAndStretch = newVal)
                .controller(opt -> EnumControllerBuilder.create(opt)
                        .enumClass(SquashAndStretchEnum.class))
                .build();
    }

    public static Option<Boolean> getCanada() {
        return Option.<Boolean>createBuilder()
                .name(Text.of("Canada"))
                .binding(false, () -> MyConfig.canada, newVal -> MyConfig.canada = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    public static Option<Boolean> getChicken() {
        return Option.<Boolean>createBuilder()
                .name(Text.of("Chicken"))
                .binding(false, () -> MyConfig.chicken, newVal -> MyConfig.chicken = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }
}
