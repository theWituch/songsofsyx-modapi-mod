package snake2d.util.file.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a hierarchical JSON object.
 * Stores a map of JsonKey keys to JsonValue values.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class Json {
    private Map<JsonKey, JsonValue> data;

    public Json() {
        this.data = new HashMap<>();
    }

    /**
     * Adds a key-value pair to the JSON object.
     */
    public void put(JsonKey key, JsonValue value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        data.put(key, value);
    }

    /**
     * Adds a key-value pair to the JSON object.
     */
    public void put(String key, JsonValue value) {
        put(new JsonKey(key), value);
    }

    /**
     * Retrieves a value by key.
     */
    public JsonValue get(JsonKey key) {
        return data.get(key);
    }

    /**
     * Retrieves a value by key.
     */
    public JsonValue get(String key) {
        return get(new JsonKey(key));
    }

    /**
     * Checks whether the object contains the given key.
     */
    public boolean containsKey(JsonKey key) {
        return data.containsKey(key);
    }

    /**
     * Checks whether the object contains the given key.
     */
    public boolean containsKey(String key) {
        return containsKey(new JsonKey(key));
    }

    /**
     * Returns a set of all keys.
     */
    public Set<JsonKey> keySet() {
        return data.keySet();
    }

    /**
     * Returns the number of key-value pairs.
     */
    public int size() {
        return data.size();
    }

    /**
     * Checks whether the object is empty.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Removes all key-value pairs.
     */
    public void clear() {
        data.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<JsonKey, JsonValue> entry : data.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
