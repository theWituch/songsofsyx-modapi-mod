package snake2d.util.file.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonValue Tests")
class JsonValueTest {

    @Test
    @DisplayName("Should store String value")
    void shouldStoreStringValue() {
        JsonValue value = new JsonValue("test string");
        assertEquals(JsonValue.ValueType.STRING, value.getType());
        assertEquals("test string", value.asString());
    }

    @Test
    @DisplayName("Should store Integer value")
    void shouldStoreIntegerValue() {
        JsonValue value = new JsonValue(42);
        assertEquals(JsonValue.ValueType.INTEGER, value.getType());
        assertEquals(42, value.asInteger());
    }

    @Test
    @DisplayName("Should store Double value")
    void shouldStoreDoubleValue() {
        JsonValue value = new JsonValue(3.14);
        assertEquals(JsonValue.ValueType.DOUBLE, value.getType());
        assertEquals(3.14, value.asDouble(), 0.001);
    }

    @Test
    @DisplayName("Should store Boolean value")
    void shouldStoreBooleanValue() {
        JsonValue value = new JsonValue(true);
        assertEquals(JsonValue.ValueType.BOOLEAN, value.getType());
        assertTrue(value.asBoolean());
    }

    @Test
    @DisplayName("Should store null value")
    void shouldStoreNullValue() {
        JsonValue value = new JsonValue(null);
        assertEquals(JsonValue.ValueType.NULL, value.getType());
        assertTrue(value.isNull());
    }

    @Test
    @DisplayName("Should store a list")
    void shouldStoreListValue() {
        List<JsonValue> list = List.of(
                new JsonValue(1),
                new JsonValue(2),
                new JsonValue(3)
        );
        JsonValue value = new JsonValue(list);
        assertEquals(JsonValue.ValueType.LIST, value.getType());
        assertEquals(3, value.asList().size());
    }

    @Test
    @DisplayName("Should store an array")
    void shouldStoreArrayValue() {
        JsonValue[] array = new JsonValue[]{
                new JsonValue(1),
                new JsonValue(2),
                new JsonValue(3)
        };
        JsonValue value = new JsonValue(array);
        assertEquals(JsonValue.ValueType.ARRAY, value.getType());
        assertEquals(3, value.asList().size());
    }

    @Test
    @DisplayName("Should store a nested Json object")
    void shouldStoreNestedJsonObject() {
        Json nested = new Json();
        nested.put("key", new JsonValue("value"));

        JsonValue value = new JsonValue(nested);
        assertEquals(JsonValue.ValueType.JSON_OBJECT, value.getType());
        assertEquals("value", value.asJson().get("key").asString());
    }

    @Test
    @DisplayName("Should throw an exception on invalid type conversion")
    void shouldThrowExceptionOnInvalidTypeConversion() {
        JsonValue stringValue = new JsonValue("text");
        assertThrows(IllegalStateException.class, () -> stringValue.asInteger());
    }

}
