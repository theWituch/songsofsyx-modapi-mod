package snake2d.util.file.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class representing a JSON value, which can store various data types.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class JsonValue {
    private Object value;
    private ValueType type;

    public enum ValueType {
        STRING, INTEGER, DOUBLE, BOOLEAN, NULL, ARRAY, LIST, JSON_OBJECT
    }

    public JsonValue(Object value) {
        if (value == null) {
            this.value = null;
            this.type = ValueType.NULL;
        } else if (value instanceof String) {
            this.value = value;
            this.type = ValueType.STRING;
        } else if (value instanceof Integer) {
            this.value = value;
            this.type = ValueType.INTEGER;
        } else if (value instanceof Double) {
            this.value = value;
            this.type = ValueType.DOUBLE;
        } else if (value instanceof Boolean) {
            this.value = value;
            this.type = ValueType.BOOLEAN;
        } else if (value instanceof Object[]) {
            this.value = value;
            this.type = ValueType.ARRAY;
        } else if (value instanceof List) {
            this.value = new ArrayList<>((List<?>) value);
            this.type = ValueType.LIST;
        } else if (value instanceof Json) {
            this.value = value;
            this.type = ValueType.JSON_OBJECT;
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
        }
    }

    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public Object getValue() {
        return value;
    }

    public ValueType getType() {
        return type;
    }

    public String asString() {
        if (type == ValueType.STRING) {
            return (String) value;
        }
        throw new IllegalStateException("Value is not of type String");
    }

    public Integer asInteger() {
        if (type == ValueType.INTEGER) {
            return (Integer) value;
        }
        throw new IllegalStateException("Value is not of type Integer");
    }

    public Double asDouble() {
        if (type == ValueType.DOUBLE) {
            return (Double) value;
        } else if (type == ValueType.INTEGER) {
            return ((Integer) value).doubleValue();
        }
        throw new IllegalStateException("Value is not of type Double");
    }

    public Boolean asBoolean() {
        if (type == ValueType.BOOLEAN) {
            return (Boolean) value;
        }
        throw new IllegalStateException("Value is not of type Boolean");
    }

    @SuppressWarnings("unchecked")
    public List<JsonValue> asList() {
        if (type == ValueType.LIST) {
            return (List<JsonValue>) value;
        }
        if (type == ValueType.ARRAY) {
            JsonValue[] asArray = (JsonValue[]) value;
            return Arrays.stream(asArray).collect(Collectors.toList());
        }
        throw new IllegalStateException("Value is not a list");
    }

    @SuppressWarnings("unchecked")
    public JsonValue[] asArray() {
        if (type == ValueType.ARRAY) {
            return (JsonValue[]) value;
        }
        if (type == ValueType.LIST) {
            List<JsonValue> asList = (List<JsonValue>) value;
            return asList.toArray(new JsonValue[asList.size()]);
        }
        throw new IllegalStateException("Value is not an array");
    }

    public Json asJson() {
        if (type == ValueType.JSON_OBJECT) {
            return (Json) value;
        }
        throw new IllegalStateException("Value is not a JSON object");
    }

    public boolean isNull() {
        return type == ValueType.NULL;
    }

    @Override
    public String toString() {
        if (type == ValueType.NULL) {
            return "null";
        } else if (type == ValueType.STRING) {
            return "\"" + value + "\"";
        } else if (type == ValueType.ARRAY) {
            return value.toString();
        } else if (type == ValueType.JSON_OBJECT) {
            return value.toString();
        }
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonValue jsonValue = (JsonValue) o;
        return Objects.equals(value, jsonValue.value) && type == jsonValue.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    static class JsonArrayValue extends JsonValue {
        private final JsonKey key;

        public JsonArrayValue(JsonKey key, JsonValue value) {
            super(value.getValue());
            this.key = key;
        }

        public JsonKey getJsonKey() {
            return key;
        }
    }
}
