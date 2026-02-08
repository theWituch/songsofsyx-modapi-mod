package snake2d.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import snake2d.config.exception.ConfigException;
import snake2d.util.file.json.JsonValue;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.LIST;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonConfig Tests")
public class JsonConfigTest {

    @TempDir
    Path tempDir;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create configuration from string")
        void shouldCreateFromString() {
            String json = "{ name: \"test\", value: 42 }";
            JsonConfig config = new JsonConfig(json);

            assertEquals("test", config.text("name"));
            assertEquals(42, config.integer("value"));
        }

        @Test
        @DisplayName("Should create configuration from Path file")
        void shouldCreateFromPath() throws IOException {
            Path file = tempDir.resolve("config.json");
            Files.writeString(file, "{ host: \"localhost\", port: 8080 }");

            JsonConfig config = new JsonConfig(file);

            assertEquals("localhost", config.text("host"));
            assertEquals(8080, config.integer("port"));
        }

        @Test
        @DisplayName("Should create configuration from File")
        void shouldCreateFromFile() throws IOException {
            Path path = tempDir.resolve("config.json");
            Files.writeString(path, "{ enabled: true }");
            File file = path.toFile();

            JsonConfig config = new JsonConfig(file);

            assertTrue(config.bool("enabled"));
        }

        @Test
        @DisplayName("Should throw ConfigException for invalid JSON")
        void shouldThrowConfigExceptionForInvalidJson() {
            String invalidJson = "{ name: invalid }";

            Exception ex = assertThrows(ConfigException.class, () -> {
                new JsonConfig(invalidJson);
            });
            assertThat(ex.getMessage()).matches("Error parsing content string");
        }

        @Test
        @DisplayName("Should throw ConfigException for non-existent file")
        void shouldThrowConfigExceptionForNonExistentFile() {
            Path nonExistent = tempDir.resolve("nonexistent.json");

            Exception ex = assertThrows(ConfigException.class, () -> {
                new JsonConfig(nonExistent);
            });
            assertThat(ex.getMessage()).matches("Error reading configuration file: .*nonexistent.json");
        }

