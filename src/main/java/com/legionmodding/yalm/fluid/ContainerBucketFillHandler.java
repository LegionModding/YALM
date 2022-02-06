package com.legionmodding.yalm.fluid;

import com.google.common.collect.Lists;
import com.legionmodding.yalm.util.Constants;
import com.mojang.datafixers.util.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import org.jline.utils.Log;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public abstract class ContainerBucketFillHandler
{
    private static final Item EMPTY_BUCKET = null;

    protected static boolean canFill(World world, BlockPos pos, TileEntity te) {
        return false;
    }

    private static final List<Pair<FluidStack, ItemStack>> buckets = Lists.newArrayList();

    public ContainerBucketFillHandler addFilledBucket(ItemStack filledBucket)
    {
        Optional<FluidStack> containedFluid = FluidUtil.getFluidContained(filledBucket);

        if (containedFluid != null)
        {
            buckets.add(Pair.of(containedFluid.get().copy(), filledBucket.copy()));
        }

        else
        {
            Log.warn("Item %s is not a filled bucket", filledBucket);
        }

        return this;
    }

    @SubscribeEvent
    public static void onBucketFill(FillBucketEvent evt)
    {
        if (evt.getResult() != Event.Result.DEFAULT)
        {
            return;
        }

        if (evt.getEmptyBucket().getItem() != EMPTY_BUCKET)
        {
            return;
        }

        final RayTraceResult target = evt.getTarget();

        if (target == null || target.getType() != RayTraceResult.Type.BLOCK)
        {
            return;
        }

        final TileEntity te = evt.getWorld().getBlockEntity(new BlockPos(target.getLocation().x, target.getLocation().y(), target.getLocation().z()));

        if (te == null)
        {
            return;
        }

        if (!canFill(evt.getWorld(), new BlockPos(target.getLocation().x, target.getLocation().y, target.getLocation().z), te))
        {
            return;
        }

        if (te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent())
        {
            final IFluidHandler source = (IFluidHandler) te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

            final FluidStack fluidInContainer = source.drain(1000, IFluidHandler.FluidAction.EXECUTE);

            if (fluidInContainer != null)
            {
                final ItemStack filledBucket = getFilledBucket(fluidInContainer);

                if (!filledBucket.isEmpty())
                {
                    final IFluidHandlerItem container = (IFluidHandlerItem) FluidUtil.getFluidHandler(filledBucket);

                    if (container != null)
                    {
                        final FluidStack fluidInBucket = container.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

                        if (fluidInBucket != null && fluidInBucket.isFluidStackIdentical(source.drain(fluidInBucket, IFluidHandler.FluidAction.EXECUTE)))
                        {
                            source.drain(fluidInBucket, IFluidHandler.FluidAction.EXECUTE);
                            evt.setFilledBucket(filledBucket.copy());
                            evt.setResult(Event.Result.ALLOW);
                        }
                    }
                }
            }
        }
    }

    private static ItemStack getFilledBucket(FluidStack fluid)
    {
        for (Pair<FluidStack, ItemStack> e : buckets)
        {
            if (e.getFirst().isFluidEqual(fluid))
            {
                return e.getSecond();
            }
        }

        return FluidUtil.getFilledBucket(fluid);
    }
}
