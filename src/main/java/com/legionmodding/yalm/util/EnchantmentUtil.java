package com.legionmodding.yalm.util;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.List;

public class EnchantmentUtil
{
    /**
     * Be warned, minecraft doesn't update experienceTotal properly, so we have
     * to do this.
     *
     * @param player
     * @return
     */
    public static int getPlayerXP(PlayerEntity player)
    {
        return (int)(EnchantmentUtil.getExperienceForLevel(player.experienceLevel) + (player.totalExperience * player.experienceProgress));
    }

    public static void addPlayerXP(PlayerEntity player, int amount)
    {
        int experience = getPlayerXP(player) + amount;
        player.totalExperience = experience;
        player.experienceLevel = EnchantmentUtil.getLevelForExperience(experience);
        int expForLevel = EnchantmentUtil.getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (float)(experience - expForLevel) / (float)player.totalExperience;
    }

    public static int xpBarCap(int level)
    {
        if (level >= 30)
        {
            return 112 + (level - 30) * 9;
        }

        if (level >= 15)
        {
            return 37 + (level - 15) * 5;
        }

        return 7 + level * 2;
    }

    private static int sum(int n, int a0, int d)
    {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

    public static int getExperienceForLevel(int level)
    {
        if (level == 0) return 0;
        if (level <= 15) return sum(level, 7, 2);
        if (level <= 30) return 315 + sum(level - 15, 37, 5);
        return 1395 + sum(level - 30, 112, 9);
    }

    public static int getXpToNextLevel(int level)
    {
        int levelXP = EnchantmentUtil.getLevelForExperience(level);
        int nextXP = EnchantmentUtil.getExperienceForLevel(level + 1);
        return nextXP - levelXP;
    }

    public static int getLevelForExperience(int targetXp)
    {
        int level = 0;

        while (true)
        {
            final int xpToNextLevel = xpBarCap(level);
            if (targetXp < xpToNextLevel) return level;
            level++;
            targetXp -= xpToNextLevel;
        }
    }

    private static float getEnchantPower(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos)
    {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }

    public static float getPower(World world, BlockPos position)
    {
        float power = 0;

        for (int deltaZ = -1; deltaZ <= 1; ++deltaZ)
        {
            for (int deltaX = -1; deltaX <= 1; ++deltaX)
            {
                if ((deltaZ != 0 || deltaX != 0) && world.getBlockState(position).is(Blocks.AIR) && world.getBlockState(new BlockPos(deltaX, 1, deltaZ)).is(Blocks.AIR))
                {
                    power += getEnchantPower(world, position.cross(new Vector3i(deltaX * 2, 0, deltaZ * 2)));
                    power += getEnchantPower(world, position.cross(new Vector3i(deltaX * 2, 1, deltaZ * 2)));

                    if (deltaX != 0 && deltaZ != 0)
                    {
                        power += getEnchantPower(world, position.cross(new Vector3i(deltaX * 2, 0, deltaZ)));
                        power += getEnchantPower(world, position.cross(new Vector3i(deltaX * 2, 1, deltaZ)));
                        power += getEnchantPower(world, position.cross(new Vector3i(deltaX, 0, deltaZ * 2)));
                        power += getEnchantPower(world, position.cross(new Vector3i(deltaX, 1, deltaZ * 2)));
                    }
                }
            }
        }
        return power;
    }

    public static void addAllBooks(Enchantment enchantment, List<ItemStack> items)
    {
        for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++)
        {
            items.add(EnchantedBookItem.createForEnchantment(new EnchantmentData(enchantment, i)));
        }
    }
}
