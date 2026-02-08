package snake2d.config.exception;

/**
 * Exception thrown when configuration errors occur.
 *
 * @author Mateusz Frydrych thewituch@gmail.com
 */
public class ConfigException extends RuntimeException {
    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
