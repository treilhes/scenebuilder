/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.core.api.preferences;

public interface DefaultPreferenceGroups {
	public static String GROUP_NAME_EMPTY = "";

	public static final PreferenceGroup GLOBAL_GROUP_A = new PreferenceGroup("A", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_B = new PreferenceGroup("B", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_C = new PreferenceGroup("C", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_D = new PreferenceGroup("D", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_E = new PreferenceGroup("E", GROUP_NAME_EMPTY);
	public static final PreferenceGroup GLOBAL_GROUP_F = new PreferenceGroup("F", GROUP_NAME_EMPTY);

	public static final PreferenceGroup GLOBAL_GROUP_G = new PreferenceGroup("G", GROUP_NAME_EMPTY);

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
