package net.rubii.rac.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.rubii.rac.network.ClientNetworkHandler;
import net.rubii.rac.network.ModChannels;
import org.jetbrains.annotations.NotNull;

public record ModsIntegrityRequestPayload(boolean silent) implements CustomPacketPayload {

    public static final Type<ModsIntegrityRequestPayload> TYPE = new Type<>(ModChannels.REQUEST_MODS_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModsIntegrityRequestPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ModsIntegrityRequestPayload::silent,
                    ModsIntegrityRequestPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ModsIntegrityRequestPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> ClientNetworkHandler.sendModsIntegrityReport(payload.silent()));
    }
}
