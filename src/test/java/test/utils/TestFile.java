package test.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class TestFile extends File {

    public TestFile(String resourceName) throws IOException {
        super(resolveResource(resourceName));
    }

    private static String resolveResource(String resourceName) throws IOException {
        try {
            ClassLoader classLoader = TestFile.class.getClassLoader();
            URL resource = classLoader.getResource(resourceName);

            if (resource == null) {
                throw new IOException("Test resource not found: " + resourceName);
            }

            return new File(resource.toURI()).getAbsolutePath();

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI for test resource: " + resourceName, e);
        }
    }

}
