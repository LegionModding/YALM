package com.legionmodding.yalm.fluid;

public class Linear implements IExperienceConverter
{
    private final int xpToFluid;

    public Linear(int xpToFluid)
    {
        this.xpToFluid = xpToFluid;
    }

    @Override
    public int fluidToExperience(int fluid)
    {
        return fluid / xpToFluid;
    }

    @Override
    public int experienceToFluid(int xp)
    {
        return xp * xpToFluid;
    }
}
