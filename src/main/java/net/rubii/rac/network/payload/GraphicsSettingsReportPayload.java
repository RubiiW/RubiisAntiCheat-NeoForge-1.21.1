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

public record GraphicsSettingsReportPayload(String shaderName, float caveLightingMultiplier, float gamma, boolean silent) implements CustomPacketPayload {

    public static final Type<GraphicsSettingsReportPayload> TYPE = new Type<>(ModChannels.REPORT_LIGHT_SETTINGS_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, GraphicsSettingsReportPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, GraphicsSettingsReportPayload::shaderName,
                    ByteBufCodecs.FLOAT, GraphicsSettingsReportPayload::caveLightingMultiplier,
                    ByteBufCodecs.FLOAT, GraphicsSettingsReportPayload::gamma,
                    ByteBufCodecs.BOOL, GraphicsSettingsReportPayload::silent,
                    GraphicsSettingsReportPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GraphicsSettingsReportPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                if (context.player() instanceof ServerPlayer player){
                    ServerNetworkHandler.handleGraphicsReport(payload, player);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}