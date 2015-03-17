package com.spoledge.bscexercise.util;

import java.io.File;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.spoledge.bscexercise.MoneyParseException;
import com.spoledge.bscexercise.impl.MoneyParserImpl;
import com.spoledge.bscexercise.model.Money;

public class MoneyFileTest {

    private MoneyFile mf;


    ////////////////////////////////////////////////////////////////////////////
    // Junit
    ////////////////////////////////////////////////////////////////////////////

    @After
    public void tearDown() {
        if (mf != null) {
            mf.close();
            mf = null;
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFetch() throws Exception {
        mf = new MoneyFile( new MoneyParserImpl( 2 ), sample( "example.txt" ));
        int count = 0;

        for (Iterator<Money> iter = mf.fetch(); iter.hasNext();) {
            Money money = iter.next();
            count++;
        }

        assertEquals( "# lines", 5, count );
    }


    @Test
    public void testValidate() throws Exception {
        mf = new MoneyFile( new MoneyParserImpl( 2 ), sample( "example.txt" ));
        mf.validate();
    }


    @Test
    public void testValidate_decimals_ok() throws Exception {
        mf = new MoneyFile( new MoneyParserImpl( 2 ), sample( "decimals.txt" ));
        mf.validate();
    }


    @Test( expected = MoneyParseException.class )
    public void testValidate_decimals_ko() throws Exception {
        mf = new MoneyFile( new MoneyParserImpl(), sample( "decimals.txt" ));
        mf.validate();
    }


    @Test( expected = MoneyParseException.class )
    public void testValidate_wrong() throws Exception {
        mf = new MoneyFile( new MoneyParserImpl( 2 ), sample( "wrong.txt" ));
        mf.validate();
    }


    @Test( expected = java.io.IOException.class )
    public void testValidate_unexistent() throws Exception {
        mf = new MoneyFile( new MoneyParserImpl( 2 ), sample( "unexistent.txt" ));
        mf.validate();
    }


    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    private File sample( String name ) {
        return new File( System.getProperty( "testSampleDir" ), name );
    }

}