        @Test
        @DisplayName("Should merge multiple files")
        void shouldMergeMultipleFiles() throws IOException {
            Path file1 = tempDir.resolve("config1.json");
            Files.writeString(file1, "{ a: 1, b: 2 }");

            Path file2 = tempDir.resolve("config2.json");
            Files.writeString(file2, "{ b: 20, c: 30 }");

            LIST<Path> paths = new ArrayList<>(file1, file2);
            JsonConfig config = new JsonConfig(paths);

            assertEquals(1, config.integer("a"));
            assertEquals(20, config.integer("b")); // Overwritten from file2
            assertEquals(30, config.integer("c"));
        }
    }

    @Nested
    @DisplayName("Has Methods Tests")
    class HasMethodsTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = "{ existing: \"value\", nullValue: null }";
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("Should return true for existing key")
        void shouldReturnTrueForExistingKey() {
            assertTrue(config.has("existing"));
        }

        @Test
        @DisplayName("Should return false for non-existent key")
        void shouldReturnFalseForNonExistentKey() {
            assertFalse(config.has("nonexistent"));
        }

        @Test
        @DisplayName("Should return true for key with null value")
        void shouldReturnTrueForNullValue() {
            assertTrue(config.has("nullValue"));
        }

        @Test
        @DisplayName("hasValue should return false for null")
        void hasValueShouldReturnFalseForNull() {
            assertFalse(config.hasValue("nullValue"));
        }

        @Test
        @DisplayName("hasValue should return true for non-null value")
        void hasValueShouldReturnTrueForNonNullValue() {
            assertTrue(config.hasValue("existing"));
        }
    }

    @Nested
    @DisplayName("Text Methods Tests")
    class TextMethodsTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = "{ name: \"John\", number: 42 }";
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("Should retrieve text value")
        void shouldGetTextValue() {
            assertEquals("John", config.text("name"));
        }

        @Test
        @DisplayName("Should throw exception for non-existent key")
        void shouldThrowExceptionForNonExistentKey() {
            assertThrows(ConfigException.class, () -> {
                config.text("nonexistent");
            });
        }

        @Test
        @DisplayName("Should throw exception for invalid type")
        void shouldThrowExceptionForWrongType() {
            assertThrows(ConfigException.class, () -> {
                config.text("number");
            });
        }

        @Test
        @DisplayName("Should return default value for non-existent key")
        void shouldReturnDefaultValueForNonExistentKey() {
            assertEquals("default", config.text("nonexistent", "default"));
        }

        @Test
        @DisplayName("Should return default value for invalid type")
        void shouldReturnDefaultValueForWrongType() {
            assertEquals("default", config.text("number", "default"));
        }

        @Test
        @DisplayName("Should return Optional.empty for non-existent key")
        void shouldReturnEmptyOptionalForNonExistentKey() {
            assertTrue(config.textOpt("nonexistent").isEmpty());
        }

        @Test
        @DisplayName("Should return Optional with value")
        void shouldReturnOptionalWithValue() {
            Optional<String> value = config.textOpt("name");
            assertTrue(value.isPresent());
            assertEquals("John", value.get());
        }
    }

    @Nested
    @DisplayName("Integer Methods Tests")
    class IntegerMethodsTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = "{ count: 42, text: \"not a number\" }";
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("Should retrieve integer value")
        void shouldGetIntegerValue() {
            assertEquals(42, config.integer("count"));
        }

        @Test
        @DisplayName("Should throw exception for non-existent key")
        void shouldThrowExceptionForNonExistentKey() {
            assertThrows(ConfigException.class, () -> {
                config.integer("nonexistent");
            });
        }

        @Test
        @DisplayName("Should return default value")
        void shouldReturnDefaultValue() {
            assertEquals(100, config.integer("nonexistent", 100));
        }

        @Test
        @DisplayName("Should return Optional with value")
        void shouldReturnOptionalWithValue() {
            Optional<Integer> value = config.integerOpt("count");
            assertTrue(value.isPresent());
            assertEquals(42, value.get());
        }
    }

    @Nested
    @DisplayName("Decimal Methods Tests")
    class DecimalMethodsTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = "{ price: 19.99, count: 5 }";
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("Should retrieve decimal value")
        void shouldGetDecimalValue() {
            assertEquals(19.99, config.decimal("price"), 0.001);
        }

        @Test
        @DisplayName("Should convert integer to double")
        void shouldConvertIntegerToDouble() {
            assertEquals(5.0, config.decimal("count"), 0.001);
        }

        @Test
        @DisplayName("Should return default value")
        void shouldReturnDefaultValue() {
            assertEquals(9.99, config.decimal("nonexistent", 9.99), 0.001);
        }

        @Test
        @DisplayName("Should return Optional with value")
        void shouldReturnOptionalWithValue() {
            Optional<Double> value = config.decimalOpt("price");
            assertTrue(value.isPresent());
            assertEquals(19.99, value.get(), 0.001);
        }
    }

    @Nested
    @DisplayName("Boolean Methods Tests")
    class BooleanMethodsTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = "{ enabled: true, disabled: false }";
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("Should retrieve true value")
        void shouldGetTrueValue() {
            assertTrue(config.bool("enabled"));
        }

        @Test
        @DisplayName("Should retrieve false value")
        void shouldGetFalseValue() {
            assertFalse(config.bool("disabled"));
        }

        @Test
        @DisplayName("Should return default value")
        void shouldReturnDefaultValue() {
            assertTrue(config.bool("nonexistent", true));
        }

        @Test
        @DisplayName("Should return Optional with value")
        void shouldReturnOptionalWithValue() {
            Optional<Boolean> value = config.boolOpt("enabled");
            assertTrue(value.isPresent());
            assertTrue(value.get());
        }
    }

    @Nested
    @DisplayName("Nested Json Tests")
    class NestedJsonTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = """
                {
                    database: {
                        host: "localhost",
                        port: 5432,
                        credentials: {
                            user: "admin",
                            password: "secret"
                        }
                    },
                    notJson: "simple string"
                }
                """;
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("Should retrieve nested configuration")
        void shouldGetNestedConfig() {
            JsonConfig db = config.json("database");
            assertEquals("localhost", db.text("host"));
            assertEquals(5432, db.integer("port"));
        }

        @Test
        @DisplayName("Should retrieve multi-level nested configuration")
        void shouldGetMultiLevelNestedConfig() {
            JsonConfig db = config.json("database");
            JsonConfig creds = db.json("credentials");
            assertEquals("admin", creds.text("user"));
            assertEquals("secret", creds.text("password"));
        }

        @Test
        @DisplayName("Should throw exception for invalid type")
        void shouldThrowExceptionForWrongType() {
            assertThrows(ConfigException.class, () -> {
                config.json("notJson");
            });
        }

        @Test
        @DisplayName("Should return Optional.empty for non-existent key")
        void shouldReturnEmptyOptionalForNonExistentKey() {
            assertTrue(config.jsonOpt("nonexistent").isEmpty());
        }

        @Test
        @DisplayName("Should return Optional with value")
        void shouldReturnOptionalWithValue() {
            Optional<JsonConfig> db = config.jsonOpt("database");
            assertTrue(db.isPresent());
            assertEquals("localhost", db.get().text("host"));
        }
    }

    @Nested
    @DisplayName("Array and List Tests")
    class ArrayAndListTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = """
                {
                    numbers: [1, 2, 3, 4, 5],
                    names: ["Alice", "Bob", "Charlie"],
                    items: [
                        { id: 1, name: "Item 1" },
                        { id: 2, name: "Item 2" }
                    ],
                    mixed: [1, "text", true]
                }
                """;
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("Should retrieve array")
        void shouldGetArray() {
            JsonValue[] numbers = config.array("numbers");
            assertEquals(5, numbers.length);
            assertEquals(1, numbers[0].asInteger());
            assertEquals(5, numbers[4].asInteger());
        }

        @Test
        @DisplayName("Should retrieve list")
        void shouldGetList() {
            List<JsonValue> numbers = config.list("numbers");
            assertEquals(5, numbers.size());
            assertEquals(1, numbers.get(0).asInteger());
        }

        @Test
        @DisplayName("Should retrieve list of strings")
        void shouldGetTextList() {
            List<String> names = config.textList("names");
            assertEquals(3, names.size());
            assertEquals("Alice", names.get(0));
            assertEquals("Charlie", names.get(2));
        }

        @Test
        @DisplayName("Should retrieve list of integers")
        void shouldGetIntegerList() {
            List<Integer> numbers = config.integerList("numbers");
            assertEquals(5, numbers.size());
            assertEquals(1, numbers.get(0));
            assertEquals(5, numbers.get(4));
        }

        @Test
        @DisplayName("Should retrieve list of nested configurations")
        void shouldGetJsonList() {
            List<JsonConfig> items = config.jsonList("items");
            assertEquals(2, items.size());
            assertEquals(1, items.get(0).integer("id"));
            assertEquals("Item 2", items.get(1).text("name"));
        }

        @Test
        @DisplayName("Should throw exception for mixed array as text list")
        void shouldThrowExceptionForMixedArrayAsTextList() {
            assertThrows(ConfigException.class, () -> {
                config.textList("mixed");
            });
        }
    }

    @Nested
    @DisplayName("Null Value Tests")
    class NullValueTests {

        private JsonConfig config;

        @BeforeEach
        void setUp() {
            String json = "{ existing: \"value\", nullValue: null }";
            config = new JsonConfig(json);
        }

        @Test
        @DisplayName("isNull should return true for null value")
        void isNullShouldReturnTrueForNullValue() {
            assertTrue(config.isNull("nullValue"));
        }

        @Test
        @DisplayName("isNull should return false for non-null value")
        void isNullShouldReturnFalseForNonNullValue() {
            assertFalse(config.isNull("existing"));
        }

        @Test
        @DisplayName("isNull should return false for non-existent key")
        void isNullShouldReturnFalseForNonExistentKey() {
            assertFalse(config.isNull("nonexistent"));
        }
    }

    @Nested
    @DisplayName("Utility Methods Tests")
    class UtilityMethodsTests {

        @Test
        @DisplayName("Should return configuration size")
        void shouldReturnSize() {
            String json = "{ a: 1, b: 2, c: 3 }";
            JsonConfig config = new JsonConfig(json);
            assertEquals(3, config.size());
        }

        @Test
        @DisplayName("Should check if configuration is empty")
        void shouldCheckIfEmpty() {
            JsonConfig emptyConfig = new JsonConfig("{}");
            assertTrue(emptyConfig.isEmpty());

            JsonConfig nonEmptyConfig = new JsonConfig("{ key: \"value\" }");
            assertFalse(nonEmptyConfig.isEmpty());
        }

        @Test
        @DisplayName("Should return raw Json object")
        void shouldReturnRawJson() {
            String json = "{ key: \"value\" }";
            JsonConfig config = new JsonConfig(json);
            assertNotNull(config.getRawJson());
            assertTrue(config.getRawJson().containsKey("key"));
        }
    }

    @Nested
    @DisplayName("Complex Scenarios Tests")
    class ComplexScenariosTests {

        @Test
        @DisplayName("Should handle complex application configuration")
        void shouldHandleComplexApplicationConfig() {
            String json = """
                {
                    app: {
                        name: "MyApp",
                        version: "1.0.0",
                        debug: true
                    },
                    database: {
                        host: "localhost",
                        port: 5432,
                        maxConnections: 100,
                        timeout: 30.5
                    },
                    features: ["auth", "logging", "caching"],
                    servers: [
                        { name: "server1", host: "192.168.1.1", port: 8080 },
                        { name: "server2", host: "192.168.1.2", port: 8081 }
                    ]
                }
                """;

            JsonConfig config = new JsonConfig(json);

            // App config
            JsonConfig app = config.json("app");
            assertEquals("MyApp", app.text("name"));
            assertEquals("1.0.0", app.text("version"));
            assertTrue(app.bool("debug"));

            // Database config
            JsonConfig db = config.json("database");
            assertEquals("localhost", db.text("host"));
            assertEquals(5432, db.integer("port"));
            assertEquals(100, db.integer("maxConnections"));
            assertEquals(30.5, db.decimal("timeout"), 0.001);

            // Features list
            List<String> features = config.textList("features");
            assertEquals(3, features.size());
            assertTrue(features.contains("auth"));

            // Servers list
            List<JsonConfig> servers = config.jsonList("servers");
            assertEquals(2, servers.size());
            assertEquals("server1", servers.get(0).text("name"));
            assertEquals(8081, servers.get(1).integer("port"));
        }

        @Test
        @DisplayName("Should handle configuration with default values")
        void shouldHandleConfigWithDefaults() {
            String json = "{ timeout: 30 }";
            JsonConfig config = new JsonConfig(json);

            assertEquals("localhost", config.text("host", "localhost"));
            assertEquals(8080, config.integer("port", 8080));
            assertEquals(30, config.integer("timeout"));
            assertTrue(config.bool("debug", true));
        }
    }
}
