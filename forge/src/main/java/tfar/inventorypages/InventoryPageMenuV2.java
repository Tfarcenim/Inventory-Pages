package tfar.inventorypages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class InventoryPageMenuV2 extends AbstractContainerMenu {

    private final DataSlot page = DataSlot.standalone();

    private final InventoryPageList inventoryPageList;


    public InventoryPageMenuV2(int containerId, Inventory inventory, final Player player, int startPage) {
        super(RegistryObjects.INVENTORY_PAGE_MENU_V2, containerId);
        inventoryPageList = ((PlayerDuck)player).inventoryPageList();

        //this is the inventory
        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new PageSlot( column + row * 9, 8 + column * 18, 16 + row * 18));
            }
        }

        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(inventory, column + (row+1) * 9, 8 + column * 18, 84 + row * 18));
            }
        }

        //this is the hotbar
        for(int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 142));
        }
        page.set(startPage);
        addDataSlot(page);
    }

    public InventoryPageMenuV2(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i,inventory,inventory.player,buf.readInt());
    }

    public void setPage(int page) {
        this.page.set(page);
        if (inventoryPageList.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundContainerSetContentPacket(containerId, incrementStateId(), inventoryPageList.get(page).items
                    , getCarried()));
        }
    }

    public int getPage() {
        return page.get();
    }

    public InventoryPage getActivePage() {
        return inventoryPageList.get(getPage());
    }

    public class PageSlot extends Slot {
        public PageSlot(int index, int x, int y) {
            super(new SimpleContainer(0), index, x, y);
        }

        @Override
        public void initialize(ItemStack p_219997_) {
            getActivePage().setItem(getContainerSlot(), p_219997_);
        }

        @Override
        public ItemStack getItem() {
            return getActivePage().getItem(getContainerSlot());
        }

        @Override
        public void set(ItemStack pStack) {
            getActivePage().setItem(getContainerSlot(), pStack);
        }

        @Override
        public ItemStack remove(int pAmount) {
            return getActivePage().removeItem(this.getContainerSlot(), pAmount);
        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return getActivePage().canPlaceItem(getContainerSlot(), pStack);
        }
    }

    @Override
    public void setData(int pId, int pData) {
        super.setData(pId, pData);
    }

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        return super.clickMenuButton(pPlayer, pId);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.slots.get(pIndex);
            if (slot != null && slot.hasItem()) {
                ItemStack itemstack1 = slot.getItem();
                itemstack = itemstack1.copy();
                if (pIndex < 3 * 9) {
                    if (!this.moveItemStackTo(itemstack1, 3 * 9, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 0, 3 * 9, false)) {
                    return ItemStack.EMPTY;
                }

                if (itemstack1.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }

            return itemstack;
        }
    }
