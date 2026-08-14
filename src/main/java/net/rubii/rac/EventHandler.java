package net.rubii.rac;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rubii.rac.network.payload.GraphicsSettingsRequestPayload;
import net.rubii.rac.network.payload.ModFilesLoggingRequestPayload;
import net.rubii.rac.network.payload.ModsIntegrityRequestPayload;
import net.rubii.rac.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class EventHandler {

    private static int ticks = 0;

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            List<String> values = new ArrayList<>();

            for (Integer value : Config.CAVE_LIGHT_MULTIPLIER_VALUES.get()){
                values.add(value.toString());
            }

            PacketDistributor.sendToPlayer(player, new GraphicsSettingsRequestPayload(
                    Utils.encodeList(Config.CAVE_LIGHT_MULTIPLIER_NAMES.get()),
                    Utils.encodeList(values),
                    true
            ));

            PacketDistributor.sendToPlayer(player, new ModFilesLoggingRequestPayload(true));
            PacketDistributor.sendToPlayer(player, new ModsIntegrityRequestPayload(true));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (!Config.ENABLE_PERIODIC_MONITORING.get()) return;

            ticks++;

            if (ticks % Config.PERIODIC_MONITORING_INTERVAL.get() == 0) {
                List<String> values = new ArrayList<>();

                for (Integer value : Config.CAVE_LIGHT_MULTIPLIER_VALUES.get()){
                    values.add(value.toString());
                }

                PacketDistributor.sendToPlayer(player, new GraphicsSettingsRequestPayload(
                        Utils.encodeList(Config.CAVE_LIGHT_MULTIPLIER_NAMES.get()),
                        Utils.encodeList(values),
                        true
                ));
            }
        }
    }
}
