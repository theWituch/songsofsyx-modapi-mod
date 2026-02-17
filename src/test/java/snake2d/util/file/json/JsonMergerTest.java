package snake2d.util.file.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import snake2d.util.file.json.exception.JsonParseException;
import test.utils.TestFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static snake2d.util.file.json.JsonParserTest.assertThatJsonArrayValueKeyString;

@DisplayName("JsonMerger Tests")
public class JsonMergerTest {

    @Nested
    @DisplayName("Basic Merge Tests")
    class BasicMergeTests {

        @Test
        @DisplayName("Should merge two empty objects")
        void shouldMergeTwoEmptyObjects() {
            Json json1 = new Json();
            Json json2 = new Json();

            Json result = JsonMerger.merge(json1, json2);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should merge an empty object with a non-empty one")
        void shouldMergeEmptyWithNonEmpty() {
            Json json1 = new Json();
            Json json2 = new Json();
            json2.put("key", new JsonValue("value"));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals(1, result.size());
            assertEquals("value", result.get("key").asString());
        }

        @Test
        @DisplayName("Should merge two objects with different keys")
        void shouldMergeTwoObjectsWithDifferentKeys() {
            Json json1 = new Json();
            json1.put("key1", new JsonValue("value1"));

            Json json2 = new Json();
            json2.put("key2", new JsonValue("value2"));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals(2, result.size());
            assertEquals("value1", result.get("key1").asString());
            assertEquals("value2", result.get("key2").asString());
        }

        @Test
        @DisplayName("Should override the value from the first object with the value from the second")
        void shouldOverrideValueFromFirstObjectWithSecond() {
            Json json1 = new Json();
            json1.put("key", new JsonValue("old_value"));

            Json json2 = new Json();
            json2.put("key", new JsonValue("new_value"));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals(1, result.size());
            assertEquals("new_value", result.get("key").asString());
        }

        @Test
        @DisplayName("Should return an empty object for null")
        void shouldReturnEmptyObjectForNull() {
            Json result = JsonMerger.merge((Json[]) null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return an empty object for an empty array")
        void shouldReturnEmptyObjectForEmptyArray() {
            Json result = JsonMerger.merge();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should ignore null objects in the array")
        void shouldIgnoreNullObjectsInArray() {
            Json json1 = new Json();
            json1.put("key1", new JsonValue("value1"));

            Json json2 = null;

            Json json3 = new Json();
            json3.put("key2", new JsonValue("value2"));

            Json result = JsonMerger.merge(json1, json2, json3);

            assertEquals(2, result.size());
            assertEquals("value1", result.get("key1").asString());
            assertEquals("value2", result.get("key2").asString());
        }

        @Test
        @DisplayName("Should merge two configurations with partially overlapping keys")
        void shouldMergeTwoConfigurationsWithOverlappingKeys() throws IOException, JsonParseException {
            Json base = new JsonParser().parse(new TestFile("json/parser/test_simple.json"));
            Json override = new JsonParser().parse(new TestFile("json/merger/test_simple_override.json"));

            Json result = JsonMerger.merge(base, override);

            assertEquals("Jan Kowalski", result.get("name").asString());
            assertEquals(40, result.get("age").asInteger());
            assertEquals("Warszawa", result.get("city").asString());
        }
    }

    @Nested
    @DisplayName("Multiple Objects Merge Tests")
    class MultipleObjectsMergeTests {

        @Test
        @DisplayName("Should merge three objects")
        void shouldMergeThreeObjects() {
            Json json1 = new Json();
            json1.put("key1", new JsonValue("value1"));

            Json json2 = new Json();
            json2.put("key2", new JsonValue("value2"));

            Json json3 = new Json();
            json3.put("key3", new JsonValue("value3"));

            Json result = JsonMerger.merge(json1, json2, json3);

            assertEquals(3, result.size());
            assertEquals("value1", result.get("key1").asString());
            assertEquals("value2", result.get("key2").asString());
            assertEquals("value3", result.get("key3").asString());
        }

        @Test
        @DisplayName("Should override values in order")
        void shouldOverrideValuesInOrder() {
            Json json1 = new Json();
            json1.put("key", new JsonValue("first"));

            Json json2 = new Json();
            json2.put("key", new JsonValue("second"));

            Json json3 = new Json();
            json3.put("key", new JsonValue("third"));

            Json result = JsonMerger.merge(json1, json2, json3);

            assertEquals(1, result.size());
            assertEquals("third", result.get("key").asString());
        }

        @Test
        @DisplayName("Should merge multiple objects with partially overlapping keys")
        void shouldMergeMultipleObjectsWithOverlappingKeys() {
            Json json1 = new Json();
            json1.put("a", new JsonValue(1));
            json1.put("b", new JsonValue(2));

            Json json2 = new Json();
            json2.put("b", new JsonValue(20));
            json2.put("c", new JsonValue(30));

            Json json3 = new Json();
            json3.put("c", new JsonValue(300));
            json3.put("d", new JsonValue(400));

            Json result = JsonMerger.merge(json1, json2, json3);

            assertEquals(4, result.size());
            assertEquals(1, result.get("a").asInteger());
            assertEquals(20, result.get("b").asInteger());
            assertEquals(300, result.get("c").asInteger());
            assertEquals(400, result.get("d").asInteger());
        }

        @Test
        @DisplayName("Should merge multiple configurations with partially overlapping keys")
        void shouldMergeMultipleConfigurationsWithOverlappingKeys() throws IOException, JsonParseException {
            Json base = new JsonParser().parse(new TestFile("json/parser/test_simple.json"));
            Json override1 = new JsonParser().parse(new TestFile("json/merger/test_simple_override.json"));
            Json override2 = new JsonParser().parse(new TestFile("json/merger/test_simple_override_2.json"));

            Json result = JsonMerger.merge(base, override1, override2);

            assertEquals("Jan Kowalski", result.get("name").asString());
            assertEquals(50, result.get("age").asInteger());
            assertEquals("Sosnowiec", result.get("city").asString());
        }
    }

    @Nested
    @DisplayName("List Merge Tests")
    class ListMergeTests {

        @Test
        @DisplayName("Should merge a list of objects")
        void shouldMergeListOfObjects() {
            Json json1 = new Json();
            json1.put("key1", new JsonValue("value1"));

            Json json2 = new Json();
            json2.put("key2", new JsonValue("value2"));

            List<Json> list = Arrays.asList(json1, json2);
            Json result = JsonMerger.merge(list);

            assertEquals(2, result.size());
            assertEquals("value1", result.get("key1").asString());
            assertEquals("value2", result.get("key2").asString());
        }

        @Test
        @DisplayName("Should return an empty object for an empty list")
        void shouldReturnEmptyObjectForEmptyList() {
            List<Json> list = Arrays.asList();
            Json result = JsonMerger.merge(list);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return an empty object for a null list")
        void shouldReturnEmptyObjectForNullList() {
            Json result = JsonMerger.merge((List<Json>) null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Deep Merge Tests")
    class DeepMergeTests {

        @Test
        @DisplayName("Should deeply merge nested objects")
        void shouldDeepMergeNestedObjects() {
            Json json1 = new Json();
            Json nested1 = new Json();
            nested1.put("a", new JsonValue(1));
            nested1.put("b", new JsonValue(2));
            json1.put("nested", new JsonValue(nested1));

            Json json2 = new Json();
            Json nested2 = new Json();
            nested2.put("b", new JsonValue(20));
            nested2.put("c", new JsonValue(30));
            json2.put(new JsonKey("nested", MergeStrategy.OVERLAY), new JsonValue(nested2));

            Json result = JsonMerger.merge(json1, json2);

            Json resultNested = result.get("nested").asJson();
            assertEquals(3, resultNested.size());
            assertEquals(1, resultNested.get("a").asInteger());
            assertEquals(20, resultNested.get("b").asInteger());
            assertEquals(30, resultNested.get("c").asInteger());
        }

        @Test
        @DisplayName("Should deeply merge multi-level nested objects")
        void shouldDeepMergeMultiLevelNestedObjects() {
            Json json1 = new Json();
            Json level1_1 = new Json();
            Json level2_1 = new Json();
            level2_1.put("x", new JsonValue(1));
            level1_1.put("level2", new JsonValue(level2_1));
            json1.put("level1", new JsonValue(level1_1));

            Json json2 = new Json();
            Json level1_2 = new Json();
            Json level2_2 = new Json();
            level2_2.put("y", new JsonValue(2));
            level1_2.put("level2", new JsonValue(level2_2));
            json2.put("level1", new JsonValue(level1_2));

            Json result = JsonMerger.merge(json1, json2);

            Json level1 = result.get("level1").asJson();
            Json level2 = level1.get("level2").asJson();
            assertEquals(2, level2.size());
            assertEquals(1, level2.get("x").asInteger());
            assertEquals(2, level2.get("y").asInteger());
        }

        @Test
        @DisplayName("Should override a nested object with a simple value")
        void shouldOverrideNestedObjectWithSimpleValue() {
            Json json1 = new Json();
            Json nested = new Json();
            nested.put("a", new JsonValue(1));
            json1.put("key", new JsonValue(nested));

            Json json2 = new Json();
            json2.put("key", new JsonValue("simple"));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals("simple", result.get("key").asString());
        }

        @Test
        @DisplayName("Should override a simple value with a nested object")
        void shouldOverrideSimpleValueWithNestedObject() {
            Json json1 = new Json();
            json1.put("key", new JsonValue("simple"));

            Json json2 = new Json();
            Json nested = new Json();
            nested.put("a", new JsonValue(1));
            json2.put("key", new JsonValue(nested));

            Json result = JsonMerger.merge(json1, json2);

            Json resultNested = result.get("key").asJson();
            assertEquals(1, resultNested.size());
            assertEquals(1, resultNested.get("a").asInteger());
        }
    }

    @Nested
    @DisplayName("Shallow Merge Tests")
    class ShallowMergeTests {

        @Test
        @DisplayName("Should shallow merge objects overriding nested objects")
        void shouldShallowMergeOverridingNestedObjects() {
            Json json1 = new Json();
            Json nested1 = new Json();
            nested1.put("a", new JsonValue(1));
            nested1.put("b", new JsonValue(2));
            json1.put("nested", new JsonValue(nested1));

            Json json2 = new Json();
            Json nested2 = new Json();
            nested2.put("c", new JsonValue(3));
            json2.put("nested", new JsonValue(nested2));

            Json result = JsonMerger.shallowMerge(json1, json2);

            Json resultNested = result.get("nested").asJson();
            assertEquals(1, resultNested.size());
            assertFalse(resultNested.containsKey("a"));
            assertFalse(resultNested.containsKey("b"));
            assertEquals(3, resultNested.get("c").asInteger());
        }

        @Test
        @DisplayName("Should shallow merge simple values")
        void shouldShallowMergeSimpleValues() {
            Json json1 = new Json();
            json1.put("key1", new JsonValue("value1"));

            Json json2 = new Json();
            json2.put("key2", new JsonValue("value2"));

            Json result = JsonMerger.shallowMerge(json1, json2);

            assertEquals(2, result.size());
            assertEquals("value1", result.get("key1").asString());
            assertEquals("value2", result.get("key2").asString());
        }
    }

    @Nested
    @DisplayName("Different Types Merge Tests")
    class DifferentTypesMergeTests {

        @Test
        @DisplayName("Should merge objects with different value types")
        void shouldMergeObjectsWithDifferentValueTypes() {
            Json json1 = new Json();
            json1.put("str", new JsonValue("text"));
            json1.put("num", new JsonValue(42));

            Json json2 = new Json();
            json2.put("bool", new JsonValue(true));
            json2.put("dbl", new JsonValue(3.14));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals(4, result.size());
            assertEquals("text", result.get("str").asString());
            assertEquals(42, result.get("num").asInteger());
            assertTrue(result.get("bool").asBoolean());
            assertEquals(3.14, result.get("dbl").asDouble(), 0.001);
        }

        @Test
        @DisplayName("Should override a value type with a different type")
        void shouldOverrideValueTypeWithDifferentType() {
            Json json1 = new Json();
            json1.put("key", new JsonValue(123));

            Json json2 = new Json();
            json2.put("key", new JsonValue("text"));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals("text", result.get("key").asString());
        }

        @Test
        @DisplayName("Should merge objects with arrays")
        void shouldMergeObjectsWithArrays() {
            Json json1 = new Json();
            JsonValue[] array1 = {new JsonValue(1), new JsonValue(2)};
            json1.put("arr", new JsonValue(array1));

            Json json2 = new Json();
            JsonValue[] array2 = {new JsonValue(3), new JsonValue(4)};
            json2.put("arr", new JsonValue(array2));

            Json result = JsonMerger.merge(json1, json2);

            JsonValue[] resultArray = result.get("arr").asArray();
            assertEquals(2, resultArray.length);
            assertEquals(3, resultArray[0].asInteger());
            assertEquals(4, resultArray[1].asInteger());
        }

        @Test
        @DisplayName("Should merge objects with null values")
        void shouldMergeObjectsWithNullValues() {
            Json json1 = new Json();
            json1.put("key", new JsonValue("value"));

            Json json2 = new Json();
            json2.put("key", new JsonValue(null));

            Json result = JsonMerger.merge(json1, json2);

            assertTrue(result.get("key").isNull());
        }
    }

    @Nested
    @DisplayName("Original Objects Preservation Tests")
    class OriginalObjectsPreservationTests {

        @Test
        @DisplayName("Should not modify original objects")
        void shouldNotModifyOriginalObjects() {
            Json json1 = new Json();
            json1.put("key1", new JsonValue("value1"));

            Json json2 = new Json();
            json2.put("key2", new JsonValue("value2"));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals(1, json1.size());
            assertEquals(1, json2.size());
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should not modify original objects when overriding")
        void shouldNotModifyOriginalObjectsWhenOverriding() {
            Json json1 = new Json();
            json1.put("key", new JsonValue("old"));

            Json json2 = new Json();
            json2.put("key", new JsonValue("new"));

            Json result = JsonMerger.merge(json1, json2);

            assertEquals("old", json1.get("key").asString());
            assertEquals("new", json2.get("key").asString());
            assertEquals("new", result.get("key").asString());
        }
    }

    @Nested
    @DisplayName("Complex Scenarios Tests")
    class ComplexScenariosTests {

        @Test
        @DisplayName("Should merge configurations with default and overridden values")
        void shouldMergeConfigurationsWithDefaultsAndOverrides() {
            Json defaults = new Json();
            defaults.put("host", new JsonValue("localhost"));
            defaults.put("port", new JsonValue(8080));
            defaults.put("timeout", new JsonValue(30));

            Json userConfig = new Json();
            userConfig.put("host", new JsonValue("example.com"));
            userConfig.put("port", new JsonValue(9000));

            Json envConfig = new Json();
            envConfig.put("port", new JsonValue(3000));

            Json result = JsonMerger.merge(defaults, userConfig, envConfig);

            assertEquals("example.com", result.get("host").asString());
            assertEquals(3000, result.get("port").asInteger());
            assertEquals(30, result.get("timeout").asInteger());
        }

        @Test
        @DisplayName("Should merge nested configurations")
        void shouldMergeNestedConfigurations() {
            Json base = new Json();
            Json dbConfig = new Json();
            dbConfig.put("host", new JsonValue("localhost"));
            dbConfig.put("port", new JsonValue(5432));
            base.put("database", new JsonValue(dbConfig));

            Json override = new Json();
            Json dbOverride = new Json();
            dbOverride.put("port", new JsonValue(3306));
            dbOverride.put("user", new JsonValue("admin"));
            override.put("database", new JsonValue(dbOverride));

            Json result = JsonMerger.merge(base, override);

            Json resultDb = result.get("database").asJson();
            assertEquals("localhost", resultDb.get("host").asString());
            assertEquals(3306, resultDb.get("port").asInteger());
            assertEquals("admin", resultDb.get("user").asString());
        }
    }

    @Nested
    @DisplayName("Real Usage Scenarios Tests")
    class RealUsageTests {

        @Test
        @DisplayName("Should merge configurations with default and overridden values")
        void shouldMergeTwoJsonsWithoutOverrides() throws IOException, JsonParseException {
            Json base = new JsonParser().parse(new TestFile("json/parser/test_arrays_special_characters.json"));
            Json addon = new JsonParser().parse(new TestFile("json/parser/test_arrays_structures.json"));

            Json result = JsonMerger.merge(base, addon);

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
        @DisplayName("Should merge configurations and override certain keys")
        void shouldMergeTwoJsonsWithOverridesForCertainKeys2() throws IOException, JsonParseException {
            Json base = new JsonParser().parse(new TestFile("json/parser/test_arrays.json"));
            Json override = new JsonParser().parse(new TestFile("json/merger/test_arrays_override.json"));

            Json result = JsonMerger.merge(base, override);

            List<JsonValue> items = result.get("items").asList();
            assertEquals(5, items.size());
            assertEquals(1, items.get(0).asInteger());
            assertEquals(5, items.get(4).asInteger());

            List<JsonValue> names = result.get("names").asList();
            assertEquals(3, names.size());
            assertEquals("Alice", names.get(0).asString());
            assertEquals("Charlie", names.get(2).asString());

            List<JsonValue> mixed = result.get("mixed").asList();
            assertEquals(4, mixed.size());
            assertEquals(1, mixed.get(0).asInteger());
            assertEquals("text", mixed.get(1).asString());
            assertTrue(mixed.get(2).asBoolean());
            assertEquals(3.14, mixed.get(3).asDouble(), 0.001);

            List<JsonValue> empty = result.get("empty").asList();
            assertEquals(0, empty.size());
        }

        @Test
        @DisplayName("Should merge configurations containing structures which overrides")
        void shouldMergeTwoJsonsWithOverrides() throws IOException, JsonParseException {
            Json base = new JsonParser().parse(new TestFile("json/parser/test_arrays_structures.json"));
            Json override = new JsonParser().parse(new TestFile("json/merger/test_arrays_structures_override.json"));

            Json result = JsonMerger.merge(base, override);

            JsonValue[] ARRAY = result.get("ARRAY").asArray();
            assertEquals(4, ARRAY.length);

            assertThatJsonArrayValueKeyString(ARRAY[0]).isEqualTo("JSON");
            Json JSON = ARRAY[0].asJson();
            assertEquals("new value", JSON.get("key").asString());
            assertEquals("new multi value", JSON.get("multi word key").asString());
            Json miniJson = JSON.get("miniJson").asJson();
            assertEquals(false, miniJson.get("mini").asBoolean());

            assertThatJsonArrayValueKeyString(ARRAY[1]).isEqualTo("NAME");
            String NAME = ARRAY[1].asString();
            assertEquals("new name", NAME);

            assertThatJsonArrayValueKeyString(ARRAY[2]).isEqualTo("SECOND NAME");
            String SECOND_NAME = ARRAY[2].asString();
            assertEquals("james", SECOND_NAME);

            assertThatJsonArrayValueKeyString(ARRAY[3]).isEqualTo("AGE");
            Integer AGE = ARRAY[3].asInteger();
            assertEquals(321, AGE);
        }

        @Test
        @DisplayName("Should merge configurations containing special characters which overrides")
        void shouldMergeTwoJsonsWithSpecialCharacterWithOverrides() throws IOException, JsonParseException {
            Json base = new JsonParser().parse(new TestFile("json/parser/test_arrays_special_characters.json"));
            Json override = new JsonParser().parse(new TestFile("json/merger/test_arrays_special_characters_override.json"));

            Json result = JsonMerger.merge(base, override);

            JsonValue[] first = result.get("_first").asArray();
            assertEquals(2, first.length);
            assertEquals(1, first[0].asInteger());
            assertEquals(2, first[1].asInteger());

            JsonValue[] second = result.get("_second_array").asArray();
            assertEquals(2, second.length);
            assertEquals("fo_onew", second[0].asString());
            assertEquals("ba.rrr", second[1].asString());

            JsonValue[] third = result.get("_some_third.array").asArray();
            assertEquals(2, third.length);
            assertEquals(true, third[0].asBoolean());
            assertEquals(false, third[1].asBoolean());

            assertEquals("¤specialvalue", result.get("¤special_key").asString());

            JsonValue[] array = result.get("_array").asArray();
            assertEquals(5, array.length);
            assertThatJsonArrayValueKeyString(array[0]).isEqualTo("_a");
            assertEquals(1, array[0].asInteger());
            assertThatJsonArrayValueKeyString(array[1]).isEqualTo("_b");
            assertEquals(3, array[1].asInteger());
            assertThatJsonArrayValueKeyString(array[2]).isEqualTo("¤_c");
            assertEquals(3, array[2].asInteger());
            assertThatJsonArrayValueKeyString(array[3]).isEqualTo("c_¤");
            assertEquals(4, array[3].asInteger());
            assertThatJsonArrayValueKeyString(array[4]).isEqualTo("d");
            assertEquals(5, array[4].asInteger());

            JsonValue[] outer = result.get("_outer").asArray();
            assertEquals(1, outer.length);
            assertThatJsonArrayValueKeyString(outer[0]).isEqualTo("_inn.er2_");
            JsonValue[] inner2 = outer[0].asArray();
            assertEquals(2, inner2.length);
            assertThatJsonArrayValueKeyString(inner2[0]).isEqualTo("_a");
            assertEquals(3, inner2[0].asInteger());
            assertThatJsonArrayValueKeyString(inner2[1]).isEqualTo("¤c");
            assertEquals(6, inner2[1].asInteger());

            JsonValue[] newArray = result.get("new array").asArray();
            assertEquals(2, newArray.length);
            assertEquals(1, newArray[0].asInteger());
            assertEquals(2, newArray[1].asInteger());
        }
    }
}
