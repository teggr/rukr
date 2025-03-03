/// usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.6.3


import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

@Command(name = "rukr",
        mixinStandardHelpOptions = true,
        version = "rukr 0.1",
        description = "rukr made with jbang",
        subcommands = {
                rukr.InitCommand.class,
        })
class rukr implements Callable<Integer> {

    public static void main(String... args) {
        int exitCode = new CommandLine(new rukr()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Please specify a sub command...");
        return 0;
    }

    @CommandLine.Command(name = "init", description = "Initialises the project for rukr")
    static class InitCommand implements Callable<Integer> {

        @CommandLine.Option(
                names = {"-wd", "--working-dir"},
                description = "Working directory",
                defaultValue = ""
        )
        private File wd;

        @Override
        public Integer call() throws Exception {

            File userHome = new File(System.getProperty("user.home"));

            System.out.println("User home: " + userHome.getAbsolutePath());

            File rukrHome = new File(userHome, ".rukr/");
            if (!rukrHome.exists()) {
                System.out.println("Rukr home not found");
                if (!rukrHome.mkdirs()) {
                    System.err.println("Failed to create Rukr home");
                    return 1;
                } else {
                    System.out.println("Rukr home created");
                }
            }

            System.out.println("Working directory: " + wd.getAbsolutePath());

            // check for pom.xml
            File rootPom = new File(wd, "pom.xml");
            if (!rootPom.exists()) {
                System.err.println("Cannot find pom.xml. Please run this script at the root of your Maven project. Or set the working directory --working-dir / -wd");
                return 1;
            }

            // create the rukr file
            File localRukrFile = new File(wd, ".rukrrc");
            if (!localRukrFile.exists()) {
                System.out.println("Local rukr file .rukrrc not found");
                if(!localRukrFile.createNewFile()) {
                    System.err.println("Failed to create local rukr file");
                    return 1;
                } else {
                    System.out.println("Local rukr file created");
                }
            }

            String version = "1.0";

            // read the rukr file
            Properties props = new Properties();
            props.load(new FileInputStream(localRukrFile));
            if(props.containsKey("rukr.version")) {
                version = props.getProperty("rukr.version");
            } else {
                System.out.println("Rukr version not found");
                props.put("rukr.version", version);
                props.store(new FileOutputStream(localRukrFile), null);
            }

            System.out.println("Rukr Initialised @ version " + version);
            return 0;
        }

    }

}
