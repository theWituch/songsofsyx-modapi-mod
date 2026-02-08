package snake2d.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonConfig Performance Tests")
public class JsonConfigPerformanceTest {

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
    @DisplayName("JsonConfig - Loading time tests")
    class ConfigLoadingTimeTests {

        @Test
        @DisplayName("Small configuration loading performance (1000 iterations)")
        void testLoadSmallConfigPerformance() {
            long startTime = System.nanoTime();

            for (int i = 0; i < 1000; i++) {
                JsonConfig config = new JsonConfig(smallConfigFile);
                assertNotNull(config);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // ms

            System.out.println("Small configuration loading 1000x: " + duration + " ms");
            assertTrue(duration < 500, "Small configuration loading should take less than 0.5s");
        }

        @Test
        @DisplayName("Medium configuration loading performance (1000 iterations)")
        void testLoadMediumConfigPerformance() {
            long startTime = System.nanoTime();

            for (int i = 0; i < 1000; i++) {
                JsonConfig config = new JsonConfig(mediumConfigFile);
                assertNotNull(config);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;

            System.out.println("Medium configuration loading 1000x: " + duration + " ms");
            assertTrue(duration < 1000, "Medium configuration loading should take less than 1s");
        }

        @Test
        @DisplayName("Large configuration loading performance (1000 iterations)")
        void testLoadLargeConfigPerformance() {
            long startTime = System.nanoTime();

            for (int i = 0; i < 1000; i++) {
                JsonConfig config = new JsonConfig(largeConfigFile);
                assertNotNull(config);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;

            System.out.println("Large configuration loading 1000x: " + duration + " ms");
            assertTrue(duration < 2000, "Large configuration loading should take less than 2s");
        }
    }

    @Test
    @DisplayName("Simple value access performance (1M iterations)")
    void testSimpleValueAccessPerformance() {
        JsonConfig config = new JsonConfig(mediumConfigFile);
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
        JsonConfig config = new JsonConfig(mediumConfigFile);
        long startTime = System.nanoTime();

        for (int i = 0; i < 1_000_000; i++) {
            boolean has = config.has("key_50");
            assertTrue(has);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Key existence check 1M x: " + duration + " ms");
        assertTrue(duration < 500, "Key existence check should take less than 500ms");
    }

    @Test
    @DisplayName("Nested configuration access performance (100k iterations)")
    void testNestedConfigAccessPerformance() {
        JsonConfig config = new JsonConfig(deeplyNestedFile);
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            JsonConfig level1 = config.json("level1");
            JsonConfig level2 = level1.json("level2");
            JsonConfig level3 = level2.json("level3");
            JsonConfig level4 = level3.json("level4");
            JsonConfig level5 = level4.json("level5");
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
            {
                text: "value",
                number: 42,
                decimal: 3.14,
                flag: true,
                nested: { inner: "nested_value" }
            }
            """;
        JsonConfig config = new JsonConfig(json);
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            config.text("text");
            config.integer("number");
            config.decimal("decimal");
            config.bool("flag");
            config.json("nested").text("inner");
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Mixed data type access 100k x: " + duration + " ms");
        assertTrue(duration < 1500, "Mixed data type access should take less than 1.5s");
    }

    @Test
    @DisplayName("Default value access performance (100k iterations)")
    void testDefaultValueAccessPerformance() {
        JsonConfig config = new JsonConfig("{ existing: \"value\" }");
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            config.text("nonexistent", "default");
            config.integer("nonexistent", 42);
            config.bool("nonexistent", true);
            config.decimal("nonexistent", 3.14);
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
            {
                numbers: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
                texts: ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j"]
            }
            """;
        JsonConfig config = new JsonConfig(json);
        long startTime = System.nanoTime();

        for (int i = 0; i < 10_000; i++) {
            List<Integer> numbers = config.integerList("numbers");
            List<String> texts = config.textList("texts");
            assertEquals(10, numbers.size());
            assertEquals(10, texts.size());
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("List access 10k x: " + duration + " ms");
        assertTrue(duration < 1000, "List access should take less than 1s");
    }

    @Test
    @DisplayName("String parsing performance (10k iterations)")
    void testStringParsingPerformance() {
        String json = "{ key1: \"value1\", key2: 42, key3: true, nested: { inner: \"value\" } }";
        long startTime = System.nanoTime();

        for (int i = 0; i < 10_000; i++) {
            JsonConfig config = new JsonConfig(json);
            assertNotNull(config);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("String parsing 10k x: " + duration + " ms");
        assertTrue(duration < 2000, "String parsing should take less than 2s");
    }

    @Test
    @DisplayName("Optional API performance (100k iterations)")
    void testOptionalApiPerformance() {
        JsonConfig config = new JsonConfig("{ existing: \"value\", number: 42 }");
        long startTime = System.nanoTime();

        for (int i = 0; i < 100_000; i++) {
            config.textOpt("existing").orElse("default");
            config.textOpt("nonexistent").orElse("default");
            config.integerOpt("number").orElse(0);
            config.integerOpt("nonexistent").orElse(0);
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("Optional API 100k x: " + duration + " ms");
        assertTrue(duration < 100, "Optional API should be faster than 0.1s");
    }

    private static String generateSmallConfig() {
        StringBuilder sb = new StringBuilder("{\n");
        for (int i = 0; i < 10; i++) {
            sb.append("  key_").append(i).append(": \"value_").append(i).append("\"");
            if (i < 9) sb.append(",");
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String generateMediumConfig() {
        StringBuilder sb = new StringBuilder("{\n");
        for (int i = 0; i < 100; i++) {
            sb.append("  key_").append(i).append(": \"value_").append(i).append("\"");
            if (i < 99) sb.append(",");
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String generateLargeConfig() {
        StringBuilder sb = new StringBuilder("{\n");
        for (int i = 0; i < 1000; i++) {
            sb.append("  key_").append(i).append(": \"value_").append(i).append("\"");
            if (i < 999) sb.append(",");
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String generateDeeplyNestedConfig() {
        return """
            {
                level1: {
                    level2: {
                        level3: {
                            level4: {
                                level5: {
                                    value: "deeply_nested_value"
                                }
                            }
                        }
                    }
                }
            }
            """;
    }
}
