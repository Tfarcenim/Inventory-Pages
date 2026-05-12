package tfar.inventorypages;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InventoryPage {

    public final NonNullList<ItemStack> items;
    public final Config config;
    public InventoryPage(Config config) {
        this.config = config;
        items = NonNullList.withSize(27, ItemStack.EMPTY);
    }

    public void load(ListTag pListTag) {
        this.items.clear();
        for(int i = 0; i < pListTag.size(); ++i) {
            CompoundTag compoundtag = pListTag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
                if (j < this.items.size()) {
                    this.items.set(j, itemstack);
                }
            }
        }
    }

    public ListTag save() {
        ListTag pListTag = new ListTag();
        for(int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                this.items.get(i).save(compoundtag);
                pListTag.add(compoundtag);
            }
        }
        return pListTag;
    }

    public void dropAll(Player player) {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack = items.get(i);
            if (!itemstack.isEmpty()) {
                player.drop(itemstack, true, false);
                items.set(i, ItemStack.EMPTY);
            }
        }
    }

    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot,ItemStack pStack) {
        items.set(slot, pStack);
    }

    public ItemStack removeItem(int slot, int pAmount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, slot, pAmount);
        return itemstack;
    }

    public void replaceWith(InventoryPage inventoryPageOriginal) {
        for(int i = 0; i < items.size(); ++i) {
            this.setItem(i, inventoryPageOriginal.getItem(i));
        }
    }

    public record Config(ItemStack icon, TagKey<Item> tag) {}

}
