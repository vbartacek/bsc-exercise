package com.spoledge.bscexercise.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spoledge.bscexercise.Controller;
import com.spoledge.bscexercise.CurrencyConverter;
import com.spoledge.bscexercise.MoneyParseException;
import com.spoledge.bscexercise.MoneyParser;
import com.spoledge.bscexercise.PaymentProcessor;
import com.spoledge.bscexercise.PaymentReporter;

import com.spoledge.bscexercise.model.Money;
import com.spoledge.bscexercise.util.MoneyFile;


/**
 * A command line implementation of the controller.
 */
public class CommandLineControllerImpl implements Runnable, Controller {

    /**
     * The number of milliseconds between calls to (reader.ready()).
     * @see readLine()
     */
    private static final int WAIT_BETWEEN_INPUT_READY = 200;


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    private PaymentProcessor paymentProcessor;
    private PaymentReporter paymentReporter;
    private CurrencyConverter currencyConverter;
    private MoneyParser moneyParser;
    private File directory;

    private BufferedReader reader;
    private PrintWriter writer;

    private Thread thread;
    private Object lockThread = new Object();

    protected Log log = LogFactory.getLog( getClass());


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new controller using standard in and out streams.
     */
    public CommandLineControllerImpl() {
        this( null, null );
    }


    /**
     * Creates a new controller.
     * @param reader an input reader, if null then the standard input is used
     * @param writer an output writer, if null then the standard output is used
     */
    public CommandLineControllerImpl( Reader reader, PrintWriter writer ) {
        try {
            this.reader = reader != null ?
                            (reader instanceof BufferedReader) ?
                                ((BufferedReader)reader) :
                                new BufferedReader( reader ) :
                            new BufferedReader( new InputStreamReader( System.in ));

            this.writer = writer != null ? writer : new PrintWriter( System.out );
        }
        catch (Exception e) {
            log.error( "Cannot initialize controller: ", e );
            throw new RuntimeException( e );
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Runnable
    ////////////////////////////////////////////////////////////////////////////

    public void run() {
        log.info( "Main loop started." );

        println( "Hello! Type 'help' for help." );

        while (true) {
            String line = readLine();

            if (line == null) break;

            // humans often add spaces:
            line = line.trim();

            if (log.isDebugEnabled()) {
                log.debug( "line='" + line + "'" );
            }

            processCommandLine( line );
        }

        println( "Bye!" );

        log.info( "Main loop stopped." );
    }


    ////////////////////////////////////////////////////////////////////////////
    // Controller
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Starts the controller/app.
     */
    public void start() {
        if (paymentReporter == null || paymentProcessor == null || moneyParser == null) {
            throw new RuntimeException( "Components not initialized correctly" );
        }

        synchronized (lockThread) {
            if (thread != null) throw new IllegalStateException( "Already started" );

            paymentReporter.startPaymentReporting();

            log.info( "Starting main controller's thread." );

            thread = new Thread( this, "Controller");
            thread.start();
        }
    }


    /**
     * Stops the controller/app.
     */
    public void stop() {
        paymentReporter.stopPaymentReporting();

        synchronized (lockThread) {
            if (thread != null) {
                thread = null;
                lockThread.notify();
            }
        }
    }


    /**
     * Loads a file.
     */
    public void loadFile( File file ) {
        MoneyFile mf = new MoneyFile( moneyParser, file );
        println( "Loading file " + file + "..." );
        int count = 0;

        try {
            mf.validate();

            for (Iterator<Money> iter = mf.fetch(); iter.hasNext();) {
                paymentProcessor.registerPayment( iter.next());
                count++;
            }

            println( "File " + file + " successfully loaded (" + count + " lines)." );
        }
        catch (Exception e) {
            log.error( "Cannot load file '" + file + "'", e);
            println( "ERROR Loading file " + file + ", aborting: " + e );
        }
        finally {
            mf.close();
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    public void setPaymentProcessor( PaymentProcessor paymentProcessor ) {
        this.paymentProcessor = paymentProcessor;
    }


    public PaymentReporter getPaymentReporter() {
        return paymentReporter;
    }

    public void setPaymentReporter( PaymentReporter paymentReporter ) {
        this.paymentReporter = paymentReporter;
    }


    public CurrencyConverter getCurrencyConverter() {
        return currencyConverter;
    }

    public void setCurrencyConverter( CurrencyConverter currencyConverter ) {
        this.currencyConverter = currencyConverter;
    }


    public MoneyParser getMoneyParser() {
        return moneyParser;
    }

    public void setMoneyParser( MoneyParser moneyParser ) {
        this.moneyParser = moneyParser;
    }


    public File getDirectory() {
        return directory;
    }

    /**
     * Sets the directory used for relative paths.
     */
    public void setDirectory( File directory ) {
        this.directory = directory;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Protected
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Processes one command line.
     * @param line the input line - always non-null
     */
    protected void processCommandLine( String line ) {
        // assume spaces are only separators - no escaping (quoting):
        String[] args = line.split( "\\s+" );
        String cmd = args[0];

        if ( "quit".equals( cmd )) processCmdQuit( args );
        else if ( "report".equals( cmd )) processCmdReport( args );
        else if ( "forex".equals( cmd )) processCmdForex( args );
        else if ( "file".equals( cmd )) processCmdFile( args );
        else if ( "help".equals( cmd )) processCmdHelp( args );
        else if ( args.length == 2 ) {
            try {
                Money money = moneyParser.parseMoney( cmd, args[ 1 ]);
                int txId = paymentProcessor.registerPayment( money );
                println( "Registered payment " + money + " as tx[" + txId + ']' );
            }
            catch (MoneyParseException e) {
                println( "ERROR - invalid payment format: " + e.getMessage());
            }
        }
        else {
            println( "ERROR - unknown command '" + line + "', type 'help'" );
        }
    }


    /**
     * Processes the 'quit' command.
     */
    protected void processCmdQuit( String[] args ) {
        switch (args.length) {
            case 1:
                stop();
                return;

            case 2:
                Integer delay = parseInteger( args[1] );

                if (delay != null) {
                    delayedStop( delay * 1000L );
                    println( "Delayed quit scheduled" );
                    return;
                }
                break;

            default:
                println( "Too many parameters" );
        }
    }


    /**
     * Processes the 'report' command.
     */
    protected void processCmdReport( String[] args ) {
        // just a warning:
        if (args.length != 1) println( "Too many parameters" );

        paymentReporter.generatePaymentReport();
    }


    /**
     * Processes the 'forex' command.
     */
    protected void processCmdForex( String[] args ) {
        if (args.length < 3) {
            println( "Too few parameters, type 'help'" );
            return;
        }

        if (args.length > 3) {
            println( "Too many parameters, type 'help'" );
            return;
        }

        // check format first:
        Money money = null;

        try {
            money = moneyParser.parseMoney( args[ 1 ], args[ 2 ] );
        }
        catch (MoneyParseException e) {
            println( "ERROR - invalid forex format: " + e.getMessage());
            return;
        }

        CurrencyConverter cc = currencyConverter;

        if (cc == null) {
            println( "No exchange rates loaded." );
            return;
        }

        Money converted = cc.convertMoney( money );

        if (converted != null) {
            println( "Forex " + money + " ==> " + converted );
        }
        else {
            println( "No conversion rate for " + money.getCurrency() + " set" );
        }
    }


    /**
     * Processes the 'file' command.
     */
    protected void processCmdFile( String[] args ) {
        if (args.length == 1) println( "Too few parameters, type 'help'" );

        for (int i=1; i < args.length; i++) {
            try {
                loadFile( new File( directory, args[i] ));
            }
            catch (Exception e) {
                // already handled
            }
        }
    }


    /**
     * Processes the 'help' command.
     */
    protected void processCmdHelp( String[] args ) {
        println( "Please enter a payment (CurrencyCode Value) or a command:" );
        println( "    quit [delay_in_sec]  - exits the app" );
        println( "    report               - prints the report immediatelly" );
        println( "    forex CURR AMOUNT    - converts currency" );
        println( "    file F1 [F2 [F3...]] - loads payments from file(s)" );
        println( "    help                 - prints this info" );
    }


    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Reads one line of the input.
     * This method is aware of calling the stop() method.
     * So we use a non-blocking solution for reading from the (standard) input.
     * For more information see http://www.javaspecialists.eu/archive/Issue153.html
     *
     * @return the line or null if EOF is reached or stop() method was called
     */
    String readLine() {
        String line = null;

        try {
            boolean running;

            synchronized (lockThread) {
                while ((running = thread == Thread.currentThread()) && !reader.ready()) {
                    try {
                        lockThread.wait( WAIT_BETWEEN_INPUT_READY );
                    }
                    catch (InterruptedException e) {
                        log.warn( "Interrupted by somebody else, aborting." );
                        running = false;
                    }
                }
            }

            line = running ? reader.readLine() : null;
        }
        catch (IOException e) {
            log.error( "Cannot read input, aborting: ", e );
        }

        return line;
    }


    /**
     * Calls stop() method asynchronously with the delay.
     * @param delay the delay in milliseconds
     */
    void delayedStop( final long delay ) {
        Thread t =
            new Thread(
                new Runnable() {
                    public void run() {
                        try { Thread.sleep( delay ); } catch (InterruptedException e) {}
                        stop();
                    }
                }
            );

        // if somebody quits earlier, then do not wait for this thread:
        t.setDaemon( true );
        t.start();
    }


    /**
     * Parses an integer and writes a message on error.
     * @return the number or null if canot be parsed
     */
    Integer parseInteger( String s ) {
        try {
            return new Integer( s );
        }
        catch (Exception e) {
            println( "Cannot parse integer value '" + s + "'" );

            return null;
        }
    }


    /**
     * Prints a single line.
     */
    void println( String s ) {
        writer.println( ">> " + s );

        // if somebody will pass a PrintWriter to us, then we do not know if auto-flushing is enabled:
        writer.flush();
    }

}
