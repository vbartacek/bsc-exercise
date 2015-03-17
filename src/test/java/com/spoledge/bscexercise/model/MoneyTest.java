package com.spoledge.bscexercise.model;

import java.math.BigDecimal;

import org.junit.Test;
import static org.junit.Assert.*;

import static com.spoledge.bscexercise.TestUtils.*;


public class MoneyTest {

    ////////////////////////////////////////////////////////////////////////////
    // Tests
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testConstructor_ok() {
        Money m1 = new Money( curr( "USD" ), BigDecimal.TEN );

        assertEquals( "currency set", curr( "USD" ), m1.getCurrency());
        assertEquals( "amount set", BigDecimal.TEN, m1.getAmount());
    }


    @Test( expected = NullPointerException.class )
    public void testConstructor_missing_currency() {
        new Money( null, BigDecimal.TEN );
    }


    @Test( expected = NullPointerException.class )
    public void testConstructor_missing_amount() {
        new Money( curr( "USD" ), null );
    }


    @Test
    public void testAdd_ok() {
        Money usd_1_23 = usd( "1.23" );
        Money usd_3_45 = usd( "3.45" );
        Money result = usd_1_23.add( usd_3_45 );

        assertNotNull( "result not null", result );
        assertEquals( "currency set", usd_1_23.getCurrency(), result.getCurrency());
        assertEquals( "amount calculated", new BigDecimal( "4.68" ), result.getAmount());

        // test negative:
        Money usd_minus_1_24 = usd( "-1.24" );
        result = usd_1_23.add( usd_minus_1_24 );

        assertNotNull( "negative result not null", result );
        assertEquals( "negative currency set", usd_1_23.getCurrency(), result.getCurrency());
        assertEquals( "negative amount calculated", new BigDecimal( "-0.01" ), result.getAmount());
    }


    @Test( expected = NullPointerException.class )
    public void testAdd_missing_other() {
        usd( "1.23" ).add( null );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testAdd_currency_mismatch() {
        usd( "1.23" ).add( eur( "1" ));
    }


    @Test
    public void testIsZero() {
        assertTrue( "ZERO", usd( BigDecimal.ZERO ).isZero());
        assertTrue( "0", usd( "0" ).isZero());
        assertTrue( "-0", usd( "-0" ).isZero());
        assertTrue( "0.00", usd( "0.00" ).isZero());
        assertTrue( "-0.00", usd( "-0.00" ).isZero());

        assertFalse( "ONE", usd( BigDecimal.ONE ).isZero());
        assertFalse( "1.23", usd( "1.23" ).isZero());
        assertFalse( "-0.01", usd( "-0.01" ).isZero());
    }


    @Test
    public void testToString() {
        assertEquals( "no dollars", "USD 0", usd( BigDecimal.ZERO ).toString());
        assertEquals( "no euro and no cents", "EUR 0.00",
            eur( BigDecimal.ZERO.movePointLeft( 2 )).toString());

        assertEquals( "one euro-cent", "EUR 0.01", eur( BigDecimal.ONE.movePointLeft( 2 )).toString());
        assertEquals( "two dollar and 99 cents", "USD 2.99", usd( "2.99" ).toString());

        assertEquals( "dept of two dollar and 99 cents", "USD -2.99", usd( "-2.99" ).toString());
    }

}
