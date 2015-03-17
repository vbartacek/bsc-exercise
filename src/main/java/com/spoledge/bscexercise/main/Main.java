package com.spoledge.bscexercise.main;

import java.io.PrintWriter;

import com.spoledge.bscexercise.impl.*;


/**
 * The entry point of the application.
 */
public class Main {

    public static void main( String[] args ) {
        PrintWriter writer = new PrintWriter( System.out );

        MoneyParserImpl moneyParser = new MoneyParserImpl( 2 );
        SimplePaymentReporterImpl reporter = new SimplePaymentReporterImpl( writer );
        MemoryPaymentProcessorImpl paymentProcessor = new MemoryPaymentProcessorImpl();
        CommandLineControllerImpl controller = new CommandLineControllerImpl( null, writer );

        reporter.setPeriod( 10000 );
        reporter.setPaymentProcessor( paymentProcessor );

        controller.setPaymentProcessor( paymentProcessor );
        controller.setPaymentReporter( reporter );
        controller.setMoneyParser( moneyParser );

        controller.start();
    }

}
