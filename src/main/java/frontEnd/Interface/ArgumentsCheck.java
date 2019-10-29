package frontEnd.Interface;

import frontEnd.Interface.outputRouting.ExceptionHandler;
import frontEnd.Interface.outputRouting.ExceptionId;
import frontEnd.Interface.outputRouting.parcelHandling;
import frontEnd.MessagingSystem.routing.EnvironmentInformation;
import frontEnd.MessagingSystem.routing.Listing;
import frontEnd.argsIdentifier;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import rule.engine.EngineType;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>ArgumentsCheck class.</p>
 *
 * @author CryptoguardTeam
 * Created on 12/13/18.
 * @version 03.07.01
 * @since 01.01.02
 *
 * <p>The main check for the Arguments</p>
 */
@Log4j2
public class ArgumentsCheck {

    /**
     * The fail fast parameter Check method
     * <p>This method will attempt to create the Environment Information and provide help if the usage doesn't match</p>
     *
     * @param args {@link java.lang.String} - the raw arguments passed into the console
     * @return {@link frontEnd.MessagingSystem.routing.EnvironmentInformation} - when not null, the general Information is created for usage within any output structure.
     * @throws frontEnd.Interface.outputRouting.ExceptionHandler if any.
     */
    public static EnvironmentInformation paramaterCheck(List<String> args) throws ExceptionHandler {

        //region CLI Section

        Options cmdLineArgs = setOptions();
        CommandLine cmd = null;

        //region Printing Version
        if (args.contains(argsIdentifier.HELP.getArg())) {
            log.trace("Retrieving the help as requested.");
            throw new ExceptionHandler(parcelHandling.retrieveHelpFromOptions(cmdLineArgs, null), ExceptionId.HELP);
        }

        if (args.contains(argsIdentifier.VERSION.getArg())) {
            log.trace("Retrieving the version as requested.");
            throw new ExceptionHandler(parcelHandling.retrieveHeaderInfo(), ExceptionId.VERSION);
        }
        //endregion

        log.trace("Starting the parsing of arguments.");
        try {
            cmd = new DefaultParser().parse(cmdLineArgs, args.toArray(new String[0]), true);
        } catch (ParseException e) {
            log.debug("Issue with parsing the arguments: " + e.getMessage());
            String arg = null;

            if (e.getMessage().startsWith("Missing required option: "))
                arg = argsIdentifier.lookup(e.getMessage().replace("Missing required option: ", "")).getArg();
            else if (e.getMessage().startsWith("Missing required options: ")) {
                String[] argIds = e.getMessage().replace("Missing required options: ", "").replace(" ", "").split(",");
                arg = "Issue with the following argument(s) ";

                for (String argId : argIds)
                    arg += argsIdentifier.lookup(argId) + ", ";

                arg = arg.substring(0, arg.length() - ", ".length());

            }

            throw new ExceptionHandler(parcelHandling.retrieveHelpFromOptions(cmdLineArgs, arg), ExceptionId.ARG_VALID);
        }

        //endregion

        //region Cleaning retrieved values from args
        log.trace("Cleaning the extra output specific arguments.");
        ArrayList<String> upgradedArgs = new ArrayList<>(args);
        for (argsIdentifier arg : argsIdentifier.values()) {
            if (cmd.hasOption(arg.getId())) {
                upgradedArgs.remove("-" + arg.getId());
                upgradedArgs.remove(cmd.getOptionValue(arg.getId()));
            }
        }
        args = upgradedArgs;
        log.debug("Output specific arguments: " + args.toString());
        //endregion

        EngineType type = EngineType.getFromFlag(cmd.getOptionValue(argsIdentifier.FORMAT.getId()));
        log.debug("Chose the enginetype: " + type.getName());

        Boolean verify = !cmd.hasOption(argsIdentifier.SKIPINPUTVALIDATION.getId());
        log.debug("Verification flag: " + verify);

        Boolean usingInputIn = cmd.getOptionValue(argsIdentifier.SOURCE.getId()).endsWith(".in");
        log.debug("Enhanced Input in file: " + usingInputIn);

        //region Logging Verbosity Check
        if (cmd.hasOption(argsIdentifier.VERYVERBOSE.getId())) {
            Configurator.setRootLevel(Level.TRACE);
            log.info("Displaying debug level logs");
        } else if (cmd.hasOption(argsIdentifier.VERBOSE.getId())) {
            Configurator.setRootLevel(Level.DEBUG);
            log.info("Displaying debug level logs");
        } else if (cmd.hasOption(argsIdentifier.NOLOGS.getId())) {
            Configurator.setRootLevel(Level.FATAL);
            log.info("Setting the Logging to Fatal logs");
        } else {
            Configurator.setRootLevel(Level.INFO);
            log.info("Displaying info level logs");
        }
        //endregion



        //inputFiles

        //region Setting the source files
        log.trace("Retrieving the source files.");

        List<String> source;
        if (!usingInputIn)
            source = verify ? Utils.retrieveFilesByType(
                    Arrays.asList(
                            cmd.getOptionValues(argsIdentifier.SOURCE.getId())), type)
                    : Arrays.asList(
                    cmd.getOptionValues(argsIdentifier.SOURCE.getId()));
        else
            source = Utils.inputFiles(cmd.getOptionValue(argsIdentifier.SOURCE.getId()));

        log.info("Using the source file(s): " + source.toString());

        String setMainClass = null;
        if (cmd.hasOption(argsIdentifier.MAIN.getId())) {
            setMainClass = StringUtils.trimToNull(cmd.getOptionValue(argsIdentifier.MAIN.getId()));
            if (setMainClass == null)
                throw new ExceptionHandler("Please Enter a valid main class path.", ExceptionId.ARG_VALID);

            log.info("Attempting to validate the main method as " + setMainClass);

            if (!source.contains(setMainClass))
                throw new ExceptionHandler("The main class path is not included within the source file.", ExceptionId.ARG_VALID);

            log.info("Using the main method from class " + setMainClass);
        }
        //endregion

        //region Setting the dependency path
        List<String> dependencies = new ArrayList<String>();
        if (cmd.hasOption(argsIdentifier.DEPENDENCY.getId())) {
            log.trace("Retrieving the dependency files.");
            dependencies = verify ? Utils.verifyClassPaths(
                    Arrays.asList(
                            cmd.getOptionValues(argsIdentifier.DEPENDENCY.getId())))
                    : Arrays.asList(
                    cmd.getOptionValues(argsIdentifier.DEPENDENCY.getId()))
            ;
            log.info("Using the dependency file(s): " + source.toString());
        }
        //endregion

        Listing messaging = Listing.retrieveListingType(cmd.getOptionValue(argsIdentifier.FORMATOUT.getId()));
        log.info("Using the output: " + messaging.getType());

        //region Retrieving the package path
        log.trace("Retrieving the package path, may/may not be able to be replaced.");
        List<String> basePath = new ArrayList<String>();
        File sourceFile;
        String pkg = "";
        switch (type) {
            case APK:
            case JAR:
                sourceFile = new File(source.get(0));
                basePath.add(sourceFile.getName());
                pkg = sourceFile.getName();
                break;
            case DIR:
                sourceFile = new File(source.get(0));
                try {
                    basePath.add(sourceFile.getCanonicalPath() + ":dir");
                } catch (IOException e) {
                }
                pkg = sourceFile.getName();
                break;
            case JAVAFILES:
            case CLASSFILES:
                for (String file : source) {
                    try {
                        sourceFile = new File(file);
                        basePath.add(sourceFile.getCanonicalPath());

                        if (pkg == null) {
                            pkg = sourceFile.getCanonicalPath();
                        }

                    } catch (IOException e) {
                    }
                }
                break;
        }
        log.debug("Package path: " + pkg);
        //endregion

        EnvironmentInformation info = new EnvironmentInformation(source, type, messaging, dependencies, basePath, pkg);

        //region - TODO - Implement an option to specify the base package
        /*
        if (cmd.hasOption(argsIdentifier.BASEPACKAGE.getId())) {
            String basePackage = cmd.getOptionValue(argsIdentifier.BASEPACKAGE.getId());
            log.debug("Going to set the Base Package : " + basePackage);

            info.setBasePackage(Utils.verifyDir(basePackage));
            log.info("Specifying the base package as " + basePackage);
        }
        */
        //endregion

        if (setMainClass != null)
            info.setMain(setMainClass);

        //region Setting the file out
        log.trace("Determining the file out.");
        String fileOutPath = "";
        if (cmd.hasOption(argsIdentifier.OUT.getId()))
            if (verify)
                fileOutPath = Utils.verifyFileExt(cmd.getOptionValue(argsIdentifier.OUT.getId()), messaging.getOutputFileExt(), cmd.hasOption(argsIdentifier.NEW.getId()));
            else
                fileOutPath = cmd.getOptionValue(argsIdentifier.OUT.getId());
        else
            fileOutPath = Utils.osPathJoin(System.getProperty("user.dir"),
                    info.getPackageName() /*+ "_" + fileName*/ + info.getMessagingType().getOutputFileExt());

        if (cmd.hasOption(argsIdentifier.TIMESTAMP.getId())) {
            String[] tempSplit = fileOutPath.split("\\.\\w+$");
            fileOutPath = tempSplit[0] + "_" + Utils.getCurrentTimeStamp() + info.getMessagingType().getOutputFileExt();
        }
        log.debug("File out: " + fileOutPath);
        info.setFileOut(fileOutPath);


        //endregion

        if (!messaging.getTypeOfMessagingInput().inputValidation(info, args.toArray(new String[0]))) {
            log.error("Issue Validating Output Specific Arguments.");
            //TODO - Add better output message for this case
            throw new ExceptionHandler(messaging.getInputHelp(), ExceptionId.FORMAT_VALID);
        }

        //region Logging Information
        info.setPrettyPrint(cmd.hasOption(argsIdentifier.PRETTY.getId()));
        log.debug("Pretty flag: " + cmd.hasOption(argsIdentifier.PRETTY.getId()));

        info.setShowTimes(cmd.hasOption(argsIdentifier.TIMEMEASURE.getId()));
        log.debug("Time measure flag: " + cmd.hasOption(argsIdentifier.TIMEMEASURE.getId()));

        info.setStreaming(cmd.hasOption(argsIdentifier.STREAM.getId()));
        log.debug("Stream flag: " + cmd.hasOption(argsIdentifier.STREAM.getId()));

        info.setDisplayHeuristics(cmd.hasOption(argsIdentifier.HEURISTICS.getId()));
        log.debug("Heuristics flag: " + cmd.hasOption(argsIdentifier.HEURISTICS.getId()));

        Utils.initDepth(Integer.parseInt(cmd.getOptionValue(argsIdentifier.DEPTH.getId(), String.valueOf(1))));
        log.debug("Scanning using a depth of " + Utils.DEPTH);

        boolean noExitJVM = cmd.hasOption(argsIdentifier.NOEXIT.getId());
        log.debug("Exiting the JVM: " + verify);
        if (noExitJVM)
            info.setKillJVM(false);
        //endregion

        //Setting the raw command within info
        info.setRawCommand(Utils.join(" ", args));

        return info;

    }

