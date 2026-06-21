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
        /*registrar.playToClient(
                ConsentRequestPayload.TYPE,
                ConsentRequestPayload.CODEC,
                ConsentRequestPayload::handle
        );*/

        registrar.playToClient(
                SettingsRequestPayload.TYPE,
                SettingsRequestPayload.CODEC,
                SettingsRequestPayload::handle
        );

        registrar.playToClient(
                ModsRequestPayload.TYPE,
                ModsRequestPayload.CODEC,
                ModsRequestPayload::handle
        );

        registrar.playToClient(
                ScreenshotRequestPayload.TYPE,
                ScreenshotRequestPayload.CODEC,
                ScreenshotRequestPayload::handle
        );

        //CLIENT TO SERVER
        /*registrar.playToServer(
                ConsentReportPayload.TYPE,
                ConsentReportPayload.CODEC,
                ConsentReportPayload::handle
        );*/

        registrar.playToServer(
                SettingsReportPayload.TYPE,
                SettingsReportPayload.CODEC,
                SettingsReportPayload::handle
        );

        registrar.playToServer(
                ModsReportPayload.TYPE,
                ModsReportPayload.CODEC,
                ModsReportPayload::handle
        );

        registrar.playToServer(
                ScreenshotReportPayload.TYPE,
                ScreenshotReportPayload.CODEC,
                ScreenshotReportPayload::handle
        );
    }
}
