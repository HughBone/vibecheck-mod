package com.vibecheck.integration;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum SquashAndStretchEnum implements NameableEnum {
    SQUASH("Squash"),
    STRETCH("Stretch"),
    DISABLED("Disabled");

    public final String value;

    SquashAndStretchEnum(String value) {
        this.value = value;
    }

    @Override
    public Text getDisplayName() {
        return Text.of(this.value);
    }
}
