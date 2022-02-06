package com.legionmodding.yalm.util;

import com.legionmodding.yalm.inventory.IInventoryProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Orientation;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

import static com.legionmodding.yalm.geometry.HalfAxis.*;

public class BlockUtil
{
    @Deprecated
    public static Direction get2dOrientation(LivingEntity entity)
    {
        return entity.getDirection().getOpposite();
    }

    public static float getRotationFromDirection(Direction direction)
    {
        switch (direction)
        {
            case NORTH:
                return 180F;
            case SOUTH:
                return 0F;
            case WEST:
                return 90F;
            case EAST:
                return -90F;
            case DOWN:
                return -90f;
            case UP:
                return 90f;
            default:
                return 0f;
        }
    }

    public static Direction get3dOrientation(LivingEntity entity, BlockPos pos)
    {
        if (MathHelper.abs((float)entity.position().x - pos.getX()) < 2.0F && MathHelper.abs((float)entity.position().z - pos.getZ()) < 2.0F)
        {
            final double entityEyes = entity.getEyeY();
            if (entityEyes - pos.getY() > 2.0D) return Direction.DOWN;
            if (pos.getY() - entityEyes > 0.0D) return Direction.UP;
        }

        return entity.getDirection().getOpposite();
    }

    public static ItemEntity dropItemStackInWorld(World worldObj, Vector3i pos, @Nonnull ItemStack stack)
    {
        return dropItemStackInWorld(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    public static ItemEntity dropItemStackInWorld(World worldObj, double x, double y, double z, @Nonnull ItemStack stack)
    {
        float f = 0.7F;
        float d0 = worldObj.random.nextFloat() * f + (1.0F - f) * 0.5F;
        float d1 = worldObj.random.nextFloat() * f + (1.0F - f) * 0.5F;
        float d2 = worldObj.random.nextFloat() * f + (1.0F - f) * 0.5F;
        ItemEntity entityitem = new ItemEntity(worldObj, x + d0, y + d1, z + d2, stack);

        entityitem.setDefaultPickUpDelay();

        if (stack.hasTag())
        {
            entityitem.getItem().setTag(stack.getTag().copy());
        }

        worldObj.addFreshEntity(entityitem);
        return entityitem;
    }

    public static ItemEntity ejectItemInDirection(World world, double x, double y, double z, Direction direction, @Nonnull ItemStack stack)
    {
        ItemEntity item = BlockUtil.dropItemStackInWorld(world, x, y, z, stack);
        final Vector3i v = direction.getNormal();
        item.setDeltaMovement(v.getX() / 5F, v.getY() / 5F, v.getZ() / 5F);
        return item;
    }

    public static boolean getTileInventoryDrops(TileEntity tileEntity, List<ItemStack> drops)
    {
        if (tileEntity == null)
        {
            return false;
        }

        if (tileEntity instanceof IInventory)
        {
            drops.addAll(InventoryUtil.getInventoryContents((IInventory)tileEntity));
            return true;
        }

        else if (tileEntity instanceof IInventoryProvider)
        {
            drops.addAll(InventoryUtil.getInventoryContents(((IInventoryProvider)tileEntity).getInventory()));
            return true;
        }

        return false;
    }

    public static void dropInventory(IInventory inventory, World world, double x, double y, double z)
    {
        if (inventory == null)
        {
            return;
        }

        for (int i = 0; i < inventory.getContainerSize(); ++i)
        {
            ItemStack itemStack = inventory.getItem(i);

            if (!itemStack.isEmpty())
            {
                dropItemStackInWorld(world, x, y, z, itemStack);
            }
        }
    }

    public static void dropInventory(IInventory inventory, World world, int x, int y, int z)
    {
        dropInventory(inventory, world, x + 0.5, y + 0.5, z + 0.5);
    }


    public static TileEntity getTileInDirection(World world, BlockPos coord, Direction direction)
    {
        Vector3i get = new Vector3i(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        return world.getBlockEntity(coord.offset(get));
    }

    public static TileEntity getTileInDirectionSafe(World world, BlockPos coord, Direction direction)
    {
        Vector3i get = new Vector3i(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        BlockPos n = coord.offset(get);
        return world.isLoaded(n)? world.getBlockEntity(n) : null;
    }

    public static AxisAlignedBB expandAround(BlockPos pos, int x, int y, int z)
    {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    public static AxisAlignedBB expandAround(BlockPos pos, double x, double y, double z)
    {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    public static AxisAlignedBB aabbOffset(BlockPos pos, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return new AxisAlignedBB(pos.getX() + x1, pos.getY() + y1, pos.getZ() + z1, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2);
    }

    public static AxisAlignedBB aabbOffset(BlockPos pos, AxisAlignedBB aabb)
    {
        return aabb.inflate(pos.getX(), pos.getY(), pos.getZ());
    }

    public static AxisAlignedBB singleBlock(BlockPos pos)
    {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public static void playSoundAtPos(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch)
    {
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, category, volume, pitch);
    }
}
