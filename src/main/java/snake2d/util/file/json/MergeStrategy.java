package snake2d.util.file.json;

public enum MergeStrategy {
    UNDEFINED('\0'),
    REPLACE('='),
    PREPEND('<'),
    APPEND('>'),
    OVERLAY('#'),
    OVERLAY_TRUNCATE('#'),
    DELETE('!');

    public final char ch;

    MergeStrategy(char ch) {
        this.ch = ch;
    }
}