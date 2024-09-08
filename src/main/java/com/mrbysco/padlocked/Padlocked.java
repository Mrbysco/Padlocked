package com.mrbysco.padlocked;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

@Mod(Padlocked.MOD_ID)
public class Padlocked {
	public static final String MOD_ID = "padlocked";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final TagKey<Item> KEYS = TagKey.create(Registries.ITEM, modLoc("keys"));

	public Padlocked(IEventBus eventBus, Dist dist, ModContainer container) {
		NeoForge.EVENT_BUS.addListener(this::onRightClick);
	}

	private void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack().is(KEYS) && event.getItemStack().has(DataComponents.CUSTOM_NAME)) {
			String keyName = event.getItemStack().get(DataComponents.CUSTOM_NAME).getString();
			final Level level = event.getLevel();
			final BlockPos pos = event.getPos();
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof BaseContainerBlockEntity containerBlockEntity) {
				if(containerBlockEntity.lockKey == LockCode.NO_LOCK) {
					containerBlockEntity.lockKey = new LockCode(keyName);
					level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			}
		}
	}

	public static ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
