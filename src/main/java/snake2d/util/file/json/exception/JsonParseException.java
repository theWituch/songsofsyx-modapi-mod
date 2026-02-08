package snake2d.util.file.json.exception;

/**
 * Exception thrown during JSON parsing.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class JsonParseException extends Exception {
    private int line;
    private int column;

    public JsonParseException(String message, int line, int column) {
        super(String.format("%s (line %d, column %d)", message, line, column));
        this.line = line;
        this.column = column;
    }

    public JsonParseException(String message, int line, int column, Throwable cause) {
        super(String.format("%s (line %d, column %d)", message, line, column), cause);
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
