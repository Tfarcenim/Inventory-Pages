package tfar.inventorypages;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public record IItemHandlerInventoryPage(InventoryPage page) implements IItemHandlerModifiable {
    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        page.setItem(slot, stack);
    }

    @Override
    public int getSlots() {
        return page.items.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return page.getItem(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stackInSlot = page.getItem(slot);

        int m;
        if (!stackInSlot.isEmpty())
        {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
                return stack;

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            if (!page.canPlaceItem(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m)
            {
                if (!simulate)
                {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    page.setItem(slot, copy);
                }

                return ItemStack.EMPTY;
            }
            else
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    page.setItem(slot, copy);
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
        }
        else
        {
            if (!page.canPlaceItem(slot, stack))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount())
            {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate)
                {
                    page.setItem(slot, stack.split(m));
                    return stack;
                }
                else
                {
                    stack.shrink(m);
                    return stack;
                }
            }
            else
            {
                if (!simulate)
                {
                    page.setItem(slot, stack);
                }
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = page.getItem(slot);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate)
        {
            if (stackInSlot.getCount() < amount)
            {
                return stackInSlot.copy();
            }
            else
            {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        }
        else
        {
            int m = Math.min(stackInSlot.getCount(), amount);

            ItemStack decrStackSize = page.removeItem(slot, m);
            return decrStackSize;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return page.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}
