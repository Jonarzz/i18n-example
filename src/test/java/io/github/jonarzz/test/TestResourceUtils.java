package io.github.jonarzz.test;

import io.vavr.control.Either;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class TestResourceUtils {

    private TestResourceUtils() {
    }

    public static File createResourceDirectory(String subdirectoryName)
            throws URISyntaxException {
        URI resourcesUri = TestResourceUtils.class.getClassLoader()
                                                  .getResource(".")
                                                  .toURI();
        File resourceDirectory = new File(resourcesUri);
        File subdirectory = new File(resourceDirectory, subdirectoryName);
        if (!subdirectory.exists() && !subdirectory.mkdir()) {
            throw new RuntimeException("Could not create resource subdirectory " + subdirectoryName);
        }
        return subdirectory;
    }

    public static void createResource(File directory, String fileName, Iterable<String> fileContentLines)
            throws IOException {
        File file = new File(directory, fileName);
        if (!file.exists() && !file.createNewFile()) {
            throw new RuntimeException("Could not create file " + fileName);
        }
        Files.write(file.toPath(), fileContentLines);
    }

    public static URL getResource(String directoryPath, String resourcePath) {
        directoryPath = directoryPath.replaceAll("[/\\\\]$", "");
        return TestResourceUtils.class.getClassLoader()
                                      .getResource(directoryPath + "/" + resourcePath);
    }

    // vavr's Either: https://www.javadoc.io/doc/io.vavr/vavr/latest/io/vavr/control/Either.html
    public static Either<AssertionFailedError, File> getResourceFileForAssertion(
            String directoryPath, String resourcePath) {
        URL resource = getResource(directoryPath, resourcePath);
        if (resource == null) {
            return Either.left(new AssertionFailedError(
                    resourcePath + " file does not exist in " + directoryPath + "/ resource directory"
            ));
        }
        try {
            return Either.right(new File(resource.toURI()));
        } catch (URISyntaxException e) {
            return Either.left(new AssertionFailedError(e.getMessage()));
        }
    }

}
