package ch.santiagovollmar.asv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static ch.santiagovollmar.asv.Main.logger;

/**
 * This class is responsible for handling the configurations of this program
 */
public class PropertyManager {
    private static Properties properties;

    /**
     * Loads the required properties from a list of possible locations.
     * If no property file can be located, the system will exit.
     * Possible locations:
     * <ul>
     *     <li><code>.\arbeitsjournal-validator.properties</code></li>
     *     <li><code>%APPDATA%\arbeitsjournal-validator\config.properties</code></li>
     *     <li><code>/etc/arbeitsjournal-validator.properties</code></li>
     * </ul>
     * @throws IllegalStateException If the properties have already been loaded before
     */
    public static void readProperties() throws IllegalStateException {
        logger.debug("Attempting to read properties");

        // check if properties have already been loaded
        if (properties != null)
            throw new IllegalStateException("Properties are already loaded");

        // locate file
        File[] locations = {
                new File((System.getenv("APPDATA") + "\\arbeitsjournal-validator\\config.properties")),
                new File(".\\arbeitsjournal-validator.properties"),
                new File("/etc/arbeitsjournal-validator.properties")
        };

        logger.debug("Checking locations: " + Arrays.toString(locations));
        File file = null;
        for (File location : locations) {
            logger.trace("Checking location: " + location.getAbsolutePath());
            if (location.exists() && location.isFile()) {
                file = location;
                logger.debug("Valid location found: " + location.getAbsolutePath());
                break;
            }
        }

        // read properties
        properties = new Properties();
        try {
            logger.debug("Reading file " + file.getAbsolutePath());
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            logger.fatal("Property file could not be opened", e);
            logger.debug("abort");
            System.exit(-1);
        } catch (NullPointerException e) {
            logger.fatal("No property file was found");
            logger.debug("abort");
            System.exit(-1);
        }
    }

    /**
     * Checks if required properties were loaded by invoking {@link #readProperties()}
     * Required properties:
     * <ul>
     *     <li><code>arbeitsjournale.location</code></li>
     *     <li><code>namefile.location</code></li>
     * </ul>
     * @return A boolean stating if the properties read are valid
     * @throws IllegalStateException If the properties haven't been read by {@link #readProperties()} before calling this function
     */
    public static boolean validateProperties() throws IllegalStateException {
        logger.debug("Validating properties");

        if (properties == null)
            throw new IllegalStateException("No properties loaded");

        String[] requiredProperties = {
                "root-folder",
                "namefile",
                "folder-format",
                "document-format"
        };
        logger.debug("Checking properties: " + Arrays.toString(requiredProperties));

        boolean valid = true;

        for (String property : requiredProperties) {
            valid = properties.getProperty(property) != null && valid;
            logger.trace("Checked property: " + property + " valid: " + valid);
        }

        return valid;
    }

    /**
     * Searches for the given property and returns it's value or {@code null} if it could not be found
     * Following properties are guaranteed to exist after invoking {@link #validateProperties()}:
     * <ul>
     *     <li><code>arbeitsjournale.location</code></li>
     *     <li><code>namefile.location</code></li>
     * </ul>
     * @param key The name of the property to search for
     * @return The value of the property or {@code null} if the property could not be found
     * @throws IllegalStateException If the properties weren't loaded by invoking {@link #readProperties()} before calling this function
     */
    public static String getProperty(String key) throws IllegalStateException {
        if (properties == null)
            throw new IllegalStateException("No properties loaded");

        return properties.getProperty(key);
    }

    /**
     * Searches for the given property and returns it's value or {@code defaultValue} if it could not be found
     * Following properties are guaranteed to exist after invoking {@link #validateProperties()}:
     * <ul>
     *     <li><code>arbeitsjournale.location</code></li>
     *     <li><code>namefile.location</code></li>
     * </ul>
     * @param key The name of the property to search for
     * @param defaultValue The value to return if the property couldn't be found
     * @return The value of the property or {@code defaultValue} if the property could not be found
     * @throws IllegalStateException If the properties weren't loaded by invoking {@link #readProperties()} before calling this function
     */
    public static String getProperty(String key, String defaultValue) {
        if (properties == null)
            throw new IllegalStateException("No properties loaded");

        return properties.getProperty(key, defaultValue);
    }

    // prevent instantiation
    private PropertyManager() { }
}
