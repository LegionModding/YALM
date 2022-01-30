package com.legionmodding.yalm.model.textureditem;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;

public class TexturedItemModelLoader implements IModelLoader<TexturedItemModelGeometry>
{
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) 
    {
    }

    @Override
    public TexturedItemModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) 
    {
        ResourceLocation untexturedModel = new ResourceLocation(JSONUtils.getAsString(modelContents, "untexturedModel"));
        ResourceLocation texturedModel = new ResourceLocation(JSONUtils.getAsString(modelContents, "texturedModel"));
        ResourceLocation placeholder = new ResourceLocation(JSONUtils.getAsString(modelContents, "placeholder"));
        return new TexturedItemModelGeometry(untexturedModel, texturedModel, ImmutableSet.of(placeholder));
    }
}
