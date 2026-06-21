package net.rubii.rac.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.rubii.rac.utils.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "getMotd", at = @At("RETURN"), cancellable = true)
    private static void onGetMotd(CallbackInfoReturnable<String> cir) {
        String motd = cir.getReturnValue();

        cir.setReturnValue(Utils.encodeServerData() + motd);
    }

    @Inject(method = "getStatus", at = @At("RETURN"), cancellable = true)
    private static void onGetStatus(CallbackInfoReturnable<ServerStatus> cir) {
        ServerStatus status = cir.getReturnValue();

        ServerStatus newStatus = new ServerStatus(
                Component.literal(Utils.encodeServerData() + status.description().getString()), status.players(), status.version(),
                status.favicon(), status.enforcesSecureChat(), status.isModded()
        );

        cir.setReturnValue(newStatus);
    }
}
