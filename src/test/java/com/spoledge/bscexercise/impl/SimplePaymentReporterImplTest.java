package com.spoledge.bscexercise.impl;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.spoledge.bscexercise.TestUtils.*;


public class SimplePaymentReporterImplTest {

    @Test
    public void testGeneratePaymentReport() {
        MemoryPaymentProcessorImpl pp = new MemoryPaymentProcessorImpl();
        SimplePaymentReporterImpl impl = new SimplePaymentReporterImpl();
        impl.setPaymentProcessor( pp );

        impl.generatePaymentReport();

        pp.registerPayment( usd( "10" ));
        impl.generatePaymentReport();

        pp.registerPayment( usd( "10.01" ));
        pp.registerPayment( eur( "-10" ));
        impl.generatePaymentReport();
    }

}
