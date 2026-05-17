package tfar.inventorypages;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class InventoryPageList extends ArrayList<InventoryPage> {
    final Player player;
    public InventoryPageList(Player player) {
        this.player = player;
        for (int i = 0; i < InventoryPages.LIST.size(); i++) {
            InventoryPage inventoryPage = new InventoryPage(InventoryPages.LIST.get(i));
            add(inventoryPage);
        }
    }

    public void load(ListTag listTag) {
        for (int i = 0; i < listTag.size(); i++) {
            Tag tag = listTag.get(i);
            ListTag cTag = (ListTag) tag;
            get(i).load(cTag);
        }
    }

    public ListTag save() {
        ListTag listTag = new ListTag();
        for (InventoryPage inventoryPage : this) {
            listTag.add(inventoryPage.save());
        }
        return listTag;
    }

    public void dropAll() {
        for (InventoryPage inventoryPage : this) {
            inventoryPage.dropAll(player);
        }
    }

    public void replaceWith(InventoryPageList original) {
        for  (int i = 0; i < this.size(); i++) {
            InventoryPage inventoryPageNew = this.get(i);
            InventoryPage inventoryPageOriginal = original.get(i);
            inventoryPageNew.replaceWith(inventoryPageOriginal);
        }
    }

    public void tick() {
        for (InventoryPage inventoryPage : this) {
            inventoryPage.tick(player);
        }
    }

    public int totalSlots() {
        int total = this.stream().mapToInt(inventoryPage -> inventoryPage.items.size()).sum();
        return total;
    }
}
