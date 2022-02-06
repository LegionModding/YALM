package com.legionmodding.yalm.fluid;

import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public class ConversionFactory
{
    public final FluidStack fluid;
    public final IExperienceConverter converter;
    public final Optional<IExperienceConverter> optionalConverter;

    public ConversionFactory(FluidStack fluid, IExperienceConverter converter)
    {
        this.fluid = fluid;
        this.converter = converter;
        this.optionalConverter = Optional.of(converter);
    }

    public boolean matches(FluidStack input)
    {
        return fluid.isFluidEqual(input);
    }
}
