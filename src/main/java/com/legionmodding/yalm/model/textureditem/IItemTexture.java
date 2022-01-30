package com.legionmodding.yalm.model.textureditem;

import net.minecraft.util.ResourceLocation;

import java.util.Optional;

@FunctionalInterface
public interface IItemTexture
{
    Optional<ResourceLocation> getTexture();
}
