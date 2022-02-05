package com.legionmodding.yalm;

import com.legionmodding.yalm.model.mulripart.MultipartLoader;
import com.legionmodding.yalm.model.textureditem.ItemTextureCapability;
import com.legionmodding.yalm.model.textureditem.TexturedItemModelLoader;
import com.legionmodding.yalm.model.variant.VariantModelLoader;
import com.legionmodding.yalm.util.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.MOD_ID)
public class YALM
{
    private static final Logger LOGGER = LogManager.getLogger();

    public YALM()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        ItemTextureCapability.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {

    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent evt)
    {
        ModelLoaderRegistry.registerLoader(new ResourceLocation("variant"), new VariantModelLoader());
        ModelLoaderRegistry.registerLoader(new ResourceLocation("textured_item"), new TexturedItemModelLoader());
        ModelLoaderRegistry.registerLoader(new ResourceLocation("multipart"), new MultipartLoader());
    }
}