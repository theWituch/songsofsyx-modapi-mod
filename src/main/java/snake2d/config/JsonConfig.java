package snake2d.config;

import snake2d.config.exception.ConfigException;
import snake2d.util.file.json.Json;
import snake2d.util.file.json.JsonMerger;
import snake2d.util.file.json.JsonParser;
import snake2d.util.file.json.JsonValue;
import snake2d.util.file.json.exception.JsonParseException;
import snake2d.util.sets.LIST;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Configuration class using Json object as a data source.
 * Provides convenient access to configuration values with support for default values.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class JsonConfig {
    private final JsonParser parser;
    private final Json json;

    /**
     * Creates a configuration from a single file.
     *
     * @param path path to the source file
     * @throws ConfigException in case of parsing or file reading error
     */
    public JsonConfig(Path path) {
        this.parser = new JsonParser();
        try {
            this.json = parser.parse(path);
        } catch (IOException | JsonParseException e) {
            throw new ConfigException("Error reading configuration file: " + path, e);
        }
    }

    /**
     * Creates a configuration from a single file.
     *
     * @param file source file
     * @throws ConfigException in case of parsing or file reading error
     */
    public JsonConfig(File file) {
        this.parser = new JsonParser();
        try {
            this.json = parser.parse(file);
        } catch (IOException | JsonParseException e) {
            throw new ConfigException("Error reading configuration file: " + file, e);
        }
    }

    /**
     * Creates a configuration from multiple files.
     * Files are merged in order – later ones override earlier ones.
     *
     * @param paths list of paths to JSON files
     * @throws ConfigException in case of parsing or file reading errors
     */
    public JsonConfig(LIST<Path> paths) {
        this.parser = new JsonParser();
        List<Json> jsons = new LinkedList<>();
        for (Path path : paths) {
            try {
                jsons.add(parser.parse(path));
            } catch (IOException | JsonParseException e) {
                throw new ConfigException("Error reading configuration file: " + path, e);
            }
        }
        this.json = JsonMerger.merge(jsons);
    }

    /**
     * Creates a configuration from a JSON string.
     *
     * @param content string containing JSON
     * @throws ConfigException in case of parsing error
     */
    public JsonConfig(String content) {
        this.parser = new JsonParser();
        try {
            this.json = parser.parse(content);
        } catch (JsonParseException e) {
            throw new ConfigException("Error parsing content string", e);
        }
    }

    /**
     * Private constructor for creating sub-configurations.
     */
    private JsonConfig(Json json) {
        this.parser = new JsonParser();
        this.json = json;
    }

    /**
     * Checks whether the key exists in the configuration.
     *
     * @param key key to check
     * @return true if the key exists
     */
    public boolean has(String key) {
        return json.containsKey(key);
    }

    /**
     * Checks whether the key exists and is not null.
     *
     * @param key key to check
     * @return true if the key exists and the value is not null
     */
    public boolean hasValue(String key) {
        return has(key) && !json.get(key).isNull();
    }

    /**
     * Retrieves a nested configuration.
     *
     * @param key key of the nested object
     * @return new JsonConfig instance for the nested object
     * @throws ConfigException if the key does not exist or the value is not a JSON object
     */
    public JsonConfig json(String key) {
        if (!has(key)) {
            throw new ConfigException("Key does not exist: " + key);
        }
        try {
            return new JsonConfig(json.get(key).asJson());
        } catch (IllegalStateException e) {
            throw new ConfigException("Value under key '" + key + "' is not a JSON object", e);
        }
    }

    /**
     * Retrieves an optional nested configuration.
     *
     * @param key key of the nested object
     * @return Optional containing JsonConfig or empty if the key does not exist
     */
    public Optional<JsonConfig> jsonOpt(String key) {
        if (!has(key)) {
            return Optional.empty();
        }
        try {
            return Optional.of(new JsonConfig(json.get(key).asJson()));
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves a text value.
     *
     * @param key key
     * @return text value
     * @throws ConfigException if the key does not exist or the value is not a string
     */
    public String text(String key) {
        if (!has(key)) {
            throw new ConfigException("Key does not exist: " + key);
        }
        try {
            return json.get(key).asString();
        } catch (IllegalStateException e) {
            throw new ConfigException("Value under key '" + key + "' is not a string", e);
        }
    }

    /**
     * Retrieves a text value with a default value.
     *
     * @param key key
     * @param defaultValue default value
     * @return text value or default value if the key does not exist
     */
    public String text(String key, String defaultValue) {
        if (!has(key)) {
            return defaultValue;
        }
        try {
            return json.get(key).asString();
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves an optional text value.
     *
     * @param key key
     * @return Optional containing the value or empty if the key does not exist
     */
    public Optional<String> textOpt(String key) {
        if (!has(key)) {
            return Optional.empty();
        }
        try {
            return Optional.of(json.get(key).asString());
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves an integer value.
     *
     * @param key key
     * @return integer value
     * @throws ConfigException if the key does not exist or the value is not an integer
     */
    public int integer(String key) {
        if (!has(key)) {
            throw new ConfigException("Key does not exist: " + key);
        }
        try {
            return json.get(key).asInteger();
        } catch (IllegalStateException e) {
            throw new ConfigException("Value under key '" + key + "' is not an integer", e);
        }
    }

    /**
     * Retrieves an integer value with a default value.
     *
     * @param key key
     * @param defaultValue default value
     * @return integer value or default value if the key does not exist
     */
    public int integer(String key, int defaultValue) {
        if (!has(key)) {
            return defaultValue;
        }
        try {
            return json.get(key).asInteger();
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves an optional integer value.
     *
     * @param key key
     * @return Optional containing the value or empty if the key does not exist
     */
    public Optional<Integer> integerOpt(String key) {
        if (!has(key)) {
            return Optional.empty();
        }
        try {
            return Optional.of(json.get(key).asInteger());
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves a floating-point value.
     *
     * @param key key
     * @return floating-point value
     * @throws ConfigException if the key does not exist or the value is not a number
     */
    public double decimal(String key) {
        if (!has(key)) {
            throw new ConfigException("Key does not exist: " + key);
        }
        try {
            return json.get(key).asDouble();
        } catch (IllegalStateException e) {
            throw new ConfigException("Value under key '" + key + "' is not a floating-point number", e);
        }
    }

    /**
     * Retrieves a floating-point value with a default value.
     *
     * @param key key
     * @param defaultValue default value
     * @return floating-point value or default value if the key does not exist
     */
    public double decimal(String key, double defaultValue) {
        if (!has(key)) {
            return defaultValue;
        }
        try {
            return json.get(key).asDouble();
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves an optional floating-point value.
     *
     * @param key key
     * @return Optional containing the value or empty if the key does not exist
     */
    public Optional<Double> decimalOpt(String key) {
        if (!has(key)) {
            return Optional.empty();
        }
        try {
            return Optional.of(json.get(key).asDouble());
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves a boolean value.
     *
     * @param key key
     * @return boolean value
     * @throws ConfigException if the key does not exist or the value is not boolean
     */
    public boolean bool(String key) {
        if (!has(key)) {
            throw new ConfigException("Key does not exist: " + key);
        }
        try {
            return json.get(key).asBoolean();
        } catch (IllegalStateException e) {
            throw new ConfigException("Value under key '" + key + "' is not a boolean value", e);
        }
    }

    /**
     * Retrieves a boolean value with a default value.
     *
     * @param key key
     * @param defaultValue default value
     * @return boolean value or default value if the key does not exist
     */
    public boolean bool(String key, boolean defaultValue) {
        if (!has(key)) {
            return defaultValue;
        }
        try {
            return json.get(key).asBoolean();
        } catch (IllegalStateException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves an optional boolean value.
     *
     * @param key key
     * @return Optional containing the value or empty if the key does not exist
     */
    public Optional<Boolean> boolOpt(String key) {
        if (!has(key)) {
            return Optional.empty();
        }
        try {
            return Optional.of(json.get(key).asBoolean());
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    /**
     * Retrieves an array of values.
     *
     * @param key key
     * @return array of JsonValue
     * @throws ConfigException if the key does not exist or the value is not an array
     */
    public JsonValue[] array(String key) {
        if (!has(key)) {
            throw new ConfigException("Key does not exist: " + key);
        }
        try {
            return json.get(key).asArray();
        } catch (IllegalStateException e) {
            throw new ConfigException("Value under key '" + key + "' is not an array", e);
        }
    }

    /**
     * Retrieves a list of values.
     *
     * @param key key
     * @return list of JsonValue
     * @throws ConfigException if the key does not exist or the value is not an array/list
     */
    public List<JsonValue> list(String key) {
        if (!has(key)) {
            throw new ConfigException("Key does not exist: " + key);
        }
        try {
            return json.get(key).asList();
        } catch (IllegalStateException e) {
            throw new ConfigException("Value under key '" + key + "' is not a list", e);
        }
    }

    /**
     * Retrieves a list of strings.
     *
     * @param key key
     * @return list of strings
     * @throws ConfigException if values are not strings
     */
    public List<String> textList(String key) {
        List<JsonValue> values = list(key);
        List<String> result = new ArrayList<>();
        for (JsonValue value : values) {
            try {
                result.add(value.asString());
            } catch (IllegalStateException e) {
                throw new ConfigException("Array element under key '" + key + "' is not a string", e);
            }
        }
        return result;
    }

    /**
     * Retrieves a list of integers.
     *
     * @param key key
     * @return list of integers
     * @throws ConfigException if values are not integers
     */
    public List<Integer> integerList(String key) {
        List<JsonValue> values = list(key);
        List<Integer> result = new ArrayList<>();
        for (JsonValue value : values) {
            try {
                result.add(value.asInteger());
            } catch (IllegalStateException e) {
                throw new ConfigException("Array element under key '" + key + "' is not an integer", e);
            }
        }
        return result;
    }

    /**
     * Retrieves a list of nested configurations.
     *
     * @param key key
     * @return list of JsonConfig
     * @throws ConfigException if values are not JSON objects
     */
    public List<JsonConfig> jsonList(String key) {
        List<JsonValue> values = list(key);
        List<JsonConfig> result = new ArrayList<>();
        for (JsonValue value : values) {
            try {
                result.add(new JsonConfig(value.asJson()));
            } catch (IllegalStateException e) {
                throw new ConfigException("Array element under key '" + key + "' is not a JSON object", e);
            }
        }
        return result;
    }

    /**
     * Checks whether the value under the key is null.
     *
     * @param key key
     * @return true if the value is null
     */
    public boolean isNull(String key) {
        if (!has(key)) {
            return false;
        }
        return json.get(key).isNull();
    }

    /**
     * Returns the underlying Json object.
     *
     * @return Json object
     */
    public Json getRawJson() {
        return json;
    }

    /**
     * Returns the number of keys in the configuration.
     *
     * @return number of keys
     */
    public int size() {
        return json.size();
    }

    /**
     * Checks whether the configuration is empty.
     *
     * @return true if there are no keys
     */
    public boolean isEmpty() {
        return json.isEmpty();
    }

}
