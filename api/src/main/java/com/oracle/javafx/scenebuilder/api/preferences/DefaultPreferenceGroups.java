package com.oracle.javafx.scenebuilder.api.preferences;

public interface DefaultPreferenceGroups {
	public static String GROUP_NAME_EMPTY = "";
	
	public static final PreferenceGroup GLOBAL_GROUP_A = new PreferenceGroup("A", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_B = new PreferenceGroup("B", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_C = new PreferenceGroup("C", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_D = new PreferenceGroup("D", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_E = new PreferenceGroup("E", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_F = new PreferenceGroup("F", GROUP_NAME_EMPTY);
	
	public static final PreferenceGroup DOCUMENT_GROUP_A = new PreferenceGroup("A", GROUP_NAME_EMPTY);
	
	public class PreferenceGroup {
		private final String orderKey;
		private final String name;
		
		public PreferenceGroup(String orderKey, String name) {
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
			PreferenceGroup other = (PreferenceGroup) obj;
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
}
