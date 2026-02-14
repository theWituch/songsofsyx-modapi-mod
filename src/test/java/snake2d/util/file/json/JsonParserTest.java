package snake2d.util.file.json;

import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import snake2d.util.file.json.exception.JsonParseException;
import test.utils.TestFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
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

        @Test
        @DisplayName("Should parse JSON with sd characters")
        void shouldParseJsonWithUnderscoreOpenedKey() throws JsonParseException  {
            String jsonString = "_key: \"value ¤\"";
            Json json = parser.parse(jsonString);

            assertEquals("value ¤", json.get("_key").asString());
        }

        @Test
        @DisplayName("Should parse JSON with underscore opened multi word key")
        void shouldParseJsonWithMultiWordUnderscoreOpenedKey() throws JsonParseException  {
            String jsonString = "\"_multi word key\": \"value ¤\"";
            Json json = parser.parse(jsonString);

            assertEquals("value ¤", json.get("_multi word key").asString());
        }

        @Test
        @DisplayName("Should parse JSON with currency sign opened key")
        void shouldParseJsonWithCurrencySignOpenedKey() throws JsonParseException  {
            String jsonString = "¤key: \"value ¤\"";
            Json json = parser.parse(jsonString);

            assertEquals("value ¤", json.get("¤key").asString());
        }

        @Test
        @DisplayName("Should parse JSON with currency sign opened multi word key")
        void shouldParseJsonWithMultiWordCurrencySignOpenedKey() throws JsonParseException  {
            String jsonString = "\"¤multi word key\": \"value ¤\"";
            Json json = parser.parse(jsonString);

            assertEquals("value ¤", json.get("¤multi word key").asString());
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
        @DisplayName("Should detect a missing key")
        void shouldDetectMissingKey() {
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse("{ -1 }");
            });
            assertEquals("Expected key (line 1, column 3)", ex.getCause().getMessage());
        }

        @Test
        @DisplayName("Should detect a missing closing bracket")
        void shouldDetectMissingClosingBracket() {
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(new TestFile("json/parser/test_invalid_missing_bracket.json"));
            });
            assertEquals("Expected '}' at the end of the file (line 6, column 1)", ex.getMessage());
        }

        @Test
        @DisplayName("Should detect a missing closing bracket")
        void shouldDetectMissingClosingBracketInArray() {
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(new TestFile("json/parser/test_invalid_array_missing_bracket.json"));
            });
            assertEquals("Expected closing ']' before '}' (line 4, column 1)", ex.getCause().getMessage());
        }

        @Test
        @DisplayName("Should detect a missing colon")
        void shouldDetectMissingColon() {
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(new TestFile("json/parser/test_invalid_missing_colon.json"));
            });
            assertEquals("Expected ':' after key 'name' (line 3, column 8)", ex.getMessage());
        }

        @Test
        @DisplayName("Should detect a missing colon")
        void shouldDetectMissingColonInArray() {
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(new TestFile("json/parser/test_invalid_array_missing_colon.json"));
            });
            assertEquals("Expected ',' between array elements (line 3, column 22)", ex.getCause().getMessage());
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
    @DisplayName("JsonParser - Array Structures Tests")
    class ArrayStructuresTests {

        @Test
        @DisplayName("Should parse array with mixed structures")
        void shouldParseMixedStructures() throws JsonParseException {
            String json = """
                {
                    items: [
                        id: 1,
                        name: "Item 1",
                        config: {
                            enabled: true
                        },
                        42,
                        "regular string"
                    ]
                }
                """;
            Json result = parser.parse(json);

            JsonValue[] items = result.get("items").asArray();
            assertEquals(5, items.length);
            assertEquals(1, items[0].asInteger());
            assertEquals("Item 1", items[1].asString());
            assertTrue(items[2].asJson().get("enabled").asBoolean());
            assertEquals(42, items[3].asInteger());
            assertEquals("regular string", items[4].asString());
        }

        @Test
        @DisplayName("Should parse nested arrays with structures")
        void shouldParseNestedArraysWithStructures() throws JsonParseException {
            String json = """
                {
                    outer: [
                        inner: [
                            a: 1,
                            b: 2
                        ]
                    ]
                }
                """;
            Json result = parser.parse(json);

            JsonValue[] outer = result.get("outer").asArray();
            JsonValue[] inner = outer[0].asArray();
            assertEquals(2, inner.length);
            assertEquals(1, inner[0].asInteger());
            assertEquals(2, inner[1].asInteger());
        }

        @Test
        @DisplayName("Should value keys be accessible arrays with structures")
        void shouldKeysBeAccessibleForArraysWithStructures() throws JsonParseException {
            String json = "array: [ key_name: 1, \"value\" ]";
            Json result = parser.parse(json);

            JsonValue[] array = result.get("array").asArray();
            assertEquals(2, array.length);

            assertThat(array[0]).isInstanceOf(JsonValue.JsonArrayValue.class);
            JsonValue.JsonArrayValue arrayValue = (JsonValue.JsonArrayValue) array[0];
            assertEquals("key_name", arrayValue.getJsonKey().getKey());

            assertThat(array[1]).isNotInstanceOf(JsonValue.JsonArrayValue.class);
            assertEquals("value",  array[1].asString());
        }

        @Test
        @DisplayName("Should parse arrays with special characters")
        void shouldParse() throws IOException, JsonParseException {
            Json result = parser.parse(new TestFile("json/parser/test_arrays_special_characters.json"));

            JsonValue[] first = result.get("_first").asArray();
            assertEquals(5, first.length);
            assertEquals(1, first[0].asInteger());
            assertEquals(3, first[2].asInteger());

            JsonValue[] second = result.get("_second_array").asArray();
            assertEquals(3, second.length);
            assertEquals("fo_o", second[0].asString());
            assertEquals("ba.r", second[1].asString());
            assertEquals("¤ value", second[2].asString());

            JsonValue[] third = result.get("_some_third.array").asArray();
            assertEquals(2, third.length);
            assertEquals(true, third[0].asBoolean());
            assertEquals(false, third[1].asBoolean());

            assertEquals("¤specialvalue", result.get("¤special_key").asString());

            JsonValue[] array = result.get("_array").asArray();
            assertEquals(4, array.length);
            assertThatJsonArrayValueKeyString(array[0]).isEqualTo("_a");
            assertEquals(1, array[0].asInteger());
            assertThatJsonArrayValueKeyString(array[1]).isEqualTo("_b");
            assertEquals(2, array[1].asInteger());
            assertThatJsonArrayValueKeyString(array[2]).isEqualTo("¤_c");
            assertEquals(3, array[2].asInteger());
            assertThatJsonArrayValueKeyString(array[3]).isEqualTo("c_¤");
            assertEquals(4, array[3].asInteger());

            JsonValue[] outer = result.get("_outer").asArray();
            assertThatJsonArrayValueKeyString(outer[0]).isEqualTo("_inn.er_");
            JsonValue[] inner = outer[0].asArray();
            assertEquals(3, inner.length);
            assertThatJsonArrayValueKeyString(inner[0]).isEqualTo("_a");
            assertEquals(1, inner[0].asInteger());
            assertThatJsonArrayValueKeyString(inner[1]).isEqualTo("_b_key");
            assertEquals(2, inner[1].asInteger());
            assertThatJsonArrayValueKeyString(inner[2]).isEqualTo("¤c");
            assertEquals(3, inner[2].asInteger());
        }

        @Test
        @DisplayName("Should parse array structures with booleans and nulls")
        void shouldParseArrayStructuresWithVariousTypes() throws JsonParseException {
            String json = """
                {
                    data: [
                        flag: true,
                        empty: null,
                        count: 42,
                        price: 19.99
                    ]
                }
                """;
            Json result = parser.parse(json);

            JsonValue[] data = result.get("data").asArray();
            assertEquals(4, data.length);
            assertTrue(data[0].asBoolean());
            assertTrue(data[1].isNull());
            assertEquals(42, data[2].asInteger());
            assertEquals(19.99, data[3].asDouble(), 0.001);
        }

        @Test
        @DisplayName("Should parse array with structures")
        void shouldParseArrayWithKeyValuePairs() throws IOException, JsonParseException {
            Json result = parser.parse(new TestFile("json/parser/test_arrays_structures.json"));

            JsonValue[] ARRAY = result.get("ARRAY").asArray();
            assertEquals(4, ARRAY.length);

            assertThatJsonArrayValueKeyString(ARRAY[0]).isEqualTo("JSON");
            Json JSON = ARRAY[0].asJson();
            assertEquals("value", JSON.get("key").asString());
            assertEquals("multi value", JSON.get("multi word key").asString());
            Json miniJson = JSON.get("miniJson").asJson();
            assertEquals(true, miniJson.get("mini").asBoolean());

            assertThatJsonArrayValueKeyString(ARRAY[1]).isEqualTo("NAME");
            String NAME = ARRAY[1].asString();
            assertEquals("test", NAME);

            assertThatJsonArrayValueKeyString(ARRAY[2]).isEqualTo("SECOND NAME");
            String SECOND_NAME = ARRAY[2].asString();
            assertEquals("bond", SECOND_NAME);

            assertThatJsonArrayValueKeyString(ARRAY[3]).isEqualTo("AGE");
            Integer AGE = ARRAY[3].asInteger();
            assertEquals(123, AGE);
        }

        @Test
        @DisplayName("Should detect a missing comma for arrays with key pairs")
        void shouldDetectMissingCommaForKeyPairs() {
            String json = """
                {
                    array: [
                        a: 1
                        b: 2
                    ]
                }
                """;
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(json);
            });
            assertEquals("Expected ',' between array elements (line 4, column 9)", ex.getCause().getMessage());
        }

        @Test
        @DisplayName("Should detect a missing closing bracket for nested arrays with structures")
        void shouldDetectMissingClosingBracketForNestedArrays() {
            String json = """
                {
                    outer: [
                        inner: [
                            a: 1,
                            b: 2
                        ]
                    ** Missing closing bracket here
                }
                """;
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(json);
            });
            assertEquals("Expected closing ']' before '}' (line 8, column 1)", ex.getCause().getMessage());
        }
    }

    @Nested
    @DisplayName("JsonParser - Comments Tests")
    class CommentsTests {

        @Test
        @DisplayName("Should skip comment at the beginning of file")
        void shouldSkipCommentAtBeginning() throws JsonParseException {
            String json = """
                ** This is a comment
                {
                    key: "value"
                }
                """;
            Json result = parser.parse(json);
            assertEquals("value", result.get("key").asString());
        }

        @Test
        @DisplayName("Should skip inline comment after value")
        void shouldSkipInlineComment() throws JsonParseException {
            String json = """
                {
                    key: "value" ** This is a comment
                }
                """;
            Json result = parser.parse(json);
            assertEquals("value", result.get("key").asString());
        }

        @Test
        @DisplayName("Should skip comment after key-value pair")
        void shouldSkipCommentAfterKeyValue() throws JsonParseException {
            String json = """
                {
                    key: "value", ** Comment after value
                    key2: "value2"
                }
                """;
            Json result = parser.parse(json);

            assertEquals("value", result.get("key").asString());
            assertEquals("value2", result.get("key2").asString());
        }

        @Test
        @DisplayName("Should skip multiple comments")
        void shouldSkipMultipleComments() throws JsonParseException {
            String json = """
                ** Comment 1
                {
                    ** Comment 2
                    key1: "value1", ** Comment 3
                    key2: "value2" ** Comment 4
                    ** Comment 5
                }
                ** Comment 6
                """;
            Json result = parser.parse(json);

            assertEquals("value1", result.get("key1").asString());
            assertEquals("value2", result.get("key2").asString());
        }

        @Test
        @DisplayName("Should skip comments in nested objects")
        void shouldSkipCommentsInNestedObjects() throws JsonParseException {
            String json = """
                {
                    ** Nested object
                    user: {
                        ** User name
                        name: "John", ** First name
                        age: 30 ** Years
                    }
                }
                """;
            Json result = parser.parse(json);
            Json user = result.get("user").asJson();
            assertEquals("John", user.get("name").asString());
            assertEquals(30, user.get("age").asInteger());
        }

        @Test
        @DisplayName("Should skip comments in arrays")
        void shouldSkipCommentsInArrays() throws JsonParseException {
            String json = """
                {
                    ** Array of numbers
                    numbers: [
                        1, ** First
                        2, ** Second
                        3 ** Third
                    ]
                }
                """;
            Json result = parser.parse(json);
            List<JsonValue> numbers = result.get("numbers").asList();
            assertEquals(3, numbers.size());
            assertEquals(1, numbers.get(0).asInteger());
        }

        @Test
        @DisplayName("Should not treat single asterisk as comment")
        void shouldNotTreatSingleAsteriskAsComment() throws JsonParseException {
            String json = "{ key: \"value*text\" }";
            Json result = parser.parse(json);
            assertEquals("value*text", result.get("key").asString());
        }

        @Test
        @DisplayName("Should handle asterisks inside string values")
        void shouldHandleAsterisksInStrings() throws JsonParseException {
            String json = "{ key: \"text ** with asterisks\" }";
            Json result = parser.parse(json);
            assertEquals("text ** with asterisks", result.get("key").asString());
        }

        @Test
        @DisplayName("Should skip comment without root brackets")
        void shouldSkipCommentWithoutBrackets() throws JsonParseException {
            String json = """
                ** Configuration
                host: "localhost", ** Server
                port: 8080 ** Port
                """;
            Json result = parser.parse(json);
            assertEquals("localhost", result.get("host").asString());
            assertEquals(8080, result.get("port").asInteger());
        }

        @Test
        @DisplayName("Should skip empty comment")
        void shouldSkipEmptyComment() throws JsonParseException {
            String json = "{ key: \"value\" ** }";
            Json result = parser.parse(json);
            assertEquals("value", result.get("key").asString());
        }

        @Test
        @DisplayName("Should skip comment with special characters")
        void shouldSkipCommentWithSpecialChars() throws JsonParseException {
            String json = "{ key: \"value\" ** ¤!@#$%^&*(){}[]<>?/\\| }";
            Json result = parser.parse(json);
            assertEquals("value", result.get("key").asString());
        }

        @Test
        @DisplayName("Should detect a missing closing bracket")
        void shouldDetectMissingClosingBracket() {
            String json = "{ key: \"value\" ** !@#$%^&*(){}[]<>?/\\|";
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(json);
            });
            assertEquals("Expected '}' at the end of the file (line 1, column 39)", ex.getMessage());
        }

        @Test
        @DisplayName("Should detect a missing colon")
        void shouldDetectMissingColon() {
            String json = "{ key \"value\" ** !@#$%^&*(){}[]<>?/\\|";
            Exception ex = assertThrows(JsonParseException.class, () -> {
                parser.parse(json);
            });
            assertEquals("Expected ':' after key 'key' (line 1, column 7)", ex.getMessage());
        }

        @Test
        @DisplayName("Should handle configuration file with documentation comments")
        void shouldHandleConfigWithDocComments() throws JsonParseException {
            String json = """
                ** Application Configuration
                ** Generated: 2026-02-03
                {
                    ** Server settings
                    server: {
                        host: "0.0.0.0", ** Bind to all interfaces
                        port: 8080 ** HTTP port
                        enabled: true ** Should start?
                    },
                    
                    ** Database configuration
                    database: {
                        host: "localhost", ** DB server
                        port: 5432 ** PostgreSQL default
                    }
                }
                """;
            Json result = parser.parse(json);

            Json server = result.get("server").asJson();
            assertEquals("0.0.0.0", server.get("host").asString());
            assertEquals(8080, server.get("port").asInteger());
            assertEquals(true, server.get("enabled").asBoolean());

            Json database = result.get("database").asJson();
            assertEquals("localhost", database.get("host").asString());
            assertEquals(5432, database.get("port").asInteger());
        }

        @Test
        @DisplayName("Should skip comments around null values")
        void shouldSkipCommentsAroundNull() throws JsonParseException {
            String json = """
                {
                    ** Null value
                    empty: null ** This is null
                }
                """;
            Json result = parser.parse(json);

            assertTrue(result.get("empty").isNull());
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

    static AbstractObjectAssert<?, String> assertThatJsonArrayValueKeyString(JsonValue value) {
        return assertThat(value)
                .asInstanceOf(type(JsonValue.JsonArrayValue.class))
                .extracting(JsonValue.JsonArrayValue::getJsonKey)
                .extracting(JsonKey::toString);
    }
}
