package com.legionmodding.yalm.api;

import info.openmods.calc.utils.OptionalInt;

import net.minecraft.inventory.IInventory;


public interface IInventoryCallback 
{
	void onInventoryChanged(IInventory inventory, OptionalInt slotNumber);
}
