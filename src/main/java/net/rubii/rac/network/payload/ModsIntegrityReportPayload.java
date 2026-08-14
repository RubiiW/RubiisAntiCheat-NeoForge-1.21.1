package net.rubii.rac.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.rubii.rac.network.ModChannels;
import net.rubii.rac.network.ServerNetworkHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public record ModsIntegrityReportPayload(String modFilesList, String modHashList, boolean silent) implements CustomPacketPayload {

    public static final Type<ModsIntegrityReportPayload> TYPE = new Type<>(ModChannels.REPORT_MODS_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModsIntegrityReportPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ModsIntegrityReportPayload::modFilesList,
                    ByteBufCodecs.STRING_UTF8, ModsIntegrityReportPayload::modHashList,
                    ByteBufCodecs.BOOL, ModsIntegrityReportPayload::silent,
                    ModsIntegrityReportPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ModsIntegrityReportPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player){
                try {
                    ServerNetworkHandler.handleModsIntegrityReport(payload, player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}