package com.legionmodding.yalm.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BakedModelAdapter implements IBakedModel
{
    protected final IBakedModel base;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms;

    public BakedModelAdapter(IBakedModel base, Map<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms)
    {
        this.base = base;
        this.cameraTransforms = ImmutableMap.copyOf(cameraTransforms);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extData)
    {
        return base.getQuads(state, side, rand, extData);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return base.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return base.isGui3d();
    }

    @Override
    public boolean isCustomRenderer()
    {
        return base.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return base.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data)
    {
        return base.getParticleTexture(data);
    }

    @Override
    public ItemCameraTransforms getTransforms()
    {
        return ItemCameraTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return base.getOverrides();
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        return PerspectiveMapWrapper.handlePerspective(this, cameraTransforms, cameraTransformType, mat);
    }

    @Override
    public boolean usesBlockLight()
    {
        return base.usesBlockLight();
    }
}
