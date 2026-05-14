package tfar.inventorypages;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryPage {

    public final NonNullList<ItemStack> items;
    public final Config config;

    public InventoryPage(Config config) {
        this.config = config;
        items = NonNullList.withSize(27, ItemStack.EMPTY);
    }

    public void load(ListTag pListTag) {
        this.items.clear();
        for (int i = 0; i < pListTag.size(); ++i) {
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
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte) i);
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

    public void setItem(int slot, ItemStack pStack) {
        items.set(slot, pStack);
    }

    public ItemStack removeItem(int slot, int pAmount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, slot, pAmount);
        return itemstack;
    }

    public void replaceWith(InventoryPage inventoryPageOriginal) {
        for (int i = 0; i < items.size(); ++i) {
            this.setItem(i, inventoryPageOriginal.getItem(i));
        }
    }

    public boolean add(Player player, ItemStack stack) {
        return this.add(player, -1, stack);
    }

    public boolean add(Player player, int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slot == -1) {
                        slot = this.getFreeSlot();
                    }

                    if (slot >= 0) {
                        this.items.set(slot, stack.copy());
                        this.items.get(slot).setPopTime(5);
                        stack.setCount(0);
                        return true;
                    } else if (player.getAbilities().instabuild) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int $$2;
                    do {
                        $$2 = stack.getCount();
                        if (slot == -1) {
                            stack.setCount(this.addResource(stack));
                        } else {
                            stack.setCount(this.addResource(slot, stack));
                        }
                    } while (!stack.isEmpty() && stack.getCount() < $$2);

                    if (stack.getCount() == $$2 && player.getAbilities().instabuild) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return stack.getCount() < $$2;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.forThrowable(throwable, "Adding item to inventory page");
                CrashReportCategory category = crashReport.addCategory("Item being added");
                category.setDetail("Item ID", Item.getId(stack.getItem()));
                category.setDetail("Item data", stack.getDamageValue());
                category.setDetail("Item name", () -> stack.getHoverName().getString());
                throw new ReportedException(crashReport);
            }
        }
    }

    private int addResource(ItemStack stack) {
        int freeSlot = this.getSlotWithRemainingSpace(stack);
        if (freeSlot == -1) {
            freeSlot = this.getFreeSlot();
        }

        return freeSlot == -1 ? stack.getCount() : this.addResource(freeSlot, stack);
    }

    private boolean hasRemainingSpaceForItem(ItemStack slotStack, ItemStack added) {
        return !slotStack.isEmpty() && ItemStack.isSameItemSameTags(slotStack, added) &&
                slotStack.isStackable() && slotStack.getCount() < slotStack.getMaxStackSize() && slotStack.getCount() < this.getMaxStackSize();
    }

    public void tick(Player player) {
        for (int i = 0; i < items.size(); ++i) {
            if (!items.get(i).isEmpty()) {
                items.get(i).inventoryTick(player.level, player, i, false);
            }
        }
    }

    public int getSlotWithRemainingSpace(ItemStack stack) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.hasRemainingSpaceForItem(this.items.get(i), stack)) {
                return i;
            }
        }
        return -1;
    }

    private int addResource(int slot, ItemStack stack) {
        Item item = stack.getItem();
        int $$3 = stack.getCount();
        ItemStack slotStack = this.getItem(slot);
        if (slotStack.isEmpty()) {
            slotStack = new ItemStack(item, 0);
            if (stack.hasTag()) {
                slotStack.setTag(stack.getTag().copy());
            }

            this.setItem(slot, slotStack);
        }

        int $$5 = $$3;
        if ($$3 > slotStack.getMaxStackSize() - slotStack.getCount()) {
            $$5 = slotStack.getMaxStackSize() - slotStack.getCount();
        }

        if ($$5 > this.getMaxStackSize() - slotStack.getCount()) {
            $$5 = this.getMaxStackSize() - slotStack.getCount();
        }

        if ($$5 == 0) {
            return $$3;
        } else {
            $$3 -= $$5;
            slotStack.grow($$5);
            slotStack.setPopTime(5);
            return $$3;
        }
    }

    public int getMaxStackSize() {
        return 64;
    }

    public int getFreeSlot() {
        for (int slot = 0; slot < this.items.size(); ++slot) {
            if (this.items.get(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return config.tag == null || stack.is(config.tag);
    }

    public record Config(ItemStack icon, @Nullable TagKey<Item> tag, Component title, int pageColor) {
    }
}
