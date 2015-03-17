package com.spoledge.bscexercise;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.spoledge.bscexercise.model.Curr;
import com.spoledge.bscexercise.model.Money;


/**
 * Test utilities.
 */
public class TestUtils {

    private static SimpleDateFormat fmtDateTime;


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    public static Money usd( String amount ) {
        return usd( new BigDecimal( amount ));
    }

    public static Money usd( BigDecimal amount ) {
        return money( "USD", amount );
    }

    public static Curr usd() {
        return curr( "USD" );
    }


    public static  Money eur( String amount ) {
        return eur( new BigDecimal( amount ));
    }

    public static Money eur( BigDecimal amount ) {
        return money( "EUR", amount );
    }

    public static Curr eur() {
        return curr( "EUR" );
    }


    public static Money money( String currencyCode, BigDecimal amount ) {
        return new Money( curr( currencyCode ), amount );
    }


    public static Curr curr( String currencyCode ) {
        return Curr.getInstance( currencyCode );
    }


    /**
     * Create date time in ISO format
     */
    public static Date dateTime( String s ) {
        if (fmtDateTime == null) fmtDateTime = new SimpleDateFormat( "yyyy-MM-hh HH:mm:ss" );

        try {
            return fmtDateTime.parse( s );
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }

    }

}
