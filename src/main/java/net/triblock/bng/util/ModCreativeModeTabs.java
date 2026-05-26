package net.triblock.bng.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.triblock.bng.BoltsNGears;
import net.triblock.bng.item.ModItems;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BoltsNGears.MODID);

    private static ItemStack icon(){
        return new ItemStack(ModItems.ITEM_VARIABLE_NAME.get());
    }

    public static final Supplier<CreativeModeTab> BOLTS_N_GEARS_TAB = CREATIVE_MODE_TAB.register("bng_tab",
            () -> CreativeModeTab.builder()
                    .icon(ModCreativeModeTabs::icon)
                    .title(Component.literal("Bolts 'N' Gears"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.ITEM_VARIABLE_NAME);
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
