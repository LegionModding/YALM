package com.legionmodding.yalm.api;

@FunctionalInterface
public interface IValueReceiver<T> 
{
	void setValue(T value);
}
