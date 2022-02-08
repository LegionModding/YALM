package com.legionmodding.yalm.api;

@FunctionalInterface
public interface IValueProvider<T> 
{
	T getValue();
}
