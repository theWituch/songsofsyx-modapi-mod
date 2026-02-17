package snake2d.util.file.json;

import java.util.List;

import static snake2d.util.file.json.JsonValue.ValueType.*;

/**
 * Class used to merge multiple Json objects.
 * The merge process combines all keys, where values from later objects
 * overwrite values from earlier ones based on MergeStrategy.
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
                merge(result, json, MergeStrategy.REPLACE);
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
     * Applies source Json to target Json with given merge strategy.
     *
     * @param target target Json (will be modified)
     * @param source source Json (provides new values)
     * @param defaultStrategy default merge strategy if key doesn't specify one
     */
    private static void merge(Json target, Json source, MergeStrategy defaultStrategy) {
        for (JsonKey key : source.keySet()) {
            MergeStrategy strategy = resolveStrategy(key, defaultStrategy);
            JsonValue sourceValue = source.get(key);
            JsonValue targetValue = target.get(key);

            if (strategy == MergeStrategy.DELETE) {
                target.remove(key);
            } else if (targetValue == null) {
                target.put(key, sourceValue);
            } else {
                JsonValue merged = mergeValuesByStrategy(strategy, targetValue, sourceValue);
                target.put(key, merged);
            }
        }
    }

    /**
     * Resolves merge strategy: uses key's strategy if defined, otherwise uses default.
     */
    private static MergeStrategy resolveStrategy(JsonKey key, MergeStrategy defaultStrategy) {
        MergeStrategy keyStrategy = key.getMergeStrategy();
        return keyStrategy == MergeStrategy.UNDEFINED ? defaultStrategy : keyStrategy;
    }

    /**
     * Merges two values based on strategy and their types.
     */
    private static JsonValue mergeValuesByStrategy(MergeStrategy strategy, JsonValue target, JsonValue source) {
        if (target.getType() == STRING && source.getType() == STRING) {
            return mergeStrings(strategy, target, source);
        }
        if (target.getType() == INTEGER && source.getType() == INTEGER) {
            return mergeIntegers(strategy, target, source);
        }
        if (target.getType() == DOUBLE && source.getType() == DOUBLE) {
            return mergeDoubles(strategy, target, source);
        }
        if (target.getType() == BOOLEAN && source.getType() == BOOLEAN) {
            return mergeBooleans(strategy, target, source);
        }
        if (target.getType() == ARRAY && source.getType() == ARRAY) {
            return mergeArrays(strategy, target, source);
        }
        if (target.getType() == LIST && source.getType() == LIST) {
            throw new IllegalStateException("Not implemented yet!");
        }
        if (target.getType() == JSON_OBJECT && source.getType() == JSON_OBJECT) {
            return mergeJsonObjects(strategy, target, source);
        }
        if (target.getType() == OVERLAY || source.getType() == OVERLAY) {
            throw new IllegalStateException("Overlays cannot be merged!");
        }

        // Different types or simple values means REPLACE
        return source;
    }

    /**
     * Merges two JSON objects recursively.
     */
    private static JsonValue mergeJsonObjects(MergeStrategy parentStrategy, JsonValue targetValue, JsonValue sourceValue) {
        Json target = targetValue.asJson();
        Json source = sourceValue.asJson();

        merge(target, source, parentStrategy);

        return targetValue;
    }

    /**
     * Merges two arrays based on merge strategy.
     */
    private static JsonValue mergeArrays(MergeStrategy strategy, JsonValue targetValue, JsonValue sourceValue) {
        JsonValue[] targetArr = targetValue.asArray();
        JsonValue[] sourceArr = sourceValue.asArray();

        JsonValue[] merged;
        switch (strategy) {
            case PREPEND:
                merged = appendArrays(sourceArr, targetArr);
                break;
            case APPEND:
                merged = appendArrays(targetArr, sourceArr);
                break;
            case OVERLAY:
                merged = overlayArrays(targetArr, sourceArr, false);
                break;
            case OVERLAY_TRUNCATE:
                merged = overlayArrays(targetArr, sourceArr, true);
                break;
            case REPLACE:
            default:
                merged = sourceArr;
                break;
        }
        return new JsonValue(merged);
    }

    /**
     * Appends source array to target array.
     */
    private static JsonValue[] appendArrays(JsonValue[] target, JsonValue[] source) {
        JsonValue[] merged = new JsonValue[target.length + source.length];
        System.arraycopy(target, 0, merged, 0, target.length);
        System.arraycopy(source, 0, merged, target.length, source.length);
        return merged;
    }

    /**
     * Overlays source array over target array.
     * If truncate is true, result length equals source length.
     * If truncate is false, result length of longer array.
     * Recursively merges nested structures (arrays and JSON objects).
     */
    private static JsonValue[] overlayArrays(JsonValue[] target, JsonValue[] source, boolean truncate) {
        int length = truncate ? source.length : Math.max(target.length, source.length);
        JsonValue[] merged = new JsonValue[length];

        for (int i = 0; i < length; i++) {
            if (i < source.length) { // Source has element at position i
                if (i < target.length) {
                    if (source[i].isOverlay()) {
                        merged[i] = target[i];
                    } else {
                        merged[i] = mergeValuesByStrategy(MergeStrategy.OVERLAY, target[i], source[i]);
                    }
                } else {
                    merged[i] = source[i]; // Only source has value
                }
            } else {
                merged[i] = target[i]; // Only target has element at position i
            }
        }

        return merged;
    }

    /**
     * Merges two strings based on merge strategy.
     */
    private static JsonValue mergeStrings(MergeStrategy strategy, JsonValue targetValue, JsonValue sourceValue) {
        String targetStr = targetValue.asString();
        String sourceStr = sourceValue.asString();

        String merged = switch (strategy) {
            case PREPEND -> sourceStr + targetStr;
            case APPEND -> targetStr + sourceStr;
            case OVERLAY -> sourceStr + targetStr.substring(Math.min(sourceStr.length(), targetStr.length()));
            default -> sourceStr;
        };
        return new JsonValue(merged);
    }

    /**
     * Merges two integers based on merge strategy.
     */
    private static JsonValue mergeIntegers(MergeStrategy strategy, JsonValue targetValue, JsonValue sourceValue) {
        Integer targetInt = targetValue.asInteger();
        Integer sourceInt = sourceValue.asInteger();

        Integer merged = switch (strategy) {
            case PREPEND, APPEND -> targetInt + sourceInt;
            default -> sourceInt;
        };
        return new JsonValue(merged);
    }

    /**
     * Merges two doubles based on merge strategy.
     */
    private static JsonValue mergeDoubles(MergeStrategy strategy, JsonValue targetValue, JsonValue sourceValue) {
        Double targetInt = targetValue.asDouble();
        Double sourceInt = sourceValue.asDouble();

        Double merged = switch (strategy) {
            case PREPEND, APPEND -> targetInt + sourceInt;
            default -> sourceInt;
        };
        return new JsonValue(merged);
    }


    /**
     * Merges two doubles based on merge strategy.
     */
    private static JsonValue mergeBooleans(MergeStrategy strategy, JsonValue targetValue, JsonValue sourceValue) {
        Boolean target = targetValue.asBoolean();
        Boolean source = sourceValue.asBoolean();

        Boolean merged = switch (strategy) {
            case OVERLAY, OVERLAY_TRUNCATE -> Boolean.logicalAnd(target, source);
            default -> source;
        };
        return new JsonValue(merged);
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