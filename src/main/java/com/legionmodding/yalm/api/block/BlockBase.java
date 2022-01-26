package com.legionmodding.yalm.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlockBase extends Block
{
    public BlockBase(Material material, int harvestLevel, ToolType harvestTool, float hardness, float resistance, SoundType soundType)
    {
        super(Block.Properties.of(material).harvestLevel(harvestLevel).harvestTool(harvestTool).strength(hardness, resistance).sound(soundType));
    }
}
