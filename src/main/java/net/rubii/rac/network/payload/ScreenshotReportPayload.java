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

public record ScreenshotReportPayload(byte[] image, boolean silent) implements CustomPacketPayload {

    public static final Type<ScreenshotReportPayload> TYPE = new Type<>(ModChannels.REPORT_SCREENSHOT_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenshotReportPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.byteArray(999999999), ScreenshotReportPayload::image,
                    ByteBufCodecs.BOOL, ScreenshotReportPayload::silent,
                    ScreenshotReportPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScreenshotReportPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player){
                try {
                    ServerNetworkHandler.handleScreenshotReport(payload, player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}