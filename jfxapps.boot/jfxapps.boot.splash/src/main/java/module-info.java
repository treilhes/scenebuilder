import com.gluonhq.jfxapps.boot.api.loader.BootContextConfigClasses;
import com.gluonhq.jfxapps.boot.splash.SplashBootClasses;

module jfxapps.boot.splash {
	exports com.gluonhq.jfxapps.boot.splash;
	exports com.gluonhq.jfxapps.boot.splash.impl;

	requires transitive jfxapps.boot.api;

    requires jfxapps.boot.starter;

    provides BootContextConfigClasses with SplashBootClasses;
}