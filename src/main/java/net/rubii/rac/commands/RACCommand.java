package net.rubii.rac.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.EnumArgument;
import net.rubii.rac.Config;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.network.payload.*;

import java.util.Collection;

public class RACCommand {
    public static void register(CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rac")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("type", EnumArgument.enumArgument(CheckType.class))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("silent", BoolArgumentType.bool())
                                        .executes(context -> {

                                            //WITH TARGETS
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                            for (ServerPlayer player : targets) {
                                                execute(context, player, BoolArgumentType.getBool(context, "silent"));
                                            }
                                            return targets.size();
                                        })
                                )
                        )
                        .executes(context -> {

                            //WITHOUT TARGETS
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                execute(context, player, true);
                                return 1;
                            }
                            context.getSource().sendFailure(Component.literal("You must be a player to run this command without arguments."));
                            return 0;
                        })
                )
        );
    }

    private static void execute(CommandContext<CommandSourceStack> context, ServerPlayer player, boolean silent) {
        CheckType type = context.getArgument("type", CheckType.class);

        if (type == CheckType.all) {
            if (/*Config.ENABLE_REQUIRED_MOD_FILES_LIST.get() || Config.ENABLE_FORBIDDEN_MOD_FILES_LIST.get() ||*/ Config.ENABLE_MOD_ALTERATION_DETECTION.get())
                PacketDistributor.sendToPlayer(player, new ModsIntegrityRequestPayload(silent));

            if (Config.ENABLE_CAVE_LIGHTING_MULTIPLIER.get() || Config.ENABLE_BRIGHTNESS.get())
                PacketDistributor.sendToPlayer(player, new GraphicsSettingsRequestPayload(silent));

            if (Config.ENABLE_SCREENSHOTS.get()) PacketDistributor.sendToPlayer(player, new ScreenshotRequestPayload(silent));

            if (Config.ENABLE_LOG_MOD_FILES_LIST.get()) PacketDistributor.sendToPlayer(player, new ModFilesLoggingRequestPayload(silent));

            context.getSource().sendSuccess(
                    () -> Component.translatable("commands.rac.full_check_other", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)),
                    true
            );
        } else if (type == CheckType.modsIntegrity){
            if (/*Config.ENABLE_REQUIRED_MOD_FILES_LIST.get() || Config.ENABLE_FORBIDDEN_MOD_FILES_LIST.get() ||*/ Config.ENABLE_MOD_ALTERATION_DETECTION.get()){
                PacketDistributor.sendToPlayer(player, new ModsIntegrityRequestPayload(silent));
                context.getSource().sendSuccess(
                        () -> Component.translatable("commands.rac.mods_integrity_check_other", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)),
                        true
                );
            } else {
                context.getSource().sendFailure(
                        Component.translatable("commands.rac.feature_not_enabled", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
                );
            }
        } else if (type == CheckType.modsList){
            if (Config.ENABLE_LOG_MOD_FILES_LIST.get()){
                PacketDistributor.sendToPlayer(player, new ModFilesLoggingRequestPayload(silent));
                context.getSource().sendSuccess(
                        () -> Component.translatable("commands.rac.mod_files_logging_check_other", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)),
                        true
                );
            } else {
                context.getSource().sendFailure(
                        Component.translatable("commands.rac.feature_not_enabled", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
                );
            }
        } else if (type == CheckType.graphics) {
            if (Config.ENABLE_CAVE_LIGHTING_MULTIPLIER.get() || Config.ENABLE_BRIGHTNESS.get()){
                PacketDistributor.sendToPlayer(player, new GraphicsSettingsRequestPayload(silent));
                context.getSource().sendSuccess(
                        () -> Component.translatable("commands.rac.graphics_check_other", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)),
                        true
                );
            } else {
                context.getSource().sendFailure(
                        Component.translatable("commands.rac.feature_not_enabled", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
                );
            }
        } else if (type == CheckType.screenshot){
            if (Config.ENABLE_SCREENSHOTS.get()) {
                PacketDistributor.sendToPlayer(player, new ScreenshotRequestPayload(silent));
                context.getSource().sendSuccess(
                        () -> Component.translatable("commands.rac.screenshot_check_other", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)),
                        true
                );
            } else {
                context.getSource().sendFailure(
                        Component.translatable("commands.rac.feature_not_enabled", player.getName().getString()).setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
                );
            }
        }
    }

    private enum CheckType implements StringRepresentable {
        all, modsIntegrity, modsList, graphics, screenshot;

        @Override
        public String getSerializedName() {
            return name();
        }
    }
}
