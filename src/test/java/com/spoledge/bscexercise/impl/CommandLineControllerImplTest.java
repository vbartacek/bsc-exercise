package com.spoledge.bscexercise.impl;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class CommandLineControllerImplTest {

    @Test
    public void testStartStop() {
        CommandLineControllerImpl ctrl = ctrl();
        ctrl.start();
        ctrl.stop();
    }


    @Test
    public void testDelayedStop() {
        CommandLineControllerImpl ctrl = ctrl();
        ctrl.start();
        ctrl.delayedStop( 500 );

        try { Thread.sleep( 550 );} catch (InterruptedException e) {}
    }


    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    private CommandLineControllerImpl ctrl() {
        CommandLineControllerImpl ctrl = new CommandLineControllerImpl();
        ctrl.setPaymentProcessor( new MemoryPaymentProcessorImpl());
        ctrl.setPaymentReporter( new SimplePaymentReporterImpl());
        ctrl.setMoneyParser( new MoneyParserImpl());

        return ctrl;
    }

}
