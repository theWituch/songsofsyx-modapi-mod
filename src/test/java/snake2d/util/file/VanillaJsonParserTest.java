package snake2d.util.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test.utils.TestFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Vanilla JsonParser Tests")
public class VanillaJsonParserTest {

    @Test
    @DisplayName("Should parse")
    void shouldParse() {
        String json = """
                array: [
                 KEY1: 1,
                 KEY2: 2,
                ],
                ints: [ 1, 2, ],
                """;
        Json result = new Json(json, "test-content");
        assertNotNull(result);

        String[] values = result.values("array");
        assertEquals(2, values.length);
        assertEquals("KEY1: 1", values[0]);
        assertEquals("KEY2: 2", values[1]);

        int[] ints = result.is("ints");
        assertEquals(2, ints.length);
        assertEquals(1, ints[0]);
        assertEquals(2, ints[1]);
    }

    @Test
    @DisplayName("Should parse _ASYLUM.txt")
    void shouldParseGameAssetsFile4() throws IOException {
        Json result = new Json(new TestFile("json/gameassets/_ASYLUM.txt").toPath());

        Json SPRITES = result.json("SPRITES");
        Json TABLE_COMBO = SPRITES.json("TABLE_COMBO");
        String[] frames = TABLE_COMBO.values("FRAMES");

        assertEquals(2, frames.length);
        assertEquals("COMBO_TABLES: 0", frames[0]);
        assertEquals("COMBO_TABLES: 1", frames[1]);
    }
}
