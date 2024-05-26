package com.gluonhq.jfxapps.metadata.finder.api;

import java.nio.file.Path;
import java.util.Set;

public interface IClassCrawler {

    Set<Class<?>> crawl(Set<Path> jars, SearchContext context);

}