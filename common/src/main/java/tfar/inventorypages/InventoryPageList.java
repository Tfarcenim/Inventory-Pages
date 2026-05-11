package tfar.inventorypages;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class InventoryPageList extends ArrayList<InventoryPage> {
    private final Player player;
    public InventoryPageList(Player player) {
        this.player = player;
    }

    public void load(ListTag listTag) {
        clear();
        for (int i = 0; i < listTag.size(); i++) {
            Tag tag = listTag.get(i);
            ListTag cTag = (ListTag) tag;
            InventoryPage inventoryPage = new InventoryPage(InventoryPages.LIST.get(i));
            inventoryPage.load(cTag);
            add(inventoryPage);
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
}
