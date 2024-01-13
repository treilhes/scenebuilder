package com.oracle.javafx.scenebuilder.maven.metadata;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.oracle.javafx.scenebuilder.metadata.bean.BeanMetaData;
import com.oracle.javafx.scenebuilder.metadata.finder.ClassCrawler;
import com.oracle.javafx.scenebuilder.metadata.finder.JarFinder;
import com.oracle.javafx.scenebuilder.metadata.finder.SearchContext;
import com.oracle.javafx.scenebuilder.metadata.model.Descriptor;
import com.oracle.javafx.scenebuilder.metadata.util.Report;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Cell;
import javafx.stage.Stage;

class CrawlClassTest {

    public static class DummyApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // noop
        }
    }

    @BeforeAll
    public static void initJFX() {
        Thread t = new Thread("JavaFX Init Thread") {
            @Override
            public void run() {
                Application.launch(DummyApp.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
    }

    @Test
    void test() {
        SearchContext searchContext = new SearchContext();//prepareParameters();

        searchContext.addRootClass(javafx.scene.Node.class);
        searchContext.addRootClass(javafx.scene.Scene.class);
        searchContext.addRootClass(javafx.scene.control.MenuItem.class);
        searchContext.addRootClass(javafx.scene.control.Tab.class);
        searchContext.addRootClass(javafx.scene.control.TableColumnBase.class);
        searchContext.addRootClass(javafx.scene.control.TextFormatter.class);
        //searchContext.addRootClass(javafx.scene.layout.ColumnConstraints.class);
        //searchContext.addRootClass(javafx.scene.layout.RowConstraints.class);
        searchContext.addRootClass(javafx.scene.layout.ConstraintsBase.class);
        searchContext.addRootClass(javafx.scene.shape.PathElement.class);
        searchContext.addRootClass(javafx.stage.Window.class);

        searchContext.addExcludeClass(Cell.class);

        searchContext.addJarFilterPattern(Pattern.compile(".*[/\\\\](javafx-(.*?))[/\\\\].*"));

        searchContext.addIncludedPackage("javafx");

        searchContext.addExcludedPackage("javafx.scene.control.skin");

        try {
            searchContext.addAltConstructor(BubbleChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {NumberAxis.class, NumberAxis.class});
            searchContext.addAltConstructor(StackedBarChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {CategoryAxis.class, NumberAxis.class});
            searchContext.addAltConstructor(StackedAreaChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {Axis.class, ValueAxis.class});
            searchContext.addAltConstructor(BarChart.class.getConstructor(Axis.class, Axis.class), new Class<?>[] {CategoryAxis.class, NumberAxis.class});
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Descriptor> descriptors = new ArrayList<>();
        Set<Path> jars = JarFinder.listJarsInClasspath(searchContext.getJarFilterPatterns(), descriptors);
        ClassCrawler crawler = new ClassCrawler();

        final CompletableFuture<Map<Class<?>, BeanMetaData<?>>> returnValue = new CompletableFuture<>();

        Platform.runLater(() -> {
            crawler.crawl(jars, searchContext);
            returnValue.complete(crawler.getClasses());
        });

        try {
            Map<Class<?>, BeanMetaData<?>> found = returnValue.get();
            for (Class<?> cls:found.keySet()) {
                System.out.println(cls.getSimpleName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean hasError = Report.flush(true);

        assertTrue(!hasError, "No error must be encountered");
    }

}