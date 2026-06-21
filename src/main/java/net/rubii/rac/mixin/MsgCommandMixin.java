package net.rubii.rac.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.rubii.rac.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MsgCommand.class)
public abstract class MsgCommandMixin {

    @Invoker("sendMessage")
    public static void invokeSendMessage(CommandSourceStack source, Collection<ServerPlayer> targets, PlayerChatMessage message) {
    }

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void onRegister(CommandDispatcher<CommandSourceStack> dispatcher, CallbackInfo ci) {
        ci.cancel();

        LiteralCommandNode<CommandSourceStack> literalcommandnode = dispatcher.register(Commands.literal("msg")
                .requires(source -> source.hasPermission(Config.PRIVATE_CHAT_PERMISSION.get()))
                .then(
                        Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes(context -> {
                            Collection<ServerPlayer> collection = EntityArgument.getPlayers(context, "targets");
                            if (!collection.isEmpty()) {
                                MessageArgument.resolveChatMessage(context, "message", playerChatMessage -> invokeSendMessage(context.getSource(), collection, playerChatMessage));
                            }

                            return collection.size();
                        }))
                )
        );
        dispatcher.register(Commands.literal("tell").redirect(literalcommandnode).requires(source -> source.hasPermission(Config.PRIVATE_CHAT_PERMISSION.get())));
        dispatcher.register(Commands.literal("w").redirect(literalcommandnode).requires(source -> source.hasPermission(Config.PRIVATE_CHAT_PERMISSION.get())));
    }
}
