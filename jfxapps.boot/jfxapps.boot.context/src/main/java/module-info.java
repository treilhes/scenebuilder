module scenebuilder.boot.context {
    exports com.oracle.javafx.scenebuilder.core.context;
    exports com.oracle.javafx.scenebuilder.core.context.annotation;

    exports com.oracle.javafx.scenebuilder.core.context.impl to scenebuilder.boot.loader;
    opens com.oracle.javafx.scenebuilder.core.context.internal to spring.core;

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
