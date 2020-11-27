package com.oracle.javafx.scenebuilder.api.util;

public class OrderedGroup {
	private final String orderKey;
	private final String name;

	public OrderedGroup(String orderKey, String name) {
		super();
		this.orderKey = orderKey;
		this.name = name;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((orderKey == null) ? 0 : orderKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderedGroup other = (OrderedGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (orderKey == null) {
			if (other.orderKey != null)
				return false;
		} else if (!orderKey.equals(other.orderKey))
			return false;
		return true;
	}

}