    private static Options setOptions() {
        Options cmdLineArgs = new Options();

        Option format = Option.builder(argsIdentifier.FORMAT.getId()).required().hasArg().argName("format").desc(argsIdentifier.FORMAT.getDesc()).build();
        format.setType(String.class);
        format.setOptionalArg(false);
        cmdLineArgs.addOption(format);

        Option sources = Option.builder(argsIdentifier.SOURCE.getId()).required().hasArgs().argName("file(s)/*.in/dir").desc(argsIdentifier.SOURCE.getDesc()).build();
        sources.setType(String.class);
        sources.setValueSeparator(' ');
        sources.setOptionalArg(false);
        cmdLineArgs.addOption(sources);

        Option dependency = Option.builder(argsIdentifier.DEPENDENCY.getId()).hasArg().argName("dir").desc(argsIdentifier.DEPENDENCY.getDesc()).build();
        dependency.setType(String.class);
        dependency.setOptionalArg(false);
        cmdLineArgs.addOption(dependency);

        Option mainFile = Option.builder(argsIdentifier.MAIN.getId()).hasArg().argName("main").desc(argsIdentifier.MAIN.getDesc()).build();
        mainFile.setType(String.class);
        mainFile.setOptionalArg(true);
        cmdLineArgs.addOption(mainFile);

        //region - TODO - Implement an option to specify the base package
        /*
        Option baseProject = Option.builder(argsIdentifier.BASEPACKAGE.getId()).hasArg().argName("package").desc(argsIdentifier.BASEPACKAGE.getDesc()).build();
        baseProject.setType(String.class);
        baseProject.setOptionalArg(true);
        cmdLineArgs.addOption(baseProject);
        */
        //endregion

        Option depth = Option.builder(argsIdentifier.DEPTH.getId()).hasArg().argName("depth").desc(argsIdentifier.DEPTH.getDesc()).build();
        depth.setType(String.class);
        depth.setOptionalArg(true);
        cmdLineArgs.addOption(depth);

        Option output = Option.builder(argsIdentifier.OUT.getId()).hasArg().argName("file").desc(argsIdentifier.OUT.getDesc()).build();
        output.setType(String.class);
        output.setOptionalArg(true);
        cmdLineArgs.addOption(output);

        Option timing = new Option(argsIdentifier.TIMEMEASURE.getId(), false, argsIdentifier.TIMEMEASURE.getDesc());
        timing.setOptionalArg(true);
        cmdLineArgs.addOption(timing);

        Option formatOut = Option.builder(argsIdentifier.FORMATOUT.getId()).hasArg().argName("formatType").desc(argsIdentifier.FORMATOUT.getDesc()).build();
        formatOut.setOptionalArg(false);
        cmdLineArgs.addOption(formatOut);

        Option prettyPrint = new Option(argsIdentifier.PRETTY.getId(), false, argsIdentifier.PRETTY.getDesc());
        prettyPrint.setOptionalArg(true);
        cmdLineArgs.addOption(prettyPrint);

        Option noExit = new Option(argsIdentifier.NOEXIT.getId(), false, argsIdentifier.NOEXIT.getDesc());
        prettyPrint.setOptionalArg(true);
        cmdLineArgs.addOption(noExit);

        Option help = new Option(argsIdentifier.HELP.getId(), false, argsIdentifier.HELP.getDesc());
        help.setOptionalArg(true);
        cmdLineArgs.addOption(help);

        Option version = new Option(argsIdentifier.VERSION.getId(), false, argsIdentifier.VERSION.getDesc());
        version.setOptionalArg(true);
        cmdLineArgs.addOption(version);

        Option skipInput = new Option(argsIdentifier.SKIPINPUTVALIDATION.getId(), false, argsIdentifier.SKIPINPUTVALIDATION.getDesc());
        skipInput.setOptionalArg(true);
        cmdLineArgs.addOption(skipInput);

        Option displayHeuristcs = new Option(argsIdentifier.HEURISTICS.getId(), false, argsIdentifier.HEURISTICS.getDesc());
        displayHeuristcs.setOptionalArg(true);
        cmdLineArgs.addOption(displayHeuristcs);

        Option timeStamp = new Option(argsIdentifier.TIMESTAMP.getId(), false, argsIdentifier.TIMESTAMP.getDesc());
        skipInput.setOptionalArg(true);
        cmdLineArgs.addOption(timeStamp);

        Option stream = new Option(argsIdentifier.STREAM.getId(), false, argsIdentifier.STREAM.getDesc());
        stream.setOptionalArg(true);
        cmdLineArgs.addOption(stream);

        Option nologs = new Option(argsIdentifier.NOLOGS.getId(), false, argsIdentifier.NOLOGS.getDesc());
        stream.setOptionalArg(true);
        cmdLineArgs.addOption(nologs);

        Option verbose = new Option(argsIdentifier.VERBOSE.getId(), false, argsIdentifier.VERBOSE.getDesc());
        stream.setOptionalArg(true);
        cmdLineArgs.addOption(verbose);

        Option vverbose = new Option(argsIdentifier.VERYVERBOSE.getId(), false, argsIdentifier.VERYVERBOSE.getDesc());
        stream.setOptionalArg(true);
        cmdLineArgs.addOption(vverbose);

        log.trace("Set the command line options to be used for parsing.");
        return cmdLineArgs;
    }

}
