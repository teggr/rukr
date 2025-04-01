/// usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.6.3
//DEPS de.codeshelf.consoleui:consoleui:0.0.13
package dev.rebelcraft.rukr;

import picocli.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "rukr",
        mixinStandardHelpOptions = true,
        version = rukr.VERSION,
        description = "Maven project code generator",
        subcommands = {
                InitCommand.class,
                AvailableCommand.class,
        }
)
public class rukr {

    public static final String VERSION = "0.1";

    public static void main(String... args) {
        int exitCode = new CommandLine(new rukr()).execute(args);
        System.exit(exitCode);
    }

}

@CommandLine.Command(
        name = "init",
        description = "Initialises the project for rukr"
)
class InitCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {

        RukrEnvironment environment =  RukrEnvironment.load();
        environment.initialise();
        return 0;

    }

}

@CommandLine.Command(
        name = "available",
        description = "List available actions"
)
class AvailableCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {

        RukrEnvironment environment =  RukrEnvironment.load();
        environment.checkInitialised();
        System.out.println("Available actions: TBC");
        return 0;

    }

}

class RukrEnvironment {

    public static RukrEnvironment load() {

        File wd = new File("").getAbsoluteFile();

        System.out.println("Working directory: " + wd);

        return new RukrEnvironment(wd);

    }

    private File wd;
    private File localRukrFolder;
    private File rukrPropertiesFile;
    private Properties props = new Properties();

    private RukrEnvironment(File wd) {
        this.wd = wd;
        this.localRukrFolder = new File(wd, ".rukr");
        this.rukrPropertiesFile = new File(localRukrFolder, "rukr.properties");
    }

    public void checkInitialised() {

        if (!localRukrFolder.exists()) {
            throw new RuntimeException("rukr has not been initialised. Run the init command first.");
        }

        if (!rukrPropertiesFile.exists()) {
            throw new RuntimeException("rukr has not been initialised. Run the init command first.");
        }

    }

    public void initialise() {

        // pre rukr checks
        checkPom();

        // create the local rukr folder
        if (!localRukrFolder.exists()) {
            System.out.println("Local rukr folder .rukr not found");
            boolean newFolder = localRukrFolder.mkdir();
            if (!newFolder) {
                System.err.println("Failed to create local rukr folder");
                throw new RuntimeException("Failed to create local rukr folder");
            } else {
                System.out.println("Local rukr folder created");
            }
        } else {
            System.out.println("Local rukr folder already exists");
        }

        // create the rukr file
        if (!rukrPropertiesFile.exists()) {
            System.out.println("rukr properties file not found");
            boolean newFile = false;
            try {
                newFile = rukrPropertiesFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!newFile) {
                System.err.println("Failed to create rukr properties file");
                throw new RuntimeException("Failed to create rukr properties file");
            } else {
                System.out.println("rukr properties file created");
            }
        } else {
            System.out.println("rukr properties file already exists");
        }

        String version = rukr.VERSION;

        // read the rukr file
        try {
            props.load(new FileInputStream(rukrPropertiesFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (props.containsKey("rukrVersion")) {
            version = props.getProperty("rukrVersion");
        } else {
            System.out.println("Rukr version not found");
            props.put("rukrVersion", version);
            try {
                props.store(new FileOutputStream(rukrPropertiesFile), null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Rukr Initialised @ version " + version);
    }

    private void checkPom() {
        // check for pom.xml
        File rootPom = new File(wd, "pom.xml");
        if (!rootPom.exists()) {
            System.err.println("Cannot find pom.xml. Please run this script at the root of your Maven project. Or set the working directory --working-dir / -wd");
            throw new RuntimeException("Cannot find pom");
        } else {
            System.out.println("Found pom");
        }
    }

}
