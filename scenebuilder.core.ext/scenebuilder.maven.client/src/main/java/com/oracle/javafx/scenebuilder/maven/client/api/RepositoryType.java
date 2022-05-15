package com.oracle.javafx.scenebuilder.maven.client.api;

import com.oracle.javafx.scenebuilder.maven.client.search.Search;

public interface RepositoryType extends Search {
    boolean validate(Repository repository);

}
