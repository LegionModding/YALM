package com.legionmodding.yalm.model.textureditem;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TexturedItemOverrides extends ItemOverrideList
{
    private final IBakedModel untexturedModel;
    private final ResourceLocation texturedModelLocation;
    private final Set<ResourceLocation> placeholders;
    private final ModelBakery bakery;
    private final List<ItemOverride> texturedModelOverrides;

    private final LoadingCache<Pair<ResourceLocation, Optional<ResourceLocation>>, IBakedModel> textureOverrides = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).build(new CacheLoader<Pair<ResourceLocation, Optional<ResourceLocation>>, IBakedModel>()
    {
        @Override
        public IBakedModel load(Pair<ResourceLocation, Optional<ResourceLocation>> key)
        {
            final ResourceLocation modelToBake = key.getSecond().orElse(texturedModelLocation);
            IUnbakedModel template = bakery.getModel(modelToBake);
            return template.bake(bakery, renderMaterial ->
            {
                ResourceLocation actualSprite;
                if (placeholders.contains(renderMaterial.atlasLocation()))
                {
                    actualSprite = key.getFirst();
                }

                else
                {
                    actualSprite = renderMaterial.atlasLocation();
                }

                return Minecraft.getInstance().getTextureAtlas(renderMaterial.atlasLocation()).apply(actualSprite);
            }, ModelRotation.X0_Y0, modelToBake);
        }
    });

    public TexturedItemOverrides(IBakedModel untexturedModel, ResourceLocation texturedModelLocation, Set<ResourceLocation> placeholders, ModelBakery bakery, List<ItemOverride> texturedModelOverrides)
    {
        super();
        this.untexturedModel = untexturedModel;
        this.texturedModelLocation = texturedModelLocation;
        this.placeholders = placeholders;
        this.bakery = bakery;
        this.texturedModelOverrides = texturedModelOverrides;
    }

    @Nullable
    @Override
    public IBakedModel resolve(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity)
    {
        final Optional<ResourceLocation> texture = getTextureFromStack(stack);
        return texture.map(t -> getTexturedModel(t, stack, world, livingEntity)).orElse(untexturedModel);
    }

    private IBakedModel getTexturedModel(ResourceLocation texture, @Nonnull ItemStack stack, ClientWorld world, LivingEntity entity)
    {
        final Optional<ResourceLocation> overrideLocation = applyOverride(stack, world, entity);
        return textureOverrides.getUnchecked(Pair.of(texture, overrideLocation));
    }

    private Optional<ResourceLocation> applyOverride(ItemStack stack, ClientWorld world, LivingEntity entity)
    {
        // TODO 1.14 AT transform
        for (ItemOverride override : texturedModelOverrides)
        {
            return Optional.of(override.getModel());
        }

        return Optional.empty();
    }

    private static Optional<ResourceLocation> getTextureFromStack(ItemStack stack)
    {
        return stack.getCapability(ItemTextureCapability.CAPABILITY, null).resolve().flatMap(IItemTexture::getTexture);
    }
}
