package com.spoledge.bscexercise.impl;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.spoledge.bscexercise.MoneyParseException;

import static com.spoledge.bscexercise.TestUtils.*;

public class MoneyParserImplTest {

    @Test
    public void testCanonizeDecimalPoints_ok() throws Exception {
        MoneyParserImpl impl = new MoneyParserImpl( 2 );

        // no changes
        assertSame( "0.00", impl.canonizeDecimalPoints( "0.00" ));
        assertSame( "2.00", impl.canonizeDecimalPoints( "2.00" ));
        assertSame( "-4.00", impl.canonizeDecimalPoints( "-4.00" ));

        // add zeros
        assertEquals( "0.10", impl.canonizeDecimalPoints( "0.1" ));
        assertEquals( "1.10", impl.canonizeDecimalPoints( "1.1" ));
        assertEquals( "3.00", impl.canonizeDecimalPoints( "3.0" ));
        assertEquals( "5.00", impl.canonizeDecimalPoints( "5" ));

        assertEquals( "-0.10", impl.canonizeDecimalPoints( "-0.1" ));
        assertEquals( "-1.10", impl.canonizeDecimalPoints( "-1.1" ));
        assertEquals( "-3.00", impl.canonizeDecimalPoints( "-3.0" ));
        assertEquals( "-5.00", impl.canonizeDecimalPoints( "-5" ));

        // leading dot - add zeros
        assertEquals( "0.90", impl.canonizeDecimalPoints( ".9" ));
        assertEquals( "0.91", impl.canonizeDecimalPoints( ".91" ));

        // strip zeros
        assertEquals( "0.70", impl.canonizeDecimalPoints( "0.700" ));
        assertEquals( "1.70", impl.canonizeDecimalPoints( "1.70000" ));
        assertEquals( "7.00", impl.canonizeDecimalPoints( "7.00000" ));

        assertEquals( "-0.70", impl.canonizeDecimalPoints( "-0.700" ));
        assertEquals( "-1.70", impl.canonizeDecimalPoints( "-1.70000" ));
        assertEquals( "-7.00", impl.canonizeDecimalPoints( "-7.00000" ));

        // leading dot - strip zeros
        assertEquals( "0.80", impl.canonizeDecimalPoints( ".800" ));
        assertEquals( "0.81", impl.canonizeDecimalPoints( ".81000" ));
    }


    @Test
    public void testCanonizeDecimalPoints_zero_ok() throws Exception {
        MoneyParserImpl impl = new MoneyParserImpl();

        // no changes
        assertSame( "0", impl.canonizeDecimalPoints( "0" ));
        assertSame( "1", impl.canonizeDecimalPoints( "1" ));
        assertSame( "-1", impl.canonizeDecimalPoints( "-1" ));

        // strip zeros
        assertEquals( "2", impl.canonizeDecimalPoints( "2.0" ));
        assertEquals( "4", impl.canonizeDecimalPoints( "4.0000" ));
    }


    @Test
    public void testCanonizeDecimalPoints_special() throws Exception {
        MoneyParserImpl impl = new MoneyParserImpl( 2 );

        assertSame( ".", impl.canonizeDecimalPoints( "." ));
    }


    @Test( expected = MoneyParseException.class )
    public void testCanonizeDecimalPoints_too_many() throws Exception {
        MoneyParserImpl impl = new MoneyParserImpl( 2 );

        impl.canonizeDecimalPoints( "1.321" );
    }

}
