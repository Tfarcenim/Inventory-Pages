package tfar.inventorypages;

import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import tfar.inventorypages.client.ClientPopup;
import tfar.inventorypages.network.S2CSetSelectedPacket;
import tfar.inventorypages.network.client.S2CPopupPacket;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class InventoryPagesClient {
    public static void handle(S2CPopupPacket packet) {
        InventoryPageList inventoryPageList = ((PlayerDuck)Minecraft.getInstance().player).inventoryPageList();
        InventoryPage inventoryPage = inventoryPageList.get(packet.page());
        addPopup(new ClientPopup(packet.page(), packet.pickedUp(),popups.size()));
    }

    public static final Deque<ClientPopup> popups = Queues.newArrayDeque();

    static void addPopup(ClientPopup popup) {
        popups.add(popup);
    }

    public static void tickPopups() {
        List<ClientPopup> forRemoval = new ArrayList<>();
        for (ClientPopup popup : popups) {
            if (popup.tick()) {
                forRemoval.add(popup);
            }
        }
        popups.removeAll(forRemoval);
    }

    public static void handleSelected(S2CSetSelectedPacket packet) {
        Minecraft.getInstance().player.getInventory().selected = packet.selected();
    }
}
