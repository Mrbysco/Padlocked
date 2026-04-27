package com.mrbysco.padlocked.datagen;

import com.mrbysco.padlocked.Padlocked;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class PadlockItemTagProvider extends ItemTagsProvider {
	public PadlockItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, Padlocked.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(Padlocked.KEYS).add(Items.TRIAL_KEY, Items.OMINOUS_TRIAL_KEY);
	}
}
