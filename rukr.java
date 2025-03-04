/// usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS org.springframework.shell:spring-shell-dependencies:3.4.0@pom
//DEPS org.springframework.boot:spring-boot-starter:3.4.3
//DEPS org.springframework.shell:spring-shell-starter
//FILES META-INF/resources/application.properties=application.properties
package dev.rebelcraft.rukr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.AvailabilityProvider;
import org.springframework.shell.command.CommandContext;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.CommandAvailability;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.shell.command.annotation.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@CommandScan
class rukr  {

    public static void main(String... args) {
        SpringApplication.run(rukr.class, args);
    }

    @Command
    static class ExampleCommands {

        @Command(
                command = "init" ,
                description = "Initialises the project for rukr"
        )
        public void init(
                @Option(longNames = { "wd", "working-dir"}) File wd,
                CommandContext ctx
        ) {

            File userHome = new File(System.getProperty("user.home"));

            System.out.println("User home: " + userHome.getAbsolutePath());

            File rukrHome = new File(userHome, ".rukr/");
            if (!rukrHome.exists()) {
                System.out.println("Rukr home not found");
                if (!rukrHome.mkdirs()) {
                    System.err.println("Failed to create Rukr home");
                    throw new RuntimeException("Failed to create Rukr home");
                } else {
                    System.out.println("Rukr home created");
                }
            } else {
                System.out.println("Rukr home already exists");
            }

            if(wd == null) {
                System.out.println("Using default working directory"   );
                wd = new File("");
            }

            System.out.println("Working directory: " + wd.getAbsolutePath());

            // check for pom.xml
            File rootPom = new File(wd, "pom.xml");
            if (!rootPom.exists()) {
                System.err.println("Cannot find pom.xml. Please run this script at the root of your Maven project. Or set the working directory --working-dir / -wd");
                throw new RuntimeException("Cannot find pom");
            } else {
                System.out.println("Found pom");
            }

            // create the rukr file
            File localRukrFile = new File(wd, ".rukrrc");
            if (!localRukrFile.exists()) {
                System.out.println("Local rukr file .rukrrc not found");
                boolean newFile = false;
                try {
                    newFile = localRukrFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(!newFile) {
                    System.err.println("Failed to create local rukr file");
                    throw new RuntimeException("Failed to create local rukr file");
                } else {
                    System.out.println("Local rukr file created");
                }
            } else {
                System.out.println("Local rukr file already exists");
            }

            String version = "1.0";

            // read the rukr file
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(localRukrFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(props.containsKey("rukr.version")) {
                version = props.getProperty("rukr.version");
            } else {
                System.out.println("Rukr version not found");
                props.put("rukr.version", version);
                try {
                    props.store(new FileOutputStream(localRukrFile), null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // create the local rukr folder
            File localRukrFolder = new File(wd, ".rukr");
            if (!localRukrFolder.exists()) {
                System.out.println("Local rukr folder .rukr not found");
                boolean newFolder = localRukrFolder.mkdir();
                if(!newFolder) {
                    System.err.println("Failed to create local rukr folder");
                    throw new RuntimeException("Failed to create local rukr folder");
                } else {
                    System.out.println("Local rukr folder created");
                }
            } else {
                System.out.println("Local rukr folder already exists");
            }

            System.out.println("Rukr Initialised @ version " + version);

        }

    }


}
