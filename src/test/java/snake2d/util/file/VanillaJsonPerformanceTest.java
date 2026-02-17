package snake2d.util.file;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vanilla Json Performance Tests")
public class VanillaJsonPerformanceTest {

    @TempDir
    static Path tempDir;

    private static Path smallConfigFile;
    private static Path mediumConfigFile;
    private static Path largeConfigFile;
    private static Path deeplyNestedFile;

    @BeforeAll
    static void setUp() throws IOException {
        // Small configuration (10 keys)
        smallConfigFile = tempDir.resolve("small.json");
        Files.writeString(smallConfigFile, generateSmallConfig());

        // Medium configuration (100 keys)
        mediumConfigFile = tempDir.resolve("medium.json");
        Files.writeString(mediumConfigFile, generateMediumConfig());

        // Large configuration (1000 keys)
        largeConfigFile = tempDir.resolve("large.json");
        Files.writeString(largeConfigFile, generateLargeConfig());

        // Deeply nested configuration
        deeplyNestedFile = tempDir.resolve("deeply_nested.json");
        Files.writeString(deeplyNestedFile, generateDeeplyNestedConfig());
    }


    @Nested
    @DisplayName("Json - Loading time tests")
    class ConfigLoadingTimeTests {

        @Test
        @DisplayName("Small configuration loading performance (1000 iterations)")
        void testLoadSmallConfigPerformance() {
            long startTime = System.nanoTime();

            for (int i = 0; i < 1000; i++) {
                Json config = new Json(smallConfigFile);
                assertNotNull(config);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // ms

            System.out.println("Loading small configuration 1000x: " + duration + " ms");
            assertTrue(duration < 500, "Loading small configuration should take less than 0.5s");
        }

        @Test
        @DisplayName("Medium configuration loading performance (1000 iterations)")
        void testLoadMediumConfigPerformance() {
            long startTime = System.nanoTime();

            for (int i = 0; i < 1000; i++) {
                Json config = new Json(mediumConfigFile);
                assertNotNull(config);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;

            System.out.println("Loading medium configuration 1000x: " + duration + " ms");
            assertTrue(duration < 1000, "Loading medium configuration should take less than 1s");
        }

        @Test
        @DisplayName("Large configuration loading performance (1000 iterations)")
        void testLoadLargeConfigPerformance() {
            long startTime = System.nanoTime();

            for (int i = 0; i < 1000; i++) {
                Json config = new Json(largeConfigFile);
                assertNotNull(config);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;

            System.out.println("Loading large configuration 1000x: " + duration + " ms");
            assertTrue(duration < 2000, "Loading large configuration should take less than 2s");
        }
    }

    @Test
    @DisplayName("Simple value access performance (1M iterations)")
    void testSimpleValueAccessPerformance() {
        Json config = new Json(mediumConfigFile);
        long startTime = System.nanoTime();

        for (int i = 0; i < 1_000_000; i++) {
            String value = config.text("key_50");
            assertNotNull(value);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Simple value access 1M x: " + duration + " ms");
        assertTrue(duration < 1000, "Simple value access should take less than 1s");
    }

    @Test
    @DisplayName("Key existence check performance (1M iterations)")
    void testHasKeyPerformance() {
        Json config = new Json(mediumConfigFile);
        long startTime = System.nanoTime();

        for (int i = 0; i < 1_000_000; i++) {
            boolean has = config.has("key_50");
            assertTrue(has);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Key existence check 1M x: " + duration + " ms");
        assertTrue(duration < 500, "Key checks should take less than 500ms");
    }

    @Test
    @DisplayName("Nested configuration access performance (100k iterations)")
    void testNestedConfigAccessPerformance() {
        Json config = new Json(deeplyNestedFile);
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            Json level1 = config.json("level1");
            Json level2 = level1.json("level2");
            Json level3 = level2.json("level3");
            Json level4 = level3.json("level4");
            Json level5 = level4.json("level5");
            String value = level5.text("value");
            assertNotNull(value);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Nested configuration access 100k x: " + duration + " ms");
        assertTrue(duration < 100, "Nested configuration access should take less than 0.1s");
    }

    @Test
    @DisplayName("Mixed data type access performance (100k iterations)")
    void testMixedTypeAccessPerformance() {
        String json = """
            text: "value",
            number: 42,
            decimal: 3.14,
            flag: true,
            nested: { inner: "nested_value", },
            """;
        Json config = new Json(json, "test-path");
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            config.text("text");
            config.i("number");
            config.d("decimal");
            config.bool("flag");
            config.json("nested").text("inner");
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Mixed data type access 100k x: " + duration + " ms");
        assertTrue(duration < 1500, "Mixed type access should take less than 1.5s");
    }

    @Test
    @DisplayName("Default value access performance (100k iterations)")
    void testDefaultValueAccessPerformance() {
        Json config = new Json("existing: \"value\",", "test-path");
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            config.text("nonexistent", "default");
            config.i("nonexistent", 0, 100, 42);
            config.bool("nonexistent", true);
            config.dTry("nonexistent", 0, 5,3.14);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Default value access 100k x: " + duration + " ms");
        assertTrue(duration < 1000, "Default value access should take less than 1s");
    }

    @Test
    @DisplayName("List access performance (10k iterations)")
    void testListAccessPerformance() {
        String json = """
            numbers: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10,],
            texts: ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j",],
            """;
        Json config = new Json(json, "test-path");
        long startTime = System.nanoTime();

        for (int i = 0; i < 10_000; i++) {
            String[] numbers = config.values("numbers");
            String[] texts = config.texts("texts");
            assertEquals(10, numbers.length);
            assertEquals(10, texts.length);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("List access 10k x: " + duration + " ms");
        assertTrue(duration < 1000, "List access should take less than 1s");
    }

    @Test
    @DisplayName("String parsing performance (10k iterations)")
    void testStringParsingPerformance() {
        String json = "key1: \"value1\", key2: 42, key3: true, nested: { inner: \"value\", },";
        long startTime = System.nanoTime();

        for (int i = 0; i < 10_000; i++) {
            Json config = new Json(json, "test-path");
            assertNotNull(config);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("String parsing 10k x: " + duration + " ms");
        assertTrue(duration < 2000, "String parsing should take less than 2s");
    }

    @Disabled("Vanilla Json does not support optionals")
    @Test
    @DisplayName("Optional API performance (100k iterations)")
    void testOptionalApiPerformance() {
        Json config = new Json("existing: \"value\", number: 42,", "test-path");
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
//            config.textOpt("existing").orElse("default");
//            config.textOpt("nonexistent").orElse("default");
//            config.integerOpt("number").orElse(0);
//            config.integerOpt("nonexistent").orElse(0);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Optional API 100k x: " + duration + " ms");
        assertTrue(duration < 100, "Optional API should be faster than 0.1s");
    }

    private static String generateConfig(int size) {
        StringBuilder sb = new StringBuilder("{\n");
        for (int i = 0; i < size; i++) {
            sb.append("key_").append(i).append(": \"value_").append(i).append("\"").append(",\n");
        }
        return sb.toString();
    }

    private static String generateSmallConfig() {
        return generateConfig(10);
    }

    private static String generateMediumConfig() {
        return generateConfig(100);
    }

    private static String generateLargeConfig() {
        return generateConfig(1000);
    }

    private static String generateDeeplyNestedConfig() {
        return """
            level1: {
                level2: {
                    level3: {
                        level4: {
                            level5: {
                                value: "deeply_nested_value",
                            },
                        },
                    },
                },
            },
            """;
    }
}
