package net.rubii.rac.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.rubii.rac.network.ModChannels;
import net.rubii.rac.network.ClientNetworkHandler;
import org.jetbrains.annotations.NotNull;

public record SettingsRequestPayload(boolean silent) implements CustomPacketPayload {

    public static final Type<SettingsRequestPayload> TYPE = new Type<>(ModChannels.REQUEST_SETTINGS_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SettingsRequestPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, SettingsRequestPayload::silent,
                    SettingsRequestPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SettingsRequestPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> ClientNetworkHandler.sendSettingsReport(payload.silent()));
    }
}
