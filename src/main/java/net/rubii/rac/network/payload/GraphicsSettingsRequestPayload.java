package net.rubii.rac.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rubii.rac.network.ModChannels;
import net.rubii.rac.network.ClientNetworkHandler;
import org.jetbrains.annotations.NotNull;

public record GraphicsSettingsRequestPayload(String namesList, String valuesList, boolean silent) implements CustomPacketPayload {

    public static final Type<GraphicsSettingsRequestPayload> TYPE = new Type<>(ModChannels.REQUEST_LIGHT_SETTINGS_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, GraphicsSettingsRequestPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, GraphicsSettingsRequestPayload::namesList,
                    ByteBufCodecs.STRING_UTF8, GraphicsSettingsRequestPayload::valuesList,
                    ByteBufCodecs.BOOL, GraphicsSettingsRequestPayload::silent,
                    GraphicsSettingsRequestPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GraphicsSettingsRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientNetworkHandler.sendGraphicsReport(payload.namesList(), payload.valuesList(), payload.silent()));
    }
}
