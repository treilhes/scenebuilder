package com.oracle.javafx.scenebuilder.api.util;

public class OrderedItem<T> {

	private final OrderedGroup group;
	private final String orderKey;
	private final String name;
	private final T item;

	public OrderedItem(OrderedGroup group, String orderKey, String name, T item) {
		super();
		this.group = group;
		this.orderKey = orderKey;
		this.name = name;
		this.item = item;
	}

	public OrderedGroup getGroup() {
		return group;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public String getName() {
		return name;
	}

	public T getItem() {
		return item;
	}

}
