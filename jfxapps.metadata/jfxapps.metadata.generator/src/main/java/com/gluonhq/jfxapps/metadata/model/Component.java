package com.gluonhq.jfxapps.metadata.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;

public class Component implements Comparable<Component>{
    private Component parent;
    private final BeanMetaData<?> raw;
	private final Map<String, Object> custom = new HashMap<>();

	public Component(BeanMetaData<?> raw) {
		super();
		this.raw = raw;
	}

	public BeanMetaData<?> getRaw() {
        return raw;
    }

    public Map<String, Object> getCustom() {
        return custom;
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    @Override
	public int compareTo(Component o) {
		Comparator<Component> comparator = Comparator
		        .comparing((Component c) -> c.getRaw().getType().getSimpleName())
				.thenComparing((Component c) -> c.getRaw().getType().getName());
		return comparator.compare(this, o);
	}

}