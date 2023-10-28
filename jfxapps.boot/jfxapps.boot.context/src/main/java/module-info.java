module jfxapps.boot.context {
    exports com.gluonhq.jfxapps.boot.context;
    exports com.gluonhq.jfxapps.boot.context.annotation;

    exports com.gluonhq.jfxapps.boot.context.impl to jfxapps.boot.loader;
    opens com.gluonhq.jfxapps.boot.context.internal to spring.core;

    requires org.slf4j;
    requires spring.core;
    requires transitive spring.context;
    requires spring.beans;
    requires spring.expression;
    requires spring.aop;
    requires jakarta.inject;
    requires jakarta.annotation;
    requires java.desktop;

}
