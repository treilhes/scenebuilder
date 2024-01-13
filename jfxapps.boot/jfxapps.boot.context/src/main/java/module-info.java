module jfxapps.boot.context {
    exports com.gluonhq.jfxapps.boot.context;
    exports com.gluonhq.jfxapps.boot.context.annotation;
    exports com.gluonhq.jfxapps.boot.context.config;

    exports com.gluonhq.jfxapps.boot.context.impl to spring.beans;

    opens com.gluonhq.jfxapps.boot.context.internal to spring.core;
    opens com.gluonhq.jfxapps.boot.context.config to spring.core;

    requires org.slf4j;
    requires spring.core;
    requires transitive spring.context;
    requires spring.beans;
    requires spring.expression;
    requires spring.aop;

    requires org.aspectj.weaver;

    requires jakarta.inject;
    requires jakarta.annotation;
    requires java.desktop;

}
