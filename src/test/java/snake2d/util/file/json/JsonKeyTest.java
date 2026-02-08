package snake2d.util.file.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonKey Tests")
class JsonKeyTest {

    @Test
    @DisplayName("Should create a key and store it correctly")
    void shouldCreateAndStoreKey() {
        JsonKey key = new JsonKey("testKey");
        assertEquals("testKey", key.getKey());
    }

    @Test
    @DisplayName("Should correctly compare keys")
    void shouldCompareKeysCorrectly() {
        JsonKey key1 = new JsonKey("key");
        JsonKey key2 = new JsonKey("key");
        JsonKey key3 = new JsonKey("different");

        assertEquals(key1, key2);
        assertNotEquals(key1, key3);
    }

    @Test
    @DisplayName("Should throw an exception for an empty key")
    void shouldThrowExceptionForEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonKey("");
        });
    }

    @Test
    @DisplayName("Should throw an exception for a null key")
    void shouldThrowExceptionForNullKey() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonKey(null);
        });
    }
}
