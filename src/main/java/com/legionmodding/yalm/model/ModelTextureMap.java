package com.legionmodding.yalm.model;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelTextureMap
{
    private final Map<String, ResourceLocation> textures;

    public ModelTextureMap()
    {
        this(ImmutableMap.of());
    }

    private ModelTextureMap(Map<String, ResourceLocation> textures)
    {
        this.textures = ImmutableMap.copyOf(textures);
    }

    public Collection<ResourceLocation> getTextures()
    {
        return textures.values();
    }

    public Optional<ModelTextureMap> update(Map<String, String> updates)
    {
        if (updates.isEmpty()) return Optional.empty();

        final Map<String, ResourceLocation> newTextures = Maps.newHashMap(this.textures);

        for (Map.Entry<String, String> e : updates.entrySet())
        {
            final String location = e.getValue();

            if (Strings.isNullOrEmpty(location))
            {
                newTextures.remove(e.getKey());
            }

            else
            {
                newTextures.put(e.getKey(), new ResourceLocation(location));
            }
        }

        return Optional.of(new ModelTextureMap(newTextures));
    }

    public Iterable<TextureAtlasSprite> bake(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        return textures.values().stream().map(bakedTextureGetter).collect(Collectors.toSet());
    }

    public Map<String, TextureAtlasSprite> bakeWithKeys(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        return textures.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> bakedTextureGetter.apply(e.getValue())));

    }
}
