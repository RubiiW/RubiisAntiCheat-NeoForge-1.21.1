package net.triblock.bng.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.triblock.bng.BoltsNGears;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BoltsNGears.MODID);

    public static final DeferredItem<ArmorItem> HAZMAT_HOOD = ITEMS.register("hazmat_hood",
            () -> new ArmorItem(ModArmorMaterials.HAZMAT_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(20))));

    public static final DeferredItem<ArmorItem> HAZMAT_SUIT = ITEMS.register("hazmat_suit",
            () -> new ArmorItem(ModArmorMaterials.HAZMAT_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(20))));

    public static final DeferredItem<ArmorItem> HAZMAT_PANTS = ITEMS.register("hazmat_pants",
            () -> new ArmorItem(ModArmorMaterials.HAZMAT_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(20))));

    public static final DeferredItem<ArmorItem> HAZMAT_BOOTS = ITEMS.register("hazmat_boots",
            () -> new ArmorItem(ModArmorMaterials.HAZMAT_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(20))));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
