package net.triblock.bng.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.triblock.bng.BoltsNGears;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoltsNGears.MODID);

    public static final DeferredItem<Item> ITEM_VARIABLE_NAME = ITEMS.register("item_id",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
