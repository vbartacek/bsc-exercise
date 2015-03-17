package com.spoledge.bscexercise.main;

import com.spoledge.bscexercise.impl.*;


/**
 * The entry point of the application.
 */
public class Main {

    public static void main( String[] args ) {
        MoneyParserImpl moneyParser = new MoneyParserImpl( 2 );
        SimplePaymentReporterImpl reporter = new SimplePaymentReporterImpl();
        MemoryPaymentProcessorImpl paymentProcessor = new MemoryPaymentProcessorImpl();
        CommandLineController controller = new CommandLineController();

        reporter.setPeriod( 10000 );
        reporter.setPaymentProcessor( paymentProcessor );

        controller.setPaymentProcessor( paymentProcessor );
        controller.setPaymentReporter( reporter );
        controller.setMoneyParser( moneyParser );

        controller.start();
    }

}
