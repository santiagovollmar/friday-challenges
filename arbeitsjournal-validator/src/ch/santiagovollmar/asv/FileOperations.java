package ch.santiagovollmar.asv;

import ch.santiagovollmar.asv.wrappers.NamePair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static ch.santiagovollmar.asv.Main.logger;

public class FileOperations {

    /**
     * Reads in a file containing a list of names. The file must be formatted as follows:
     * {@code RegEx: '(.+\t.+\r?\n)*'}
     * @param file the file to read
     * @return A list containing NamePair read from the file
     * @throws IOException If the file couldn't be read
     * @throws FileFormatException If the file isn't formatted properly
     */
    public static ArrayList<NamePair> readNamesFile(File file) throws IOException, FileFormatException {
        ArrayList<NamePair> names = new ArrayList<>();
        logger.debug("Attempting to read file " + file.getAbsolutePath());

        for (String line : Files.readAllLines(file.toPath(), Charset.forName("UTF-8"))) {
            String[] parts = line.split("\t");
            logger.trace("Parsing line: " + line);

            if (parts.length != 2) {
                throw new FileFormatException("Could not parse line: " + line);
            }

            names.add(new NamePair(parts[0], parts[1]));
        }

        logger.debug("Successfully read names from file " + file.getAbsolutePath());

        return names;
    }
}
