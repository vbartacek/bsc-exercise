package com.spoledge.bscexercise.main;

import java.io.File;
import java.io.PrintWriter;

import com.spoledge.bscexercise.impl.*;


/**
 * The entry point of the application.
 */
public class Main {

    private static class Opts {
        int period = 60;
        int decimalPoints = 2;
        boolean syncSec;
        boolean syncMin;
        boolean syncHour;

        File directory;
        File[] files;
    }


    public static void main( String[] args ) throws Exception {
        Opts opts = getOpts( args );
        if (opts == null) return;

        PrintWriter writer = new PrintWriter( System.out );

        MoneyParserImpl moneyParser = new MoneyParserImpl( opts.decimalPoints );
        SimplePaymentReporterImpl reporter = new SimplePaymentReporterImpl( writer );
        MemoryPaymentProcessorImpl paymentProcessor = new MemoryPaymentProcessorImpl();
        CommandLineControllerImpl controller = new CommandLineControllerImpl( null, writer );

        reporter.setPeriod( opts.period * 1000 );
        reporter.setPaymentProcessor( paymentProcessor );
        reporter.setSyncSec( opts.syncSec );
        reporter.setSyncMin( opts.syncMin );
        reporter.setSyncHour( opts.syncHour );

        controller.setPaymentProcessor( paymentProcessor );
        controller.setPaymentReporter( reporter );
        controller.setMoneyParser( moneyParser );

        if (opts.directory != null) controller.setDirectory( opts.directory );

        printVersion( writer );

        controller.start();

        if (opts.files != null) {
            for (File file : opts.files) {
                controller.loadFile( file );
            }
        }
    }


    public static void printVersion() {
        printVersion( new PrintWriter( System.out, true ));
    }


    public static void printVersion( PrintWriter out ) {
        Package pkg = Main.class.getPackage();

        if (pkg == null) {
            out.println( "No version information available !");
            out.println( "Please check the build process (build.xml).");
            return;
        }

        out.println( "=======================================================" );
        out.println( pkg.getImplementationTitle());
        out.println( "Version " + pkg.getImplementationVersion());
        out.println( "=======================================================" );
    }


    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    private static Opts getOpts( String[] args ) {
        Opts ret = new Opts();

        boolean printUsage = false;

        for (int i=0; i < args.length;) {
            String opt = args[ i++ ];

            if (!opt.startsWith("-")) {
                ret.files = new File[ args.length - i + 1 ];

                for (int j=0; j < ret.files.length; j++, i++) {
                    File file = ret.files[ j ] = new File( ret.directory, args[i - 1] );

                    if (!file.exists() || !file.canRead()) {
                        return error("File '" + file.getAbsolutePath() + "' cannot be opened.");
                    }
                }

                break;
            }

            if ("-V".equals( opt ) || "--version".equals( opt )) {
                printVersion();
                return null;
            }

            if ("-?".equals( opt ) || "--help".equals( opt ) || "--usage".equals( opt )) {
                usage();
                return null;
            }

            if (i >= args.length) return error( "Missing argument for option '" + opt + "'" );

            String val = args[ i++ ];

            if ("-d".equals( opt ) || "--decimal".equals( opt )) {
                try {
                    ret.decimalPoints = Integer.parseInt( val );
                }
                catch (Exception e) {
                    return error( "Invalid number of decimal points - must be an integer - was '" + val + "'");
                }
            }
            else if ("-D".equals( opt ) || "--directory".equals( opt )) {
                File file = ret.directory = new File( val );

                if (!file.exists() || !file.canRead()) {
                    return error("Directory '" + file.getAbsolutePath() + "' cannot be opened.");
                }
                if (!file.isDirectory()) {
                    return error("File '" + file.getAbsolutePath() + "' is not a directory.");
                }
                continue;
            }
            else if ("-p".equals( opt ) || "--period".equals( opt )) {
                try {
                    ret.period = Integer.parseInt( val );
                }
                catch (Exception e) {
                    return error( "Invalid period - must be an integer - was '" + val + "'");
                }
            }
            else if ("-S".equals( opt ) || "--sync-period".equals( opt )) {
                char c = val.length() == 1 ? val.charAt( 0 ) : 'x';

                switch (c) {
                    case 'h': ret.syncHour = true;
                    case 'm': ret.syncMin = true;
                    case 's': ret.syncSec = true;
                        break;
                    default:
                        return error( "Unknown sync-period option value '" + val + "'" );
                }
            }
            else return error("Unknown option '" + opt + "'");
        }

        return ret;
    }


    private static Opts error( String msg ) {
        System.err.println( "\nERROR: " + msg + "\n" );
        usage();
        return null;
    }


    private static void usage() {
        System.err.println( "usage: java -jar bsc-example.jar [OPTIONS] [FILE1 [FILE2...]]" );
        System.err.println( "BSC-Example - payment tracker.\n");
        System.err.println( " OPTIONS:");
        System.err.println( "  -d, --decimal NUMBER         max number of decimal points (default=2)");
        System.err.println( "  -D, --directory DIR          parent directory used for relative paths (input)");
        System.err.println( "  -p, --period SECONDS         the reporting period, default is 60 seconds.");
        System.err.println( "  -S, --sync-period {s|m|h}    sync reporting with clock's seconds|minutes|hours");
        System.err.println( "  -?, --help                   prints this help and exits");
        System.err.println( "      --usage");
        System.err.println( "  -V, --version                prints the version and exits");
    }


}
