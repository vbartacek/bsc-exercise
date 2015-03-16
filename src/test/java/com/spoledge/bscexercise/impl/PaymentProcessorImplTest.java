package com.spoledge.bscexercise.impl;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.spoledge.bscexercise.TestUtils.*;


public class PaymentProcessorImplTest {

    private MyImpl impl;


    ////////////////////////////////////////////////////////////////////////////
    // Junit
    ////////////////////////////////////////////////////////////////////////////

    @Before
    public void tearUp() {
        impl = new MyImpl();
    }

    @After
    public void tearDown() {
        if (impl != null) {
            impl.stopPaymentReporting();
            impl = null;
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Tests
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testSyncStartDate() {
        Date date = new Date( dateTime( "2015-03-16 15:12:34" ).getTime() + 987 );

        assertEquals( "0-0-0", date, AbstractPaymentReporterImpl.syncStartDate( date, false, false, false ));
        assertEquals( "1-0-0", dateTime( "2015-03-16 15:12:35" ),
            AbstractPaymentReporterImpl.syncStartDate( date, true, false, false ));

        assertEquals( "1-1-0", dateTime( "2015-03-16 15:13:00" ),
            AbstractPaymentReporterImpl.syncStartDate( date, true, true, false ));

        assertEquals( "1-1-1", dateTime( "2015-03-16 16:00:00" ),
            AbstractPaymentReporterImpl.syncStartDate( date, true, true, true ));
    }


    @Test
    public void testTimer() throws Exception {
        impl.setPeriod( 100 );
        impl.startPaymentReporting();

        Thread.sleep( 550 );

        impl.stopPaymentReporting();
        int count = impl.count();

        assertTrue( ">= 5", count >= 5 );
        assertTrue( "<= 6", count <= 6 );

        Thread.sleep( 550 );
        assertEquals( "count not changing", count, impl.count());
    }


    ////////////////////////////////////////////////////////////////////////////
    // Inner
    ////////////////////////////////////////////////////////////////////////////

    class MyImpl extends AbstractPaymentReporterImpl  {
        int count;

        public synchronized void generatePaymentReport() {
            count++;
        }

        synchronized int count() {
            return count;
        }
        
    }
}
