package snake2d.util.file.json;

import snake2d.util.file.json.exception.JsonParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON file parser. Reads the file character by character and is tolerant
 * to formatting errors.
 * Validates file consistency with regard to brackets, colons, etc.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class JsonParser {
    private String content;
    private int position;
    private int line;
    private int column;

    /**
     * Parses a JSON file from the given path.
     */
    public Json parse(Path path) throws IOException, JsonParseException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return parseString(sb.toString());
    }

    /**
     * Parses a JSON file from a File object.
     */
    public Json parse(File file) throws IOException, JsonParseException {
        return parse(file.toPath());
    }

    /**
     * Parses a JSON file from the provided content.
     */
    public Json parse(String content) throws JsonParseException {
        return parseString(content);
    }

    /**
     * Parses JSON from a string.
     */
    private Json parseString(String jsonString) throws JsonParseException {
        this.content = jsonString;
        this.position = 0;
        this.line = 1;
        this.column = 1;

        skipWhitespace();

        // Check if the file starts with '{'
        boolean hasRootBracket = false;
        if (peek() == '{') {
            consume();
            hasRootBracket = true;
            skipWhitespace();
        }

        Json json = parseObject(hasRootBracket);

        skipWhitespace();

        // If there was an opening bracket at the beginning, a closing one is required
        if (hasRootBracket) {
            if (peek() != '}') {
                throw new JsonParseException("Expected '}' at the end of the file", line, column);
            }
            consume();
        }

        skipWhitespace();

        // Check for any extra characters
        if (position < content.length()) {
            throw new JsonParseException("Unexpected characters after parsing finished", line, column);
        }

        return json;
    }

    /**
     * Parses a JSON object (a set of key-value pairs).
     */
    private Json parseObject(boolean insideBrackets) throws JsonParseException {
        Json json = new Json();

        while (position < content.length()) {
            skipWhitespace();

            // Check for end of object
            char c = peek();
            if (c == '}') {
                if (insideBrackets) {
                    return json; // Do not consume '}', leave it for the caller
                } else {
                    throw new JsonParseException("Unexpected character '}'", line, column);
                }
            }

            if (c == '\0') {
                break; // End of file
            }

            // Parse key
            JsonKey key = parseKey();
            skipWhitespace();

            // Expect colon
            if (peek() != ':') {
                throw new JsonParseException("Expected ':' after key '" + key.getKey() + "'", line, column);
            }
            consume(); // Consume ':'
            skipWhitespace();

            // Parse value
            JsonValue value = parseValue();
            json.put(key, value);

            skipWhitespace();

            // Check for comma or end of object
            c = peek();
            if (c == ',') {
                consume();
                skipWhitespace();
                // Check if '}' immediately follows the comma
                if (peek() == '}') {
                    if (insideBrackets) {
                        return json;
                    }
                }
            } else if (c == '}' || c == '\0') {
                // End of object or file
                break;
            }
            // Missing comma is also acceptable - continue parsing
        }

        return json;
    }

    /**
     * Parses a JSON key.
     */
    private JsonKey parseKey() throws JsonParseException {
        skipWhitespace();

        try {
            char c = peek();
            if (c == '"') {
                // Quoted key
                return new JsonKey(parseStringLiteral());
            } else if (Character.isDefined(c)) {
                // Unquoted key
                StringBuilder key = new StringBuilder();
                while (position < content.length()) {
                    c = peek();
                    if (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.') {
                        key.append(consume());
                    } else {
                        break;
                    }
                }
                return new JsonKey(key.toString());
            } else {
                throw new JsonParseException("Expected key", line, column);
            }
        } catch (Exception ex) {
            throw new JsonParseException("Error while building key", line, column, ex);
        }
    }

    /**
     * Parses a JSON value.
     */
    private JsonValue parseValue() throws JsonParseException {
        skipWhitespace();

        try {
            char c = peek();
            if (c == '"') {
                // String
                return new JsonValue(parseStringLiteral());
            } else if (c == '{') {
                // Nested object
                consume(); // Consume '{'
                skipWhitespace();
                Json nestedJson = parseObject(true);
                skipWhitespace();
                if (peek() != '}') {
                    throw new JsonParseException("Expected '}' at the end of object", line, column);
                }
                consume(); // Consume '}'
                return new JsonValue(nestedJson);
            } else if (c == '[') {
                // Array
                return new JsonValue(parseArray());
            } else if (c == 't' || c == 'f') {
                // Boolean
                return new JsonValue(parseBoolean());
            } else if (c == 'n') {
                // null
                parseNull();
                return new JsonValue(null);
            } else if (c == '-' || Character.isDigit(c)) {
                // Number
                return parseNumber();
            } else {
                throw new JsonParseException("Unexpected character: '" + c + "'", line, column);
            }
        } catch (Exception ex) {
            throw new JsonParseException("Error while building value", line, column, ex);
        }
    }

    /**
     * Parses a quoted string.
     */
    private String parseStringLiteral() throws JsonParseException {
        if (peek() != '"') {
            throw new JsonParseException("Expected '\"'", line, column);
        }
        consume(); // Consume opening "

        StringBuilder sb = new StringBuilder();
        while (position < content.length()) {
            char c = peek();
            if (c == '"') {
                consume(); // Consume closing "
                return sb.toString();
            } else if (c == '\\') {
                consume();
                if (position >= content.length()) {
                    throw new JsonParseException("Unexpected end of file inside string", line, column);
                }
                char escaped = consume();
                switch (escaped) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case '\\': sb.append('\\'); break;
                    case '"': sb.append('"'); break;
                    default: sb.append(escaped);
                }
            } else {
                sb.append(consume());
            }
        }

        throw new JsonParseException("Unterminated string", line, column);
    }

    /**
     * Parses an array.
     */
    private List<JsonValue> parseArray() throws JsonParseException {
        if (peek() != '[') {
            throw new JsonParseException("Expected '['", line, column);
        }
        consume(); // Consume '['

        List<JsonValue> array = new ArrayList<>();
        skipWhitespace();

        if (peek() == ']') {
            consume();
            return array; // Empty array
        }

        while (position < content.length()) {
            skipWhitespace();
            array.add(parseValue());
            skipWhitespace();

            char c = peek();
            if (c == ',') {
                consume();
                skipWhitespace();
                // Check if ']' immediately follows the comma
                if (peek() == ']') {
                    consume();
                    return array;
                }
            } else if (c == ']') {
                consume();
                return array;
            } else {
                // Missing comma is also acceptable - continue
            }
        }

        throw new JsonParseException("Unterminated array", line, column);
    }

    /**
     * Parses a number (int or double).
     */
    private JsonValue parseNumber() throws JsonParseException {
        StringBuilder number = new StringBuilder();
        boolean isDouble = false;

        // Minus
        if (peek() == '-') {
            number.append(consume());
        }

        // Digits before decimal point
        if (!Character.isDigit(peek())) {
            throw new JsonParseException("Expected digit", line, column);
        }

        while (position < content.length() && Character.isDigit(peek())) {
            number.append(consume());
        }

        // Decimal point
        if (position < content.length() && peek() == '.') {
            isDouble = true;
            number.append(consume());

            if (!Character.isDigit(peek())) {
                throw new JsonParseException("Expected digit after decimal point", line, column);
            }

            while (position < content.length() && Character.isDigit(peek())) {
                number.append(consume());
            }
        }

        // Exponent (e/E)
        if (position < content.length() && (peek() == 'e' || peek() == 'E')) {
            isDouble = true;
            number.append(consume());

            if (peek() == '+' || peek() == '-') {
                number.append(consume());
            }

            if (!Character.isDigit(peek())) {
                throw new JsonParseException("Expected digit in exponent", line, column);
            }

            while (position < content.length() && Character.isDigit(peek())) {
                number.append(consume());
            }
        }

        try {
            if (isDouble) {
                return new JsonValue(Double.parseDouble(number.toString()));
            } else {
                return new JsonValue(Integer.parseInt(number.toString()));
            }
        } catch (NumberFormatException ex) {
            throw new JsonParseException("Invalid number format: " + number, line, column, ex);
        }
    }

    /**
     * Parses a boolean value.
     */
    private boolean parseBoolean() throws JsonParseException {
        if (peek() == 't') {
            expectString("true");
            return true;
        } else if (peek() == 'f') {
            expectString("false");
            return false;
        } else {
            throw new JsonParseException("Expected 'true' or 'false'", line, column);
        }
    }

    /**
     * Parses null.
     */
    private void parseNull() throws JsonParseException {
        expectString("null");
    }

    /**
     * Expects a specific string.
     */
    private void expectString(String expected) throws JsonParseException {
        for (int i = 0; i < expected.length(); i++) {
            if (position >= content.length() || consume() != expected.charAt(i)) {
                throw new JsonParseException("Expected '" + expected + "'", line, column);
            }
        }
    }

    /**
     * Skips whitespace characters.
     */
    private void skipWhitespace() {
        while (position < content.length()) {
            char c = content.charAt(position);
            if (Character.isWhitespace(c)) {
                consume();
            } else {
                break;
            }
        }
    }

    /**
     * Returns the current character without advancing the position.
     */
    private char peek() {
        if (position >= content.length()) {
            return '\0';
        }
        return content.charAt(position);
    }

    /**
     * Consumes the current character and advances the position.
     */
    private char consume() {
        if (position >= content.length()) {
            return '\0';
        }
        char c = content.charAt(position);
        position++;

        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }

        return c;
    }


}
