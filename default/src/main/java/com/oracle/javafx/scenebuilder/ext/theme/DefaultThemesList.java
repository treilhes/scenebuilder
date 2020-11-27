package com.oracle.javafx.scenebuilder.ext.theme;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.theme.AbstractTheme;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeMeta;
import com.oracle.javafx.scenebuilder.api.theme.ThemeProvider;
import com.oracle.javafx.scenebuilder.ext.theme.group.CaspianGroup;
import com.oracle.javafx.scenebuilder.ext.theme.group.ModenaGroup;

@Component
@Qualifier("default")
public class DefaultThemesList implements ThemeProvider {

	public DefaultThemesList() {}

	@Override
	public List<Class<? extends Theme>> themes() {
		return Arrays.asList(
				Modena.class,
				ModenaHighContrastBlackOnWhite.class,
				ModenaHighContrastWhiteOnBlack.class,
				ModenaHighContrastYellowOnBlack.class,
				ModenaTouch.class,
				ModenaTouchHighContrastBlackOnWhite.class,
				ModenaTouchHighContrastWhiteOnBlack.class,
				ModenaTouchHighContrastYellowOnBlack.class,
				Caspian.class,
				CaspianHighContrast.class,
				CaspianEmbedded.class,
				CaspianEmbeddedHighContrast.class,
				CaspianEmbeddedQvga.class,
				CaspianEmbeddedQvgaHighContrast.class
				);
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena", group = ModenaGroup.class)
	public static class Modena extends AbstractTheme {
		public Modena() {
			super("com/sun/javafx/scene/control/skin/modena/modena.bss", Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena_touch", group = ModenaGroup.class)
	public static class ModenaTouch extends AbstractTheme {
		public ModenaTouch() {
			super("com/oracle/javafx/scenebuilder/theme/modena/modena-touch.css", Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena_high_contrast_black_on_white", group = ModenaGroup.class)
	public static class ModenaHighContrastBlackOnWhite extends AbstractTheme {
		public ModenaHighContrastBlackOnWhite() {
			super("com/oracle/javafx/scenebuilder/theme/modena/modena-highContrast-blackOnWhite.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena_high_contrast_white_on_black", group = ModenaGroup.class)
	public static class ModenaHighContrastWhiteOnBlack extends AbstractTheme {
		public ModenaHighContrastWhiteOnBlack() {
			super("com/oracle/javafx/scenebuilder/theme/modena/modena-highContrast-whiteOnBlack.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena_high_contrast_yellow_on_black", group = ModenaGroup.class)
	public static class ModenaHighContrastYellowOnBlack extends AbstractTheme {
		public ModenaHighContrastYellowOnBlack() {
			super("com/oracle/javafx/scenebuilder/theme/modena/modena-highContrast-yellowOnBlack.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena_touch_high_contrast_black_on_white", group = ModenaGroup.class)
	public static class ModenaTouchHighContrastBlackOnWhite extends AbstractTheme {
		public ModenaTouchHighContrastBlackOnWhite() {
			super("com/oracle/javafx/scenebuilder/theme/modena/modena-touch-highContrast-blackOnWhite.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena_touch_high_contrast_white_on_black", group = ModenaGroup.class)
	public static class ModenaTouchHighContrastWhiteOnBlack extends AbstractTheme {
		public ModenaTouchHighContrastWhiteOnBlack() {
			super("com/oracle/javafx/scenebuilder/theme/modena/modena-touch-highContrast-whiteOnBlack.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.modena_touch_high_contrast_yellow_on_black", group = ModenaGroup.class)
	public static class ModenaTouchHighContrastYellowOnBlack extends AbstractTheme {
		public ModenaTouchHighContrastYellowOnBlack() {
			super("com/oracle/javafx/scenebuilder/theme/modena/modena-touch-highContrast-yellowOnBlack.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.caspian", group = CaspianGroup.class)
	public static class Caspian extends AbstractTheme {
		public Caspian() {
			super("com/sun/javafx/scene/control/skin/caspian/caspian.bss", Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.caspian_high_contrast", group = CaspianGroup.class)
	public static class CaspianHighContrast extends AbstractTheme {
		public CaspianHighContrast() {
			super("com/oracle/javafx/scenebuilder/theme/caspian/caspian-highContrast.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.caspian_embedded", group = CaspianGroup.class)
	public static class CaspianEmbedded extends AbstractTheme {
		public CaspianEmbedded() {
			super("com/oracle/javafx/scenebuilder/theme/caspian/caspian-embedded.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.caspian_embedded_high_contrast", group = CaspianGroup.class)
	public static class CaspianEmbeddedHighContrast extends AbstractTheme {
		public CaspianEmbeddedHighContrast() {
			super("com/oracle/javafx/scenebuilder/theme/caspian/caspian-embedded-highContrast.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.caspian_embedded_qvga", group = CaspianGroup.class)
	public static class CaspianEmbeddedQvga extends AbstractTheme {
		public CaspianEmbeddedQvga() {
			super("com/oracle/javafx/scenebuilder/theme/caspian/caspian-embedded-qvga.css",
					Arrays.asList());
		}
	}

	@Component
	@Lazy
	@ThemeMeta(name = "title.theme.caspian_embedded_qvga_high_contrast", group = CaspianGroup.class)
	public static class CaspianEmbeddedQvgaHighContrast extends AbstractTheme {
		public CaspianEmbeddedQvgaHighContrast() {
			super("com/oracle/javafx/scenebuilder/theme/caspian/caspian-embedded-qvga-highContrast.css",
					Arrays.asList());
		}
	}



}
