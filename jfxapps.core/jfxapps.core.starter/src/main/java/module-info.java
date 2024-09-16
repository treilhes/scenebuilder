module jfxapps.core.starter {

    requires transitive jfxapps.javafx.starter;

    requires transitive jfxapps.boot.starter;

    requires transitive jfxapps.boot.api;
    //requires transitive jfxapps.boot.loader;
    //requires transitive jfxapps.boot.context;
    //requires transitive jfxapps.boot.platform;

    requires transitive org.slf4j;
    requires transitive io.reactivex.rxjava3;
    requires transitive org.reactivestreams;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive org.pdfsam.rxjavafx;

    requires transitive jakarta.annotation;
    requires transitive jakarta.persistence;
    requires transitive jakarta.validation;

    requires transitive org.aspectj.weaver;

    requires transitive org.hibernate.validator;

    requires transitive java.prefs;


}