package net.rubii.rac.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.rubii.rac.network.payload.*;

@EventBusSubscriber
public class NetworkRegistrar {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0").optional();

        //SERVER TO CLIENT

        registrar.playToClient(
                GraphicsSettingsRequestPayload.TYPE,
                GraphicsSettingsRequestPayload.CODEC,
                GraphicsSettingsRequestPayload::handle
        );

        registrar.playToClient(
                ModsIntegrityRequestPayload.TYPE,
                ModsIntegrityRequestPayload.CODEC,
                ModsIntegrityRequestPayload::handle
        );

        registrar.playToClient(
                ModFilesLoggingRequestPayload.TYPE,
                ModFilesLoggingRequestPayload.CODEC,
                ModFilesLoggingRequestPayload::handle
        );

        registrar.playToClient(
                ScreenshotRequestPayload.TYPE,
                ScreenshotRequestPayload.CODEC,
                ScreenshotRequestPayload::handle
        );

        //CLIENT TO SERVER

        registrar.playToServer(
                GraphicsSettingsReportPayload.TYPE,
                GraphicsSettingsReportPayload.CODEC,
                GraphicsSettingsReportPayload::handle
        );

        registrar.playToServer(
                ModsIntegrityReportPayload.TYPE,
                ModsIntegrityReportPayload.CODEC,
                ModsIntegrityReportPayload::handle
        );

        registrar.playToServer(
                ModFilesLoggingReportPayload.TYPE,
                ModFilesLoggingReportPayload.CODEC,
                ModFilesLoggingReportPayload::handle
        );

        registrar.playToServer(
                ScreenshotReportPayload.TYPE,
                ScreenshotReportPayload.CODEC,
                ScreenshotReportPayload::handle
        );
    }
}
