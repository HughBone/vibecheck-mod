package com.vibecheck.integration;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum AnimationEnum implements NameableEnum {
    SCALE("Scale"),
    BOB_UP("Bob up"),
    BOB_DOWN("Bob down");

    public final String value;

    AnimationEnum(String value) {
        this.value = value;
    }

    @Override
    public Text getDisplayName() {
        return Text.of(this.value);
    }
}
