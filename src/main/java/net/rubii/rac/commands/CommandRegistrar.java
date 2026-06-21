package net.rubii.rac.commands;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class CommandRegistrar {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        RACCommand.register(event.getDispatcher());
    }
}
