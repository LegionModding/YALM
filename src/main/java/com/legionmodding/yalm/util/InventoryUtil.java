package com.legionmodding.yalm.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class InventoryUtil
{
    public static boolean areItemAndTagEqual(@Nonnull ItemStack stackA, @Nonnull ItemStack stackB)
    {
        return stackA.areShareTagsEqual(stackB) && ItemStack.tagMatches(stackA, stackB);
    }

    public static boolean areMergeCandidates(@Nonnull ItemStack source, @Nonnull ItemStack target)
    {
        return areItemAndTagEqual(source, target) && target.getCount() < target.getMaxStackSize();
    }

    @Nonnull
    public static ItemStack copyAndChange(@Nonnull ItemStack stack, int newSize)
    {
        ItemStack copy = stack.copy();
        copy.setCount(newSize);
        return copy;
    }

    public static List<ItemStack> getInventoryContents(IInventory inventory)
    {
        List<ItemStack> result = Lists.newArrayList();

        for (int i = 0; i < inventory.getContainerSize(); i++)
        {
            ItemStack slot = inventory.getItem(i);
            if (!slot.isEmpty())
            {
                result.add(slot);
            }
        }

        return result;
    }

    public static boolean tryMergeStacks(@Nonnull ItemStack stackToMerge, @Nonnull ItemStack stackInSlot)
    {
        if (stackInSlot.isEmpty() || !stackInSlot.areShareTagsEqual(stackToMerge) || !ItemStack.tagMatches(stackToMerge, stackInSlot))
        {
            return false;
        }

        int newStackSize = stackInSlot.getCount() + stackToMerge.getCount();

        final int maxStackSize = stackToMerge.getMaxStackSize();

        if (newStackSize <= maxStackSize)
        {
            stackToMerge.setCount(0);
            stackInSlot.setCount(newStackSize);
            return true;
        }

        else if (stackInSlot.getCount() < maxStackSize)
        {
            stackToMerge.shrink(maxStackSize - stackInSlot.getCount());
            stackInSlot.setCount(maxStackSize);
            return true;
        }

        return false;
    }

    @Nonnull
    public static ItemStack returnItem(@Nonnull ItemStack stack)
    {
        return stack.isEmpty()? ItemStack.EMPTY : stack.copy();
    }

    protected static void isItemValid(IInventory inventory, int slot, @Nonnull ItemStack stack)
    {
        Preconditions.checkArgument(inventory.canPlaceItem(slot, stack), "Slot %s cannot accept item", slot);
    }

    public static Iterable<ItemStack> asIterable(final IInventory inv)
    {
        return () -> new AbstractIterator<ItemStack>()
        {
            final int size = inv.getContainerSize();
            int slot = 0;

            @Override
            protected ItemStack computeNext()
            {
                if (slot >= size)
                {
                    return endOfData();
                }

                return inv.getItem(slot++);
            }
        };
    }

    public static IItemHandler tryGetHandler(World world, BlockPos pos, Direction side)
    {
        if (!world.isLoaded(pos))
        {
            return null;
        }

        final TileEntity te = world.getBlockEntity(pos);

        return tryGetHandler(te, side);
    }

    @Nullable
    public static IItemHandler tryGetHandler(TileEntity te, Direction side)
    {
        if (te == null) return null;

        final LazyOptional<IItemHandler> cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);

        if (cap.isPresent())
        {
            return cap.orElse(null);
        }

        if (te instanceof ISidedInventory)
        {
            return new SidedInvWrapper((ISidedInventory)te, side);
        }

        if (te instanceof IInventory)
        {
            return new InvWrapper((IInventory)te);
        }

        return null;
    }

    public static boolean canInsertStack(IItemHandler handler, @Nonnull ItemStack stack)
    {
        final ItemStack toInsert = ItemHandlerHelper.insertItemStacked(handler, stack, true);
        return toInsert.getCount() < stack.getCount();
    }
}
