package com.legionmodding.yalm.model.mulripart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.legionmodding.yalm.model.CustomBakedModel;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultipartGeometry implements IModelGeometry<MultipartGeometry>
{
    private final List<IUnbakedModel> parts;

    MultipartGeometry(List<IUnbakedModel> parts)
    {
        this.parts = parts;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
    {
        ImmutableList.Builder<IBakedModel> bakedParts = ImmutableList.builder();
        IModelTransform ownerTransform = owner.getCombinedTransform();

        for (IUnbakedModel part : parts)
        {
            bakedParts.add(part.bake(bakery, spriteGetter, ownerTransform, modelLocation));
        }

        return new RootBaked(owner, spriteGetter, ownerTransform, bakedParts.build());
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<RenderMaterial> textures = new HashSet<>();

        for (IUnbakedModel part : parts)
        {
            textures.addAll(part.getMaterials(modelGetter, missingTextureErrors));
        }
        return textures;
    }

    private static class CompositeBaked extends CustomBakedModel
    {
        protected final List<IBakedModel> bakedParts;

        public CompositeBaked(final IModelConfiguration owner, final Function<RenderMaterial, TextureAtlasSprite> spriteGetter, final IModelTransform transforms, List<IBakedModel> bakedParts)
        {
            super(owner, spriteGetter, transforms);
            this.bakedParts = bakedParts;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
        {
            List<BakedQuad> quads = Lists.newArrayList();

            for (IBakedModel model : bakedParts)
            {
                quads.addAll(model.getQuads(state, side, rand, EmptyModelData.INSTANCE));
            }

            return quads;
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.EMPTY;
        }
    }

    private static class RootBaked extends CompositeBaked
    {
        private final ItemOverrideList overrideList;

        public RootBaked(final IModelConfiguration owner, final Function<RenderMaterial, TextureAtlasSprite> spriteGetter, final IModelTransform transforms, List<IBakedModel> bakedParts)
        {
            super(owner, spriteGetter, transforms, bakedParts);

            overrideList = new ItemOverrideList()
            {
                @Nullable
                @Override
                public IBakedModel resolve(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity)
                {
                    List<IBakedModel> overriddenParts = bakedParts.stream().map(part -> part.getOverrides().resolve(part, stack, world, livingEntity)).collect(Collectors.toList());

                    if (overriddenParts.equals(bakedParts))
                    {
                        return RootBaked.this;
                    }

                    return new CompositeBaked(owner, spriteGetter, transforms, overriddenParts);
                }
            };
        }

        @Override
        public ItemOverrideList getOverrides()
        {
            return overrideList;
        }
    }
}
