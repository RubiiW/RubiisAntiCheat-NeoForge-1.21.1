package net.rubii.rac.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.rubii.rac.network.ClientNetworkHandler;
import net.rubii.rac.network.ModChannels;
import org.jetbrains.annotations.NotNull;

public record ModFilesLoggingRequestPayload(boolean silent) implements CustomPacketPayload {

    public static final Type<ModFilesLoggingRequestPayload> TYPE = new Type<>(ModChannels.REQUEST_MOD_FILES_LOGGING_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModFilesLoggingRequestPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ModFilesLoggingRequestPayload::silent,
                    ModFilesLoggingRequestPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ModFilesLoggingRequestPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> ClientNetworkHandler.sendModFilesLoggingReport(payload.silent()));
    }
}
