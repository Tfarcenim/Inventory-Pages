package tfar.inventorypages.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.inventorypages.network.PacketHandler;
import tfar.inventorypages.network.server.C2SCarriedItemPacket;

public class BackButton extends Button {
    public BackButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnTooltip pOnTooltip) {
        super(pX, pY, pWidth, pHeight, pMessage, b -> {

            Player player = Minecraft.getInstance().player;

            if (player != null) {
                ItemStack stack = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                InventoryScreen inventory = new InventoryScreen(player);
                Minecraft.getInstance().setScreen(inventory);
                player.containerMenu.setCarried(stack);
                PacketHandler.sendToServer(new C2SCarriedItemPacket(stack));
            }
        }, pOnTooltip);
    }
}
