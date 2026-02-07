package com.mrbysco.padlocked;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record CustomNamePredicate(Component customName) implements SingleComponentItemPredicate<Component> {
	public static final Codec<CustomNamePredicate> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							ComponentSerialization.CODEC.fieldOf("custom_name").forGetter(CustomNamePredicate::customName))
					.apply(instance, CustomNamePredicate::new)
	);

	@Override
	public DataComponentType<Component> componentType() {
		return DataComponents.CUSTOM_NAME;
	}

	@Override
	public boolean matches(Component value) {
		return customName.getString().equals(value.getString());
	}
}
