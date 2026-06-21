package net.rubii.rac.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.quickplay.QuickPlay;
import net.minecraft.client.quickplay.QuickPlayLog;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.screen.InformationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {

    @Invoker("<init>")
    private static ConnectScreen createConnectScreen(Screen parent, Component component) {
        return null;
    }

    @Invoker("connect")
    public abstract void invokeConnect(final Minecraft minecraft, final ServerAddress serverAddress, final ServerData serverData, @Nullable final TransferState transferState);

    @Invoker("updateStatus")
    public abstract void invokeUpdateStatus(Component component);

    @Inject(method = "startConnecting", at = @At("HEAD"), cancellable = true)
    private static void onConnect(Screen parent, Minecraft minecraft, ServerAddress serverAddress, ServerData serverData, boolean isQuickPlay, TransferState transferState, CallbackInfo ci) {
        ci.cancel();

        Runnable continueConnection = () -> {
            if (minecraft.screen instanceof ConnectScreen) {
                RubiisAntiCheat.LOGGER.error("Attempt to connect while already connecting");
            } else {
                Component component;
                if (transferState != null) {
                    component = CommonComponents.TRANSFER_CONNECT_FAILED;
                } else if (isQuickPlay) {
                    component = QuickPlay.ERROR_TITLE;
                } else {
                    component = CommonComponents.CONNECT_FAILED;
                }

                ConnectScreen connectscreen = createConnectScreen(parent, component);
                if (transferState != null) {
                    ((ConnectScreenMixin) (Object) connectscreen).invokeUpdateStatus(Component.translatable("connect.transferring"));
                }

                minecraft.disconnect();
                minecraft.prepareForMultiplayer();
                minecraft.updateReportEnvironment(ReportEnvironment.thirdParty(serverData.ip));
                minecraft.quickPlayLog().setWorldData(QuickPlayLog.Type.MULTIPLAYER, serverData.ip, serverData.name);
                minecraft.setScreen(connectscreen);
                ((ConnectScreenMixin) (Object) connectscreen).invokeConnect(minecraft, serverAddress, serverData, transferState);
            }
        };
        Runnable cancelConnection = () -> minecraft.setScreen(parent);

        minecraft.setScreen(new InformationScreen(continueConnection, cancelConnection, serverData));
    }
}