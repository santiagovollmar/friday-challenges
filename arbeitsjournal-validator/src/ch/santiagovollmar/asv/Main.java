package ch.santiagovollmar.asv;

import ch.santiagovollmar.asv.wrappers.DocumentEntry;
import ch.santiagovollmar.asv.wrappers.NamePair;
import org.apache.logging.log4j.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static final Logger logger = LogManager.getLogger("application-results");

    public static void main(String[] args) {
        // read properties
        logger.debug("Reading properties");
        PropertyManager.readProperties();
        if (!PropertyManager.validateProperties()) {
            logger.fatal("Property file is incomplete");
            logger.debug("abort");
            System.exit(-1);
        }

        // read names
        logger.debug("Reading names");
        ArrayList<NamePair> names = null;
        try {
            names = FileOperations.readNamesFile(new File(PropertyManager.getProperty("namefile")));
        } catch (IOException | FileFormatException e) {
            logger.fatal("Namefile could not be read", e);
            logger.debug("abort");
            System.exit(-1);
        }

        // create formatters
        final Formatter fileFormatter;
        final Formatter folderFormatter;
        {
            Formatter tempFileFormatter = null;
            Formatter tempFolderFormatter = null;
            try {
                tempFileFormatter = new Formatter(PropertyManager.getProperty("document-format"));
                tempFolderFormatter = new Formatter(PropertyManager.getProperty("folder-format"));
            } catch (IllegalArgumentException e) {
                logger.fatal("");
            }

            fileFormatter = tempFileFormatter;
            folderFormatter = tempFolderFormatter;
        }
        // check directories
        logger.debug("Checking directories");
        List<NamePair> namesWithFolders = null;
        try {
            String folderFormatRegex = folderFormatter.format(null, null, null);
            Set<String> dirNames = Arrays.stream(new File(PropertyManager.getProperty("root-folder")).list())
                    .filter(dirName -> dirName.matches(folderFormatRegex))
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            namesWithFolders = names.stream().filter(name -> {
                if (dirNames.contains(folderFormatter.format(name, null, null))) {
                    logger.debug("Identified folder of " + name.prename + ' ' + name.surname);
                    return true;
                } else {
                    logger.warn("Could not find folder for " + name.prename + ' ' + name.surname);
                    return false;
                }
            }).collect(Collectors.toList());
        } catch (NullPointerException e) {
            logger.fatal("Property: root-folder specifies a file but should specify a folder");
            logger.debug("abort");
            System.exit(-1);
        }

        // add files to file map
        HashMap<NamePair, ArrayList<DocumentEntry>> fileMap = new HashMap<>();
        String fileFormatRegex = fileFormatter.format(null, null, null);

        for (NamePair name : namesWithFolders) {
            File folder = new File(PropertyManager.getProperty("root-folder") + File.pathSeparator + folderFormatter.format(name, null, null));
            try {
                fileMap.put(name, new ArrayList<>(Arrays.stream(folder.listFiles())
                        .filter(file -> file.getName().matches(fileFormatRegex))
                        .map(file -> {
                            return new DocumentEntry(name, file, fileFormatter.getWeek(file.getName()), fileFormatter.getYear(file.getName())); // TODO extract week and year from filename
                        })
                        .sorted((e1, e2) -> {
                            if (e1.getYear() > e2.getYear()) {
                                return 1;
                            } else if (e2.getYear() > e1.getYear()) {
                                return -1;
                            } else if (e1.getWeek() > e2.getWeek()) {
                                return 1;
                            } else if (e2.getWeek() > e1.getWeek()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        })
                        .collect(Collectors.toList())));
            } catch (NullPointerException e) { // "folder" is not a folder
                logger.warn("Could not find folder for " + name.prename + " " + name.surname + " but there is a file with the right name");
            }
        }

        // check integrity of the files
        fileMap.forEach((key, value) -> {

        });
    }
}
