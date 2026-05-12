package tfar.inventorypages;

import net.minecraft.client.Minecraft;
import tfar.inventorypages.network.client.S2CToastPacket;

public class InventoryPagesClient {
    public static void handle(S2CToastPacket packet) {
        InventoryPageList inventoryPageList = ((PlayerDuck)Minecraft.getInstance().player).inventoryPageList();
        InventoryPage inventoryPage = inventoryPageList.get(packet.page());
        Minecraft.getInstance().getToasts().addToast(new InventoryPageToast(inventoryPage.config.icon(),packet.pickedUp()));
    }
}
