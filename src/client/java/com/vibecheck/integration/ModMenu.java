package com.vibecheck.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import net.minecraft.text.Text;


public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
            .title(Text.of("Vibe Check"))
            .save(() -> MyConfig.HANDLER.save())
            .category(ConfigCategory.createBuilder()
                .name(Text.of("General"))
                .group(OptionGroup.createBuilder()
                        .name(Text.of("Misc."))
                        .option(MyConfig.getSendPackets())
                        .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.of("Head options"))
                    .option(MyConfig.getScaleHeadToSound())
                    .option(MyConfig.getHeadAnimation())
                .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.of("Hand options"))
                    .option(MyConfig.getScaleMainHandToSound())
                    .option(MyConfig.getScaleOffHandToSound())
                    .option(MyConfig.getHandAnimation())
                .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.of("Misc. options"))
                    .option(MyConfig.getScaleMultiplier())
                .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.of("Goofy animations"))
                    .option(MyConfig.getSquashAndStretch())
                    .option(MyConfig.getChicken())
                    .option(MyConfig.getCanada())
                .build())
            .build())
        .build()
        .generateScreen(parentScreen);
    }

}
