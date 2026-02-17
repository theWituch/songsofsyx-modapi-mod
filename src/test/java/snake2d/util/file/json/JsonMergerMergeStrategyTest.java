package snake2d.util.file.json;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import snake2d.util.file.json.exception.JsonParseException;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonMerger MergeStrategy Tests")
public class JsonMergerMergeStrategyTest {

    @Nested
    @DisplayName("Merge Tests - String")
    class StringMergeTests {

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("testPayload")
        @DisplayName("Should merge using strategy")
        void shouldMergeUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> testPayload() {
            String baseContent = """
                key: "value"
                """;
            String patchContent = """
                key: "new"
                """;
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals("new", json.get("key").asString());
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals("new", json.get("key").asString());
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals("newvalue", json.get("key").asString());
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals("valuenew", json.get("key").asString());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals("newue", json.get("key").asString());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals("new", json.get("key").asString());
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("key"));
                    })
            );
        }

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("arraysTestPayload")
        @DisplayName("Should merge arrays using strategy")
        void shouldMergeArraysUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> arraysTestPayload() {
            String baseContent = """
                arr: [ "aa", "b", "c", "d", "e" ]
                """;
            String patchContent = """
                arr: [ "A", "b", "CCC", "d" ]
                """;
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asString).containsExactly("A", "b", "CCC", "d");
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asString).containsExactly("A", "b", "CCC", "d");
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9, arr.length);
                        assertThat(arr).extracting(JsonValue::asString).containsExactly("A", "b", "CCC", "d", "aa", "b", "c", "d", "e");
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9, arr.length);
                        assertThat(arr).extracting(JsonValue::asString).containsExactly("aa", "b", "c", "d", "e", "A", "b", "CCC", "d");
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(5, arr.length);
                        assertThat(arr).extracting(JsonValue::asString).containsExactly("Aa", "b", "CCC", "d", "e");
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asString).containsExactly("Aa", "b", "CCC", "d");
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("arr"));
                    })
            );
        }
    }

    @Nested
    @DisplayName("Merge Tests - Integer")
    class IntegerMergeTests {

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("testPayload")
        @DisplayName("Should merge using strategy")
        void shouldMergeUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> testPayload() {
            String baseContent = """
                key: 1
                """;
            String patchContent = """
                key: 2
                """;
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2, json.get("key").asInteger());
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2, json.get("key").asInteger());
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(3, json.get("key").asInteger());
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(3, json.get("key").asInteger());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2, json.get("key").asInteger());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2, json.get("key").asInteger());
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("key"));
                    })
            );
        }

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("arraysTestPayload")
        @DisplayName("Should merge arrays using strategy")
        void shouldMergeArraysUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> arraysTestPayload() {
            String baseContent  = "arr: [ 1, 2, 3, 4, 5 ]";
            String patchContent = "arr: [ 10, 2, 12, 4 ]";
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asInteger).containsExactly(10, 2, 12, 4);
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asInteger).containsExactly(10, 2, 12, 4);
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9, arr.length);
                        assertThat(arr).extracting(JsonValue::asInteger).containsExactly(10, 2, 12, 4, 1, 2, 3, 4, 5);
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9, arr.length);
                        assertThat(arr).extracting(JsonValue::asInteger).containsExactly(1, 2, 3, 4, 5, 10, 2, 12, 4);
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(5, arr.length);
                        assertThat(arr).extracting(JsonValue::asInteger).containsExactly(10, 2, 12, 4, 5);
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asInteger).containsExactly(10, 2, 12, 4);
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("arr"));
                    })
            );
        }
    }

    @Nested
    @DisplayName("Merge Tests - Double")
    class DoubleMergeTests {

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("testPayload")
        @DisplayName("Should merge using strategy")
        void shouldMergeUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> testPayload() {
            String baseContent = """
                key: 1.0
                """;
            String patchContent = """
                key: 2.0
                """;
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2D, json.get("key").asDouble());
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2D, json.get("key").asDouble());
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(3D, json.get("key").asDouble());
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(3D, json.get("key").asDouble());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2D, json.get("key").asDouble());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(2D, json.get("key").asDouble());
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("key"));
                    })
            );
        }

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("arraysTestPayload")
        @DisplayName("Should merge arrays using strategy")
        void shouldMergeArraysUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> arraysTestPayload() {
            String baseContent  = "arr: [ 1.0, 2.0, 3.0, 4.0, 5.0 ]";
            String patchContent = "arr: [ 10.0, 2.0, 12.0, 4.0 ]";
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4D, arr.length);
                        assertThat(arr).extracting(JsonValue::asDouble).containsExactly(10D, 2D, 12D, 4D);
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4D, arr.length);
                        assertThat(arr).extracting(JsonValue::asDouble).containsExactly(10D, 2D, 12D, 4D);
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9D, arr.length);
                        assertThat(arr).extracting(JsonValue::asDouble).containsExactly(10D, 2D, 12D, 4D, 1D, 2D, 3D, 4D, 5D);
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9D, arr.length);
                        assertThat(arr).extracting(JsonValue::asDouble).containsExactly(1D, 2D, 3D, 4D, 5D, 10D, 2D, 12D, 4D);
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(5D, arr.length);
                        assertThat(arr).extracting(JsonValue::asDouble).containsExactly(10D, 2D, 12D, 4D, 5D);
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4D, arr.length);
                        assertThat(arr).extracting(JsonValue::asDouble).containsExactly(10D, 2D, 12D, 4D);
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("arr"));
                    })
            );
        }
    }

    @Nested
    @DisplayName("Merge Tests - Boolean")
    class BooleanMergeTests {

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("testPayload")
        @DisplayName("Should merge using strategy")
        void shouldMergeUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> testPayload() {
            String baseContent = """
                key: false
                """;
            String patchContent = """
                key: true
                """;
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(true, json.get("key").asBoolean());
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(true, json.get("key").asBoolean());
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(true, json.get("key").asBoolean());
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(true, json.get("key").asBoolean());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(false, json.get("key").asBoolean());
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertEquals(false, json.get("key").asBoolean());
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("key"));
                    })
            );
        }

        @ParameterizedTest(name = "{0}: {1} with {2}")
        @MethodSource("arraysTestPayload")
        @DisplayName("Should merge arrays using strategy")
        void shouldMergeArraysUsingStrategy(MergeStrategy mergeStrategy, String baseContent, String patchContent, Consumer<Json> verifier) throws JsonParseException {
            Json base = new JsonParser().parse(baseContent);
            Json patch = new JsonParser().parse(getPatchContent(mergeStrategy, patchContent));

            Json result = JsonMerger.merge(base, patch);
            verifier.accept(result);
        }

        private static Stream<Arguments> arraysTestPayload() {
            String baseContent  = "arr: [ false, false, true, true, false ]";
            String patchContent = "arr: [ false, true, false, true ]";
            return Stream.of(
                    Arguments.of(MergeStrategy.UNDEFINED, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asBoolean).containsExactly(false, true, false, true);
                    }),
                    Arguments.of(MergeStrategy.REPLACE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asBoolean).containsExactly(false, true, false, true);
                    }),
                    Arguments.of(MergeStrategy.PREPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9, arr.length);
                        assertThat(arr).extracting(JsonValue::asBoolean).containsExactly(false, true, false, true, false, false, true, true, false);
                    }),
                    Arguments.of(MergeStrategy.APPEND, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(9, arr.length);
                        assertThat(arr).extracting(JsonValue::asBoolean).containsExactly(false, false, true, true, false, false, true, false, true);
                    }),
                    Arguments.of(MergeStrategy.OVERLAY, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(5, arr.length);
                        assertThat(arr).extracting(JsonValue::asBoolean).containsExactly(false, false, false, true, false);
                    }),
                    Arguments.of(MergeStrategy.OVERLAY_TRUNCATE, baseContent, patchContent, (Consumer<Json>) json -> {
                        JsonValue[] arr = json.get("arr").asArray();
                        assertEquals(4, arr.length);
                        assertThat(arr).extracting(JsonValue::asBoolean).containsExactly(false, false, false, true);
                    }),
                    Arguments.of(MergeStrategy.DELETE, baseContent, patchContent, (Consumer<Json>) json -> {
                        assertNull(json.get("arr"));
                    })
            );
        }
    }

    @Nested
    @DisplayName("Merge Tests - Custom")
    class CustomTests {

        @Test
        @DisplayName("Should deep-merge nested object and append integers")
        void shouldDeepMergeNestedObject() throws JsonParseException {
            Json base = new JsonParser().parse("""
                unit: {
                    hp: 100,
                    speed: 5,
                    name: "archer",
                    stats: {
                        deaths: 0,
                        kills: 5
                    }
                }
            """);
            Json patch = new JsonParser().parse("""
                unit: {
                    >hp: 20
                    stats: {
                        deaths: 1,
                        >kills: 3
                    }
                }
            """);

            Json result = JsonMerger.merge(base, patch);

            Json unit = result.get("unit").asJson();
            assertEquals(120, unit.get("hp").asInteger());
            assertEquals(5, unit.get("speed").asInteger());
            assertEquals("archer", unit.get("name").asString());

            Json stats = unit.get("stats").asJson();
            assertEquals(1, stats.get("deaths").asInteger());
            assertEquals(8, stats.get("kills").asInteger());
        }

        @Test
        @DisplayName("Should deep-merge nested and prepend string value")
        void shouldDeepMergeNestedObjectWhenUsingOverlay() throws JsonParseException {
            Json base = new JsonParser().parse("""
                unit: {
                    hp: 100,
                    speed: 5,
                    name: "archer"
                }
            """);
            Json patch = new JsonParser().parse("""
                unit: {
                    hp: 120,
                    <name: "huge "
                }
            """);

            Json result = JsonMerger.merge(base, patch);

            Json unit = result.get("unit").asJson();
            assertEquals(120, unit.get("hp").asInteger());
            assertEquals(5, unit.get("speed").asInteger());
            assertEquals("huge archer", unit.get("name").asString());
        }

        @Test
        @DisplayName("Should overlay arrays by index")
        void shouldOverlayArraysByIndex() throws JsonParseException {
            Json base = new JsonParser().parse("{ key: [1,2,3,4,5] } ");
            Json patch = new JsonParser().parse("{ #key: [ #, #, 0] } ");

            Json result = JsonMerger.merge(base, patch);
            List<JsonValue> key = result.get("key").asList();
            assertEquals(5, key.size());
            assertEquals(1, key.get(0).asInteger());
            assertEquals(2, key.get(1).asInteger());
            assertEquals(0, key.get(2).asInteger());
            assertEquals(4, key.get(3).asInteger());
            assertEquals(5, key.get(4).asInteger());
        }

        @Test
        @DisplayName("Should overlay arrays by index when using '#' prefix and keep placeholder when extending")
        void shouldOverlayArraysByIndexWhenUsingOverlayPrefixWhenExtending() throws JsonParseException {
            Json base = new JsonParser().parse("{ key: [ 1, 2, 3 ] } ");
            Json patch = new JsonParser().parse("{ #key: [ #, 22, #, 44, 5 ] } ");

            Json result = JsonMerger.merge(base, patch);
            List<JsonValue> key = result.get("key").asList();
            assertEquals(5, key.size());
            assertEquals(1, key.get(0).asInteger());
            assertEquals(22, key.get(1).asInteger());
            assertEquals(3, key.get(2).asInteger());
            assertEquals(44, key.get(3).asInteger());
            assertEquals(5, key.get(4).asInteger());
        }

        @Test
        @DisplayName("Should overlay arrays by index and truncate")
        void shouldOverlayArraysByIndexAndTruncate() throws JsonParseException {
            Json base = new JsonParser().parse("{ key: [1,2,3,4,5] } ");
            Json patch = new JsonParser().parse("{ ##key: [#, #, 0] } ");

            Json result = JsonMerger.merge(base, patch);
            List<JsonValue> key = result.get("key").asList();
            assertEquals(3, key.size());
            assertEquals(1, key.get(0).asInteger());
            assertEquals(2, key.get(1).asInteger());
            assertEquals(0, key.get(2).asInteger());
        }

        @Test
        @DisplayName("Should merge nested arrays inside arrays during overlay")
        void shouldMergeNestedArraysInsideArraysDuringOverlay() throws JsonParseException {
            Json base = new JsonParser().parse("{ grid: [ [1,2,3], [4,5,6] ] } ");
            Json patch = new JsonParser().parse("{ #grid: [ [#, 0], # ] } ");

            Json result = JsonMerger.merge(base, patch);

            List<JsonValue> grid = result.get("grid").asList();
            assertEquals(2, grid.size());

            List<JsonValue> row0 = grid.get(0).asList();
            assertEquals(3, row0.size());
            assertEquals(1, row0.get(0).asInteger());
            assertEquals(0, row0.get(1).asInteger());
            assertEquals(3, row0.get(2).asInteger());

            List<JsonValue> row1 = grid.get(1).asList();
            assertEquals(3, row1.size());
            assertEquals(4, row1.get(0).asInteger());
            assertEquals(5, row1.get(1).asInteger());
            assertEquals(6, row1.get(2).asInteger());
        }

        @Test
        @DisplayName("Should support mixed merge strategies in a nested configuration")
        void shouldSupportMixedMergeStrategiesInNestedConfiguration() throws IOException, JsonParseException {
            Json base = new JsonParser().parse("""
                {
                    settings: {
                        name: "City",
                        enabled: true,
                        limits: [10, 20, 30],
                        tags: ["a", "b"]
                    }
                }
            """);;

            Json patch = new JsonParser().parse("""
                {
                    #settings: {
                        enabled: false,
                        #limits: [#, 999],
                        >tags: ["c"]
                    }
                }
            """);;

            Json result = JsonMerger.merge(base, patch);

            Json settings = result.get("settings").asJson();
            assertEquals("City", settings.get("name").asString());
            assertFalse(settings.get("enabled").asBoolean());

            List<JsonValue> limits = settings.get("limits").asList();
            assertEquals(3, limits.size());
            assertEquals(10, limits.get(0).asInteger());
            assertEquals(999, limits.get(1).asInteger());
            assertEquals(30, limits.get(2).asInteger());

            List<JsonValue> tags = settings.get("tags").asList();
            assertEquals(3, tags.size());
            assertEquals("a", tags.get(0).asString());
            assertEquals("b", tags.get(1).asString());
            assertEquals("c", tags.get(2).asString());
        }
    }

    private static String getPatchContent(MergeStrategy mergeStrategy, String patchContent) {
        return switch (mergeStrategy) {
            case UNDEFINED -> patchContent;
            case OVERLAY_TRUNCATE -> "" + mergeStrategy.ch + mergeStrategy.ch + patchContent;
            default -> mergeStrategy.ch + patchContent;
        };
    }
}
