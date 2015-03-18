package com.spoledge.bscexercise.impl;

import java.io.File;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.spoledge.bscexercise.TestUtils.*;


public class FileCurrencyConverterImplTest {

    @Test
    public void testAll() throws Exception {
        FileCurrencyConverterImpl impl = new FileCurrencyConverterImpl( usd(), 2 );
        impl.loadFile( sample( "rates.txt" ));

        assertEquals( "EUR 1", new BigDecimal( "1.06" ), impl.convertMoney( eur( "1" )).getAmount());
        assertEquals( "EUR 1", usd(), impl.convertMoney( eur( "1" )).getCurrency());

        assertEquals( "CHF 10", new BigDecimal( "9.94" ), impl.convertMoney( money( "CHF", "10" )).getAmount());

        assertEquals( "EUR 10", new BigDecimal( "105.93" ), impl.convertMoney( eur( "100" )).getAmount());
        assertEquals( "JPY 10", new BigDecimal( "0.08" ), impl.convertMoney( money( "JPY", "10" )).getAmount());
    }


    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    private File sample( String name ) {
        return new File( System.getProperty( "testSampleDir" ), name );
    }
}
