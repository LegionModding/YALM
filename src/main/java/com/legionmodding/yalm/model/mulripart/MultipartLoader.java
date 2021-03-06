package com.legionmodding.yalm.model.mulripart;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.client.model.IModelLoader;

import java.util.Map;

public class MultipartLoader implements IModelLoader<MultipartGeometry>
{
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {

    }

    @Override
    public MultipartGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
    {
        JsonObject partData = JSONUtils.getAsJsonObject(modelContents, "parts");
        ImmutableList.Builder<IUnbakedModel> parts = ImmutableList.builder();

        for (Map.Entry<String, JsonElement> part : partData.entrySet())
        {
            BlockModel partModel = deserializationContext.deserialize(part.getValue(), BlockModel.class);
            parts.add(partModel);
        }
        return new MultipartGeometry(parts.build());
    }
}
