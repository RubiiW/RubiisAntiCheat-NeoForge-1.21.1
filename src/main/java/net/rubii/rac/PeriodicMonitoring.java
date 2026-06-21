package net.rubii.rac;

import net.irisshaders.iris.Iris;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.ModList;
import net.rubii.rac.network.ClientNetworkHandler;

import java.text.DecimalFormat;
import java.time.format.DecimalStyle;

@EventBusSubscriber
public class PeriodicMonitoring {

    private static int ticks = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!ModList.get().isLoaded("iris")) return;
        if (Minecraft.getInstance().level == null) return;
        if (!Config.ENABLE_PERIODIC_MONITORING.get()) return;

        ticks++;

        if (ticks % Config.PERIODIC_MONITORING_INTERVAL.get() == 0) {
            check();
        }
    }

    private static void check() {
        Minecraft instance = Minecraft.getInstance();
        if (instance.player == null) return;

        ClientNetworkHandler.sendSettingsReport(true);
    }
}
