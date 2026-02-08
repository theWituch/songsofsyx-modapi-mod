package snake2d.util.file.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import snake2d.util.file.json.exception.JsonParseException;
import test.utils.TestFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonParser Tests")
public class JsonParserTest {

    private JsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new JsonParser();
    }

    @Nested
    @DisplayName("JsonParser - String Parsing Tests")
    class StringParsingTests {

        @Test
        @DisplayName("Should parse full JSON from string")
        void shouldParseFromString() throws JsonParseException  {
            String jsonString = "{ name: \"Full JSON\", age: 25 }";
            Json json = parser.parse(jsonString);

            assertEquals("Full JSON", json.get("name").asString());
            assertEquals(25, json.get("age").asInteger());
        }

        @Test
        @DisplayName("Should parse no-brackets JSON from string")
        void shouldParseFromStringWithoutBrackets() throws JsonParseException  {
            String jsonString = "name: \"No brackets\", age: 12";
            Json json = parser.parse(jsonString);

            assertEquals("No brackets", json.get("name").asString());
            assertEquals(12, json.get("age").asInteger());
        }

        @Test
        @DisplayName("Should parse minimal JSON for text value")
        void shouldParseMinimalJsonForText() throws JsonParseException  {
            String jsonString = "key: \"value\"";
            Json json = parser.parse(jsonString);

            assertEquals("value", json.get("key").asString());
        }

        @Test
        @DisplayName("Should parse minimal JSON for numeric value")
        void shouldParseMinimalJsonForNumeric() throws JsonParseException  {
            String jsonString = "key: 123";
            Json json = parser.parse(jsonString);

            assertEquals(123, json.get("key").asInteger());
        }

        @Test
        @DisplayName("Should parse minimal JSON of multi word key for text value")
        void shouldParseMinimalJsonWithMultiWordKeyForText() throws JsonParseException  {
            String jsonString = "\"multi word key\": \"value\"";
            Json json = parser.parse(jsonString);

            assertEquals("value", json.get("multi word key").asString());
        }

        @Test
        @DisplayName("Should parse minimal JSON of multi word key for numeric value")
        void shouldParseMinimalJsonWithMultiWordKeyForNumeric() throws JsonParseException  {
            String jsonString = "\"multi word key\": 123";
            Json json = parser.parse(jsonString);

            assertEquals(123, json.get("multi word key").asInteger());
        }
    }

    @Nested
    @DisplayName("JsonParser - File Parsing Tests")
    class BasicParsingTests {

        @Test
        @DisplayName("Should parse a simple JSON file")
        void shouldParseSimpleFile() throws IOException, JsonParseException {
            Json json = parser.parse(new TestFile("json/parser/test_simple.json"));

            assertEquals("Jan Kowalski", json.get("name").asString());
            assertEquals(30, json.get("age").asInteger());
            assertEquals("Warszawa", json.get("city").asString());
        }

        @Test
        @DisplayName("Should parse a file without root brackets")
        void shouldParseFileWithoutRootBrackets() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/parser/test_no_brackets.json"));

            assertEquals("Anna Nowak", json.get("name").asString());
            assertEquals(25, json.get("age").asInteger());
            assertEquals("anna@example.com", json.get("email").asString());
            assertTrue(json.get("active").asBoolean());
        }

        @Test
        @DisplayName("Should be resilient to formatting errors")
        void shouldBeFormatErrorResilient() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/parser/test_whitespace.json"));

            assertEquals("Test User", json.get("name").asString());
            assertEquals(28, json.get("age").asInteger());
            assertEquals("Poznań", json.get("city").asString());
            assertEquals("Poland", json.get("country").asString());
        }

        @Test
        @DisplayName("Should parse all data types")
        void shouldParseAllDataTypes() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/parser/test_types.json"));

            assertEquals("Hello World", json.get("stringValue").asString());
            assertEquals(42, json.get("intValue").asInteger());
            assertEquals(3.14159, json.get("doubleValue").asDouble(), 0.00001);
            assertTrue(json.get("boolTrue").asBoolean());
            assertFalse(json.get("boolFalse").asBoolean());
            assertTrue(json.get("nullValue").isNull());
            assertEquals(-100, json.get("negativeInt").asInteger());
            assertEquals(-2.5, json.get("negativeDouble").asDouble(), 0.001);
            assertEquals(1.5e10, json.get("scientificNotation").asDouble(), 1e8);
        }
    }

    @Nested
    @DisplayName("JsonParser - Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw IOException for a non-existent file")
        void shouldThrowIOExceptionForNonExistentFile() {
            Exception ex = assertThrows(IOException.class, () -> {
                parser.parse(new TestFile("json/parser/nonexistent.json"));
            });
            assertEquals("Test resource not found: json/parser/nonexistent.json", ex.getMessage());
        }

        @Test
        @DisplayName("Should detect a missing closing bracket")
        void shouldDetectMissingClosingBracket() {
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(new TestFile("json/parser/test_invalid_missing_bracket.json"));
            });
            assertEquals("Expected '}' at the end of the file (line 4, column 1)", ex.getMessage());
        }

        @Test
        @DisplayName("Should detect a missing colon")
        void shouldDetectMissingColon() {
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(new TestFile("json/parser/test_invalid_missing_colon.json"));
            });
            assertEquals("Expected ':' after key 'name' (line 2, column 8)", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("JsonParser - Structures Tests")
    class StructuresTests {

        @Test
        @DisplayName("Should parse nested objects")
        void shouldParseNestedObjects() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/parser/test_nested.json"));

            assertEquals("Piotr", json.get("user").asString());

            Json profile = json.get("profile").asJson();
            assertEquals(35, profile.get("age").asInteger());
            assertEquals("Poland", profile.get("country").asString());

            Json settings = json.get("settings").asJson();
            assertEquals("dark", settings.get("theme").asString());
            assertTrue(settings.get("notifications").asBoolean());

            Json preferences = settings.get("preferences").asJson();
            assertEquals("pl", preferences.get("language").asString());
            assertEquals("UTC+1", preferences.get("timezone").asString());
        }

        @Test
        @DisplayName("Should parse different types of arrays")
        void shouldParseArrays() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/parser/test_arrays.json"));

            List<JsonValue> items = json.get("items").asList();
            assertEquals(5, items.size());
            assertEquals(1, items.get(0).asInteger());
            assertEquals(5, items.get(4).asInteger());

            List<JsonValue> names = json.get("names").asList();
            assertEquals(3, names.size());
            assertEquals("Alice", names.get(0).asString());
            assertEquals("Charlie", names.get(2).asString());

            List<JsonValue> mixed = json.get("mixed").asList();
            assertEquals(4, mixed.size());
            assertEquals(1, mixed.get(0).asInteger());
            assertEquals("text", mixed.get(1).asString());
            assertTrue(mixed.get(2).asBoolean());
            assertEquals(3.14, mixed.get(3).asDouble(), 0.001);

            List<JsonValue> empty = json.get("empty").asList();
            assertEquals(0, empty.size());
        }

        @Test
        @DisplayName("Should parse a complex structure")
        void shouldParseComplexStructure() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/parser/test_complex.json"));

            assertEquals("Tech Corp", json.get("company").asString());

            List<JsonValue> employees = json.get("employees").asList();
            assertEquals(2, employees.size());

            Json employee1 = employees.get(0).asJson();
            assertEquals("John Doe", employee1.get("name").asString());
            assertEquals("Developer", employee1.get("position").asString());
            assertEquals(5000.50, employee1.get("salary").asDouble(), 0.01);

            Json departments = json.get("departments").asJson();
            Json itDept = departments.get("IT").asJson();
            assertEquals(100000, itDept.get("budget").asInteger());

            List<JsonValue> projects = itDept.get("projects").asList();
            assertEquals(2, projects.size());
            assertEquals("Project A", projects.get(0).asString());
        }
    }

    @Nested
    @DisplayName("JsonParser - Game Assets Tests")
    class GameAssetsTests {

        @Test
        @DisplayName("Should parse _RANKS.txt")
        void shouldParseGameAssetsFile1() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/gameassets/_RANKS.txt"));

            JsonValue[] names = json.get("RANKS").asArray();
            assertEquals(10, names.length);
            assertEquals("Sparabar", names[0].asString());
            assertEquals("Asbari", names[2].asString());
        }

        @Test
        @DisplayName("Should parse 000_Tutorial.txt")
        void shouldParseGameAssetsFile2() throws IOException, JsonParseException  {
            Json json = parser.parse(new TestFile("json/gameassets/000_Tutorial.txt"));

            assertEquals("New Cretonia #1 (Tutorial)", json.get("NAME").asString());

            Json cutscene = json.get("CUTSCENE").asJson();
            assertEquals("The struggles of Hotam Greattusk", cutscene.get("TITLE").asString());

            final String body = """
                    
                    Since their creation, the stout, yet short, Cretonians once inhabited the large and fertile plains of Coralia. They first made contact with Men in the year 349 of the Third Age, as a trading vessel from the kingdom of Agari went off course and encountered one of their settlements and after some initial suspicion a trade agreement was struck.
                    %r%
                    %r%
                    The Cretonians are many things, but they do not possess a business mind. Men offered many new things, and trinkets that they had not seen before and they gladly traded gems for glass beads, and fine meat and hides for watered down Piwa. Eventually the Cretonians were in debt and started trading their lands for what they now had grown to depend on.\s
                    %r%
                    %r%
                    The Agarians were not the only kingdom, soon the northern Franshi kingdom, and the  Ennurian nomads established colonies to sap the richness of the Cretonian ancestral lands.  In 353, the War of Golden Rose broke out, and it quickly spread to Coralia. All three parties enlisted Cretonians in their ranks. Despite their short stature, they possess great strength.
                    %r%
                    %r%
                    The war was brutal, and many great heroes fell,  The Franshi burned and slaughtered those Cretonian tribes that had picked the wrong side. Coralia laid in ruins.
                    %r%
                    %r%
                    It was at this time that a Cretonian chieftain by the name of Hotam Greattusk was declared Numbi by the Shamans, and tasked with finding a new home for his people. He gathered what was left of his tribe and set out to cross the Prygian mountains in search of a new home.  Finally he reached the plains of Samba and settled by the river of Krios. He called this land \"New Cretonia\".
                    \t""";
            assertEquals(body, cutscene.get("BODY").asString());

            JsonValue[] BUILD_WAREHOUSE = json.get("UI").asJson().get("BUILD_WAREHOUSE").asArray();
            assertEquals(5, BUILD_WAREHOUSE.length);
            assertEquals("1. Click this button. Then click and hold the left mouse button on the map to assign the area of the warehouse.", BUILD_WAREHOUSE[0].asString());
            assertEquals("4. Now click an item here (crates), and place at least 6 of them within the designated area of the room.", BUILD_WAREHOUSE[3].asString());
        }

        @Test
        @DisplayName("Should parse Dic.txt")
        void shouldParseGameAssetsFile3() throws IOException, JsonParseException {
            Json json = parser.parse(new TestFile("json/gameassets/Dic.txt"));

            Json BOOSTABLES = json.get("game.boosting.BOOSTABLES").asJson();
            assertEquals("Cold Resistance", BOOSTABLES.get("PHYSICS_RESISTANCE_COLD").asString());

            Json SFrame = json.get("view.sett.ui.room.construction.SFrame").asJson();
            assertEquals("{0} construction", SFrame.get("{0} construction").asString());

            Json RoomEmploymentIns = json.get("settlement.room.main.employment.RoomEmploymentIns").asJson();
            assertEquals("Industry workers can fetch the input for the industry without problems if the distance is short. If long, then productivity will suffer.", RoomEmploymentIns.get("¤¤ProximityInputD").asString());
        }
    }
}
