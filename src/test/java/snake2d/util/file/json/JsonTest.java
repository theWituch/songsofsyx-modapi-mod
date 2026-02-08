package snake2d.util.file.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Json Tests")
class JsonTest {

    @Test
    @DisplayName("Should add and retrieve values")
    void shouldPutAndGetValues() {
        Json json = new Json();
        json.put("name", new JsonValue("Jan"));
        json.put("age", new JsonValue(30));

        assertEquals("Jan", json.get("name").asString());
        assertEquals(30, json.get("age").asInteger());
    }

    @Test
    @DisplayName("Should check key presence")
    void shouldCheckKeyPresence() {
        Json json = new Json();
        json.put("name", new JsonValue("Jan"));

        assertTrue(json.containsKey("name"));
        assertFalse(json.containsKey("age"));
    }

    @Test
    @DisplayName("Should correctly return size and check if empty")
    void shouldReturnSizeAndCheckIfEmpty() {
        Json json = new Json();
        assertTrue(json.isEmpty());
        assertEquals(0, json.size());

        json.put("key1", new JsonValue("value1"));
        json.put("key2", new JsonValue("value2"));

        assertFalse(json.isEmpty());
        assertEquals(2, json.size());
    }

    @Test
    @DisplayName("Should return null for a non-existent key")
    void shouldReturnNullForNonExistentKey() {
        Json json = new Json();
        assertNull(json.get("nonexistent"));
    }
}
