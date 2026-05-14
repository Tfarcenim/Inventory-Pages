package tfar.inventorypages.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class MoveToPagesButton extends SmallButton {


    public MoveToPagesButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress callback, OnTooltip onTooltip) {
        super(x, y, widthIn, heightIn, buttonText, callback, onTooltip);
    }

    public MoveToPagesButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress callback) {
        super(x, y, widthIn, heightIn, buttonText, callback);
    }


    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen screen) {
            boolean isInventoryTab =screen.getSelectedTab() == CreativeModeTab.TAB_INVENTORY.getId();
            this.active = isInventoryTab;
            if (!isInventoryTab) {
                return;
            }
        }
        super.renderButton(matrices, mouseX, mouseY, partialTicks);
    }
}
