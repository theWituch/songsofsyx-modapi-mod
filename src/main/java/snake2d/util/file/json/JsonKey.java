package snake2d.util.file.json;

import java.util.Objects;

/**
 * Class representing a key in a JSON object.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class JsonKey {
    private final String key;
    private final MergeStrategy mergeStrategy;

    public JsonKey(String key) {
        this(key, MergeStrategy.UNDEFINED);
    }

    public JsonKey(String key, MergeStrategy mergeStrategy) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (mergeStrategy == null) {
            throw new IllegalArgumentException("MergeStrategy cannot be empty");
        }
        this.key = key;
        this.mergeStrategy = mergeStrategy;
    }

    public String getKey() {
        return key;
    }

    public MergeStrategy getMergeStrategy() {
        return mergeStrategy;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonKey jsonKey = (JsonKey) o;
        return Objects.equals(key, jsonKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
