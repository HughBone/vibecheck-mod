package com.vibecheck.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.text.Text;


public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Text.of("Vibe Check"))
                .save(() -> MyConfig.HANDLER.save())
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("General"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Scale Head to Sound"))
                                .binding(true, () -> MyConfig.scaleHeadToSound, newVal -> {
                                    MyConfig.scaleHeadToSound = newVal;
                                })
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Scale Main Hand to Sound"))
                                .description(OptionDescription.of(Text.of("Note: This looks better with 'view bobbing' disabled.")))
                                .binding(false, () -> MyConfig.scaleMainHandToSound, newVal -> MyConfig.scaleMainHandToSound = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Scale Off Hand to Sound"))
                                .description(OptionDescription.of(Text.of("Note: This looks better with 'view bobbing' disabled.")))
                                .binding(false, () -> MyConfig.scaleOffHandToSound, newVal -> MyConfig.scaleOffHandToSound = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Float>createBuilder()
                                .name(Text.of("Scale Strength"))
                                .description(OptionDescription.of(Text.of("How strong or weak you want the scaling to be.")))
                                .binding(1f, () -> MyConfig.scaleMultiplier, newVal -> MyConfig.scaleMultiplier = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                    .range(0f, 5f)
                                    .step(0.1f))
                                .build())
                    .build())
            .build()
            .generateScreen(parentScreen);
    }

}
