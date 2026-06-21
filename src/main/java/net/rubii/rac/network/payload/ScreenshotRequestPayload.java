package net.rubii.rac.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.rubii.rac.network.ClientNetworkHandler;
import net.rubii.rac.network.ModChannels;
import net.rubii.rac.utils.Utils;
import org.jetbrains.annotations.NotNull;

public record ScreenshotRequestPayload(boolean silent) implements CustomPacketPayload {

    public static final Type<ScreenshotRequestPayload> TYPE = new Type<>(ModChannels.REQUEST_SCREENSHOT_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenshotRequestPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ScreenshotRequestPayload::silent,
                    ScreenshotRequestPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScreenshotRequestPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> ClientNetworkHandler.sendScreenshotReport(payload.silent()));
    }
}
