package net.rubii.rac.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.rubii.rac.network.ModChannels;
import net.rubii.rac.network.ServerNetworkHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public record ModFilesLoggingReportPayload(String modFilesList, boolean silent) implements CustomPacketPayload {

    public static final Type<ModFilesLoggingReportPayload> TYPE = new Type<>(ModChannels.REPORT_MOD_FILES_LOGGING_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModFilesLoggingReportPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ModFilesLoggingReportPayload::modFilesList,
                    ByteBufCodecs.BOOL, ModFilesLoggingReportPayload::silent,
                    ModFilesLoggingReportPayload::new
            );

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ModFilesLoggingReportPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player){
                try {
                    ServerNetworkHandler.handleModFilesLoggingReport(payload, player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}