package com.mrbysco.padlocked;

import com.mojang.logging.LogUtils;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(Padlocked.MOD_ID)
public class Padlocked {
	public static final String MOD_ID = "padlocked";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final TagKey<Item> KEYS = TagKey.create(Registries.ITEM, modLoc("keys"));

	public static final DeferredRegister<DataComponentPredicate.Type<?>> COMPONENT_PREDICATES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE, MOD_ID);

	public static final Supplier<DataComponentPredicate.Type<CustomNamePredicate>> CUSTOM_NAME = COMPONENT_PREDICATES.register("custom_name", () -> new DataComponentPredicate.ConcreteType<>(CustomNamePredicate.CODEC));

	public Padlocked(IEventBus eventBus) {
		COMPONENT_PREDICATES.register(eventBus);

		NeoForge.EVENT_BUS.addListener(this::onRightClick);
	}

	private void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != InteractionHand.MAIN_HAND) return;
		ItemStack itemStack = event.getItemStack();
		if (itemStack.is(KEYS) && itemStack.has(DataComponents.CUSTOM_NAME)) {
			final Player player = event.getEntity();
			final Level level = event.getLevel();
			final BlockPos pos = event.getPos();
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof BaseContainerBlockEntity containerBlockEntity) {
				LockCode code = containerBlockEntity.lockKey;
				if (code == LockCode.NO_LOCK) {
					if (player.isShiftKeyDown()) return;
					HolderGetter<Item> itemLookup = level.holderLookup(Registries.ITEM);
					containerBlockEntity.lockKey = new LockCode(getPredicate(itemStack, itemLookup));
					level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
				} else {
					if (player.isShiftKeyDown() && code.unlocksWith(itemStack)) {
						containerBlockEntity.lockKey = LockCode.NO_LOCK;
						level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
						player.displayClientMessage(Component.translatable("padlocked.message.unlocked", containerBlockEntity.getDisplayName()), true);
					}
				}
			}
		}
	}

	private static ItemPredicate getPredicate(ItemStack stack, HolderGetter<Item> itemLookup) {
		Component customName = stack.get(DataComponents.CUSTOM_NAME);
		return ItemPredicate.Builder.item()
				.of(itemLookup, stack.getItem())
				.withComponents(DataComponentMatchers.Builder.components()
						.partial(CUSTOM_NAME.get(), new CustomNamePredicate(customName)).build())
				.build();
	}

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
