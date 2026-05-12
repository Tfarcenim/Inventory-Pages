package tfar.inventorypages;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class InventoryPageMenu extends RecipeBookMenu<CraftingContainer> {
    public static final int CONTAINER_ID = 0;
    public static final int RESULT_SLOT = 0;
    public static final int CRAFT_SLOT_START = 1;
    public static final int CRAFT_SLOT_END = 5;
    public static final int ARMOR_SLOT_START = 5;
    public static final int ARMOR_SLOT_END = 9;
    public static final int INV_SLOT_START = 9;
    public static final int INV_SLOT_END = 36;
    public static final int USE_ROW_SLOT_START = 36;
    public static final int USE_ROW_SLOT_END = 45;
    public static final int SHIELD_SLOT = 45;
    public static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = new ResourceLocation("item/empty_armor_slot_shield");
    static final ResourceLocation[] TEXTURE_EMPTY_SLOTS;
    private static final EquipmentSlot[] SLOT_IDS;
    private final CraftingContainer craftSlots = new CraftingContainer(this, 2, 2);
    private final ResultContainer resultSlots = new ResultContainer();
    private final Player owner;

    int page;

    private final InventoryPageList inventoryPageList;

    public InventoryPageMenu(int i, Inventory inventory) {
        this(i,inventory, inventory.player,0);
    }


    public InventoryPageMenu(int containerId,Inventory inventory, final Player player,int page) {
        super(RegistryObjects.INVENTORY_PAGE_MENU, containerId);
        inventoryPageList = ((PlayerDuck)player).inventoryPageList();
        this.owner = player;

        this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 154, 28));

        for(int craftSlotRow = 0; craftSlotRow < 2; ++craftSlotRow) {
            for(int craftSlotColumn = 0; craftSlotColumn < 2; ++craftSlotColumn) {
                this.addSlot(new Slot(this.craftSlots, craftSlotColumn + craftSlotRow * 2, 98 + craftSlotColumn * 18, 18 + craftSlotRow * 18));
            }
        }

        for(int armorSlot = 0; armorSlot < 4; ++armorSlot) {
            final EquipmentSlot $$6 = SLOT_IDS[armorSlot];
            this.addSlot(new Slot(inventory, 39 - armorSlot, 8, 8 + armorSlot * 18) {
                @Override
                public void set(ItemStack $$0) {
                    ItemStack $$1 = this.getItem();
                    super.set($$0);
                    player.onEquipItem($$6, $$1, $$0);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack $$0) {
                    return $$6 == Mob.getEquipmentSlotForItem($$0);
                }

                @Override
                public boolean mayPickup(Player $$0) {
                    ItemStack $$1 = this.getItem();
                    return ($$1.isEmpty() || $$0.isCreative() || !EnchantmentHelper.hasBindingCurse($$1)) && super.mayPickup($$0);
                }

                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[$$6.getIndex()]);
                }
            });
        }

        //this is the inventory
        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new PageSlot( column + row * 9, 8 + column * 18, 84 + row * 18));
            }
        }

        //this is the hotbar
        for(int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 142));
        }

        this.addSlot(new Slot(inventory, 40, 77, 62) {
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }

    public void setPage(int page) {
        this.page = page;
    }

    InventoryPage getActivePage() {
        return inventoryPageList.get(page);
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
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents $$0) {
        this.craftSlots.fillStackedContents($$0);
    }

    @Override
    public void clearCraftingContent() {
        this.resultSlots.clearContent();
        this.craftSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(Recipe<? super CraftingContainer> $$0) {
        return $$0.matches(this.craftSlots, this.owner.level);
    }

    @Override
    public void slotsChanged(Container $$0) {
        CraftingMenu.slotChangedCraftingGrid(this, this.owner.level, this.owner, this.craftSlots, this.resultSlots);
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.resultSlots.clearContent();
        if (!$$0.level.isClientSide) {
            this.clearContainer($$0, this.craftSlots);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int pIndex) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack $$4 = slot.getItem();
            $$2 = $$4.copy();
            EquipmentSlot $$5 = Mob.getEquipmentSlotForItem($$2);
            if (pIndex == 0) {
                if (!this.moveItemStackTo($$4, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft($$4, $$2);
            } else if (pIndex >= 1 && pIndex < 5) {
                if (!this.moveItemStackTo($$4, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 5 && pIndex < 9) {
                if (!this.moveItemStackTo($$4, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if ($$5.getType() == EquipmentSlot.Type.ARMOR && !this.slots.get(8 - $$5.getIndex()).hasItem()) {
                int $$6 = 8 - $$5.getIndex();
                if (!this.moveItemStackTo($$4, $$6, $$6 + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if ($$5 == EquipmentSlot.OFFHAND && !this.slots.get(45).hasItem()) {
                if (!this.moveItemStackTo($$4, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 9 && pIndex < 36) {
                if (!this.moveItemStackTo($$4, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 36 && pIndex < 45) {
                if (!this.moveItemStackTo($$4, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo($$4, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if ($$4.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, $$4);
            if (pIndex == 0) {
                player.drop($$4, false);
            }
        }

        return $$2;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return $$1.container != this.resultSlots && super.canTakeItemForPickAll($$0, $$1);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return 5;
    }

    public CraftingContainer getCraftSlots() {
        return this.craftSlots;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int $$0) {
        return $$0 != this.getResultSlotIndex();
    }

    static {
        TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
        SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }
}
