package com.spoledge.bscexercise.impl;

import java.math.BigDecimal;
import java.util.Currency;

import com.spoledge.bscexercise.MoneyParseException;
import com.spoledge.bscexercise.MoneyParser;

import com.spoledge.bscexercise.model.Money;


/**
 * The implementation of the MoneyParser.
 * It is aware of decimal points - so it checks that maximum decimal numbers are not exceeded
 * and also canonize the numbers to have exactly decimal points.
 */
public class MoneyParserImpl implements MoneyParser {

    private static final String ZEROS = "0000000000";

    /**
     * The maximal allowed decimal points.
     */
    public static final int MAX_DECIMAL_POINTS = ZEROS.length();


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    private int decimalPoints;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new parser with 0 decimal points.
     */
    public MoneyParserImpl() {
        this( 0 );
    }


    /**
     * Creates a new parser.
     * @param decimalPoints the maximum decimal points allowed.
     */
    public MoneyParserImpl( int decimalPoints ) {
        setDecimalPoints( decimalPoints );
    }


    ////////////////////////////////////////////////////////////////////////////
    // MoneyParser
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Parses money string line.
     * @param line the string line containing both currency and amount
     * @return the parsed money - always non-null
     * @throws MoneyParseException when the money cannot be parsed
     * @throws NullPointerException when the parameter is null
     */
    public Money parseMoney( String line ) throws MoneyParseException {
        if (line == null) throw new NullPointerException( "Missing line parameter" );

        int n = line.indexOf( ' ' );

        if (n == -1) throw new MoneyParseException( "Invalid line format" );

        return parseMoney( line.substring( 0, n ), line.substring( n ));
    }


    /**
     * Parses money string line.
     * @param currency the currency representation
     * @param amount the amount representation
     * @return the parsed money - always non-null
     * @throws MoneyParseException when the money cannot be parsed
     * @throws NullPointerException when any of the parameters are null
     */
    public Money parseMoney( String currency, String amount ) throws MoneyParseException {
        if (currency == null) throw new NullPointerException( "Missing currency parameter" );
        if (amount == null) throw new NullPointerException( "Missing amount parameter" );

        currency = currency.trim();
        amount = amount.trim();

        Currency curr = null;

        try {
            curr = Currency.getInstance( currency );
        }
        catch (IllegalArgumentException e) {
            throw new MoneyParseException( "Invalid currency code '" + currency + "'" );
        }

        String canonizedAmount = canonizeDecimalPoints( amount );
        BigDecimal bd = null;

        try {
            return new Money( curr, new BigDecimal( canonizedAmount ));
        }
        catch (NumberFormatException e) {
            throw new MoneyParseException( "Invalid amount '" + amount + "'" );
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    public int getDecimalPoints() {
        return decimalPoints;
    }


    /**
     * Sets maximum allowed decimal points.
     */
    public void setDecimalPoints( int decimalPoints ) {
        if (decimalPoints < 0) throw new IllegalArgumentException( "Negative values not allowed" );
        if (decimalPoints > MAX_DECIMAL_POINTS) throw new IllegalArgumentException( "Max value exceeded" );

        this.decimalPoints = decimalPoints;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Canonize decimal points.
     * If the value has less decimal points, then additional ones are added.
     * If the value has more decimal points, then try to cut trailing zeros
     * and if not enough, then an exception is thrown.
     * @return the canonized value
     * @throws MoneyParseException if too many decimal points are found
     */
    String canonizeDecimalPoints( String amount ) throws MoneyParseException {
        // avoid numbers starting with decimal point only:
        if (amount.charAt( 0 ) == '.') {
            // let the BigDecimal parser to cope with this:
            if (amount.length() == 1) return amount;
            amount = "0" + amount;
        }

        int n = amount.lastIndexOf( '.' );
        boolean hasDp = n != -1;
        int dp = hasDp ? amount.length() - n - 1 : 0; // decimal points

        if (dp == decimalPoints) {
            return amount;
        }

        // NOTE: first check this, maybe we will strip more decimals than needed
        //      so we will add them in the next step again
        if (dp > decimalPoints) {
            // try to cut trailing zeros:
            int n2 = amount.length() - 1, i;

            for (i = n2; i > 0; i--) {
                if (amount.charAt( i ) != '0') break;
            }

            if (i < n2) {
                // found - we strip it now:
                if ( amount.charAt( i ) == '.' ) {
                    amount = amount.substring( 0, i ); // assume a char before '.'
                    dp = 0;
                    hasDp = false;
                }
                else {
                    dp -= n2 - i;
                    amount = amount.substring( 0, i + 1 );
                }

                if (dp == decimalPoints) return amount;
            }
        }

        if (dp < decimalPoints) {
            return amount + (hasDp ? "" : ".") + ZEROS.substring( 0, decimalPoints - dp );
        }

        throw new MoneyParseException( "Too many decimal points: " + amount );
    }

}
