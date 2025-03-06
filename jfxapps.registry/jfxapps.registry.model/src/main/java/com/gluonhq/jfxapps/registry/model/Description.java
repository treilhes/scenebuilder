/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.registry.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

// TODO: Auto-generated Javadoc
/**
 * The Class Description.
 */
@JsonInclude(Include.NON_NULL)
public class Description {

	/** The image. */
	private String splash;

	/** The image. */
	private String image;

	/** The title. */
	private String title;

	/** The text. */
	private String text;

	/** The changelog. */
	private String changelog;

	/** The i18n file. */
	private String i18n;


	/**
	 * Instantiates a new description.
	 *
	 * @param icon  the icon
	 * @param title the title
	 * @param text  the text
	 */
	public Description(String title, String text) {
		this.title = title;
		this.text = text;
	}

	/**
	 * Instantiates a new description.
	 */
	public Description() {

	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the changelog.
	 *
	 * @return the changelog
	 */
	public String getChangelog() {
		return changelog;
	}

	/**
	 * Sets the changelog.
	 *
	 * @param changelog the new changelog
	 */
	public void setChangelog(String changelog) {
		this.changelog = changelog;
	}

	public String getI18n() {
		return i18n;
	}

	public void setI18n(String i18n) {
		this.i18n = i18n;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSplash() {
		return splash;
	}

	public void setSplash(String splash) {
		this.splash = splash;
	}

	@Override
	public int hashCode() {
		return Objects.hash(changelog, i18n, image, splash, text, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Description other = (Description) obj;
		return Objects.equals(changelog, other.changelog) && Objects.equals(i18n, other.i18n)
				&& Objects.equals(image, other.image) && Objects.equals(splash, other.splash)
				&& Objects.equals(text, other.text) && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "Description [splash=" + splash + ", image=" + image + ", title=" + title + ", text="
				+ text + ", changelog=" + changelog + ", i18n=" + i18n + "]";
	}

}
