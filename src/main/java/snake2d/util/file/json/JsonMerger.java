package snake2d.util.file.json;

import java.util.List;

/**
 * Class used to merge multiple Json objects.
 * The merge process combines all keys, where values from later objects
 * overwrite values from earlier ones.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class JsonMerger {

    /**
     * Merges multiple Json objects into one.
     * Keys from later objects overwrite keys from earlier ones.
     *
     * @param jsons Json objects to merge (in order from first to last)
     * @return new Json object containing merged keys
     */
    public static Json merge(Json... jsons) {
        if (jsons == null || jsons.length == 0) {
            return new Json();
        }

        Json result = new Json();

        for (Json json : jsons) {
            if (json != null) {
                mergeInto(result, json);
            }
        }

        return result;
    }

    /**
     * Merges a list of Json objects into one.
     * Keys from later objects overwrite keys from earlier ones.
     *
     * @param jsons list of Json objects to merge
     * @return new Json object containing merged keys
     */
    public static Json merge(List<Json> jsons) {
        if (jsons == null || jsons.isEmpty()) {
            return new Json();
        }

        return merge(jsons.toArray(new Json[0]));
    }

    /**
     * Merges the contents of source into target.
     * Modifies the target object.
     *
     * @param target target object (will be modified)
     * @param source source object (its values will overwrite values in target)
     */
    private static void mergeInto(Json target, Json source) {
        for (JsonKey key : source.keySet()) {
            JsonValue sourceValue = source.get(key);
            JsonValue targetValue = target.get(key);

            // If the key does not exist in target or the value is not a JSON object,
            // simply overwrite the value
            if (targetValue == null ||
                    sourceValue.getType() != JsonValue.ValueType.JSON_OBJECT ||
                    targetValue.getType() != JsonValue.ValueType.JSON_OBJECT) {
                target.put(key, sourceValue);
            } else {
                // Both are JSON objects - merge them recursively
                Json mergedNested = new Json();
                mergeInto(mergedNested, targetValue.asJson());
                mergeInto(mergedNested, sourceValue.asJson());
                target.put(key, new JsonValue(mergedNested));
            }
        }
    }

    /**
     * Shallowly merges Json objects.
     * Nested objects are completely overwritten and not merged.
     *
     * @param jsons Json objects to shallowly merge
     * @return new Json object containing shallowly merged keys
     */
    public static Json shallowMerge(Json... jsons) {
        if (jsons == null || jsons.length == 0) {
            return new Json();
        }

        Json result = new Json();

        for (Json json : jsons) {
            if (json != null) {
                for (JsonKey key : json.keySet()) {
                    result.put(key, json.get(key));
                }
            }
        }

        return result;
    }

    private JsonMerger() {
        throw new RuntimeException("Utility class should not be instantiated");
    }

}
