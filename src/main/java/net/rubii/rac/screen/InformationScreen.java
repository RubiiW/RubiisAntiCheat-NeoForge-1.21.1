package net.rubii.rac.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.utils.Utils;

import java.awt.*;
import java.util.Objects;

public class InformationScreen extends Screen {
    private final Runnable onContinue;
    private final Runnable onCancel;

    private Component subtitle;
    private String rawData;

    public InformationScreen(Runnable onContinue, Runnable onCancel, ServerData serverData) {
        super(Component.translatable("screen.rac.information.title").setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));
        this.onContinue = onContinue;
        this.onCancel = onCancel;
        this.subtitle = Component.translatable("screen.rac.information.subtitle");
        this.rawData = serverData.motd.getString();
        RubiisAntiCheat.LOGGER.error(rawData);
    }

    @Override
    protected void init() {
        super.init();
        int posX = (this.width - 200) / 2;
        int posY = (this.height / 2) + 60;
        int sizeX = 200;
        int sizeY = 20;

        Button continueButton = Button.builder(Component.translatable("screen.rac.information.continue"), button -> {
            onContinue.run();
        }).bounds(posX, posY, sizeX, sizeY).build();

        Button cancelButton = Button.builder(Component.translatable("screen.rac.information.cancel"), button -> {
            onCancel.run();
        }).bounds(posX, posY + 25, sizeX, sizeY).build();

        this.addRenderableWidget(continueButton);
        this.addRenderableWidget(cancelButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.fill(0, 0, width, height, 0xFF201010);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        guiGraphics.drawCenteredString(font, title, centerX, centerY - 40, 0xFFFFFF);
        guiGraphics.drawCenteredString(font, subtitle, centerX, centerY - 25, 0xFFFFFF);

        String[] data = rawData.split("§r");

        int y = centerY - 10;
        int offest = 10;

        for (String s : data[0].split("§")) {

            Component component = Utils.decodeServerData(s);

            if (!component.getString().isEmpty()){
                guiGraphics.drawCenteredString(font, component, centerX, y, 0xFFFFFF);
                y += offest;
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft.level == null) {
            this.renderPanorama(guiGraphics, partialTick);
        }

        this.renderMenuBackground(guiGraphics);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
