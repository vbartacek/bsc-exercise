package com.spoledge.bscexercise.impl;

import java.io.File;
import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spoledge.bscexercise.CurrencyConverter;
import com.spoledge.bscexercise.MoneyParseException;

import com.spoledge.bscexercise.model.Curr;
import com.spoledge.bscexercise.model.Money;

import com.spoledge.bscexercise.util.MoneyFile;


/**
 * The implementation of CurrencyConverter which loads the conversion
 * rates frm a file.
 */
public class FileCurrencyConverterImpl implements CurrencyConverter {

    private final Curr targetCurrency;
    private final int decimalDigits;

    private HashMap<Curr, BigDecimal> rates = new HashMap<Curr, BigDecimal>();
    private Object lock = new Object();

    protected Log log = LogFactory.getLog( getClass());


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new converter.
     */
    public FileCurrencyConverterImpl( Curr targetCurrency, int decimalDigits ) {
        this.targetCurrency = targetCurrency;
        this.decimalDigits = decimalDigits;
    }


    ////////////////////////////////////////////////////////////////////////////
    // CurrencyConverter
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the target currency
     */
    public Curr getTargetCurrency() {
        return targetCurrency;
    }


    /**
     * Converts money to the target currency.
     * @param money the money to be converted
     * @return the converted money or null if no conversion rate is set
     *  for the input currency
     */
    public Money convertMoney( Money money ) {
        if (money == null) throw new NullPointerException( "Missing money param" );

        Curr currency = money.getCurrency();

        // trivial conversion:
        if (currency == targetCurrency) return money;

        synchronized( lock ) {
            BigDecimal rate = rates.get( currency );

            if (rate == null) return null;

            BigDecimal converted =
                money.getAmount()
                    .multiply( rate )
                    .setScale( decimalDigits, RoundingMode.HALF_EVEN );

            return new Money( targetCurrency, converted );
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Loads a file containing the conversion rates.
     * Each line contains one currency: (CurrencyCode Amount)
     */
    public void loadFile( File file ) throws IOException, MoneyParseException {
        MoneyFile mf = new MoneyFile( new MoneyParserImpl( MoneyParserImpl.MAX_DECIMAL_POINTS ), file );

        try {
            HashMap<Curr, BigDecimal> map = new HashMap<Curr, BigDecimal>();

            for (Iterator<Money> iter = mf.fetch(); iter.hasNext();) {
                Money rate = iter.next();
                Curr currency = rate.getCurrency();

                if (currency == targetCurrency) {
                    log.warn( "Skipping target currency rate " + rate );
                    continue;
                }

                map.put( currency, rate.getAmount());
            }

            setRates( map );
        }
        finally {
            mf.close();
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Sets a conversion rate table.
     */
    void setRates( HashMap<Curr, BigDecimal> map ) {
        synchronized( lock ) {
            rates = map;
        }
    }

}
