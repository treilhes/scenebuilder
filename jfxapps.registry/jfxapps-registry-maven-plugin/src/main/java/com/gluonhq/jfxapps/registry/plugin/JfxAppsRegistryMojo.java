package com.gluonhq.jfxapps.registry.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.gluonhq.jfxapps.registry.mapper.Mapper;
import com.gluonhq.jfxapps.registry.mapper.impl.JsonMapper;
import com.gluonhq.jfxapps.registry.mapper.impl.XmlMapper;
import com.gluonhq.jfxapps.registry.model.Dependency;
import com.gluonhq.jfxapps.registry.model.JfxApps;
import com.gluonhq.jfxapps.registry.model.Registry;

@Mojo(name = "jfxappsRegistry", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, configurator = "jfxapps-mojo-component-configurator")
public class JfxAppsRegistryMojo extends AbstractMojo {

    private static String GENERATED_RESOURCES_FOLDER = "registry-maven-plugin";

    /** The registry. */
    @Parameter(property = "registry", required = true, alias = "registry")
    Registry registry;

    @Parameter(property = "format", required = false, alias = "format", defaultValue = "xml")
    Format format;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-resources")
    private File outputDirectory;

    public enum Format {
        xml, json
    }

    /**
     * Default constructor.
     */
    public JfxAppsRegistryMojo() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException {
        try {

            Mapper mapper = switch (format) {
            case xml: {
                yield new XmlMapper();
            }
            case json: {
                yield new JsonMapper();
            }
            default:
                throw new IllegalArgumentException("Unexpected format: " + format);
            };

            File resourceFolder = new File(outputDirectory, GENERATED_RESOURCES_FOLDER);
            File registryFile = new File(resourceFolder, JfxApps.REGISTRY_FILE_NAME + "." + format);

            String projectPath = project.getBasedir().getAbsolutePath();
            String resourcePath = resourceFolder.getAbsolutePath();
            String relativePath = resourcePath.replace(projectPath + File.separator, "");

            final Resource resource = new Resource();
            resource.setDirectory(relativePath);
            project.getBuild().getResources().add(resource);

            if (!resourceFolder.exists()) {
                resourceFolder.mkdirs();
            }

            getLog().info(relativePath);
            getLog().info(registryFile.getAbsolutePath());
            getLog().info(outputDirectory.getAbsolutePath());

            if (registry.getDependency() == null) {
                registry.setDependency(new Dependency());
            }
            if (registry.getDependency().getGroupId() == null) {
                registry.getDependency().setGroupId(project.getGroupId());
            }
            if (registry.getDependency().getArtifactId() == null) {
                registry.getDependency().setArtifactId(project.getArtifactId());
            }
            if (registry.getDependency().getVersion() == null) {
                registry.getDependency().setVersion(project.getVersion());
            }

            try (OutputStream output = new FileOutputStream(registryFile)) {
                mapper.to(registry, output);
            }

        } catch (Exception e) {
            getLog().error("Failed to complete the generation process! " + e.getMessage(), e);
            throw new MojoExecutionException("Failed to complete the generation process!", e);
        }

    }

}
