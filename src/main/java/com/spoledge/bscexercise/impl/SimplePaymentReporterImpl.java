package com.spoledge.bscexercise.impl;

import java.io.PrintWriter;

import com.spoledge.bscexercise.CurrencyConverter;
import com.spoledge.bscexercise.PaymentProcessor;
import com.spoledge.bscexercise.PaymentReporter;

import com.spoledge.bscexercise.model.Balance;
import com.spoledge.bscexercise.model.Curr;
import com.spoledge.bscexercise.model.Money;


/**
 * An implementation of the PaymentReporter which generates the report as a plain text.
 */
public class SimplePaymentReporterImpl extends AbstractPaymentReporterImpl {

    public static final String NL = System.getProperty( "line.separator" );


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    private PrintWriter out;
    private PaymentProcessor paymentProcessor;
    private CurrencyConverter currencyConverter;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new reporter which reports to the standard out.
     */
    public SimplePaymentReporterImpl() {
        this( null );
    }


    /**
     * Creates a new reporter.
     * @param out the output writer; if null then the system out is used
     */
    public SimplePaymentReporterImpl( PrintWriter out ) {
        this.out = out != null ? out : new PrintWriter( System.out );
    }


    ////////////////////////////////////////////////////////////////////////////
    // PaymentReporter
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Generates a new payment report.
     * This method can be called explicitly even when the automatic reporting is enabled.
     */
    public void generatePaymentReport() {
        PaymentProcessor pp = this.paymentProcessor;
        CurrencyConverter cc = this.currencyConverter;
        Curr targetCurrency = cc != null ? cc.getTargetCurrency() : null;

        if (pp == null) {
            log.error( "PaymentProcessor not set, cannot obtain data." );
            return;
        }

        Balance balance = pp.getBalance();
        String sLastTransationId = String.valueOf( balance.getLastTransationId());

        StringBuilder sb = new StringBuilder();
        sb.append( NL );
        sb.append( "---- Report after tx[" ).append( sLastTransationId ).append( "] ----" );
        sb.append( NL );

        for (Money money : sortByCurrencyCode( balance.getAllMoney())) {
            if (!money.isZero()) {
                sb.append( money );

                if (cc != null && money.getCurrency() != targetCurrency) {
                    Money converted = cc.convertMoney( money );
                    sb.append( " (" );

                    if (converted != null) sb.append( converted );
                    else sb.append( targetCurrency ).append( " ???" );

                    sb.append( ')' );
                }

                sb.append( NL );
            }
        }

        sb.append( "---------------------------" );
        sb.append( "-----------".substring( 0, sLastTransationId.length()));
        sb.append( NL );

        String report = sb.toString();

        log.info( "Generating report:" + report );

        // we print it at once, so underlying synchronization avoid mixing report messages together:
        out.print( report );
        out.flush();
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    public void setPaymentProcessor( PaymentProcessor paymentProcessor ) {
        this.paymentProcessor = paymentProcessor;
    }


    public CurrencyConverter getCurrencyConverter() {
        return currencyConverter;
    }

    public void setCurrencyConverter( CurrencyConverter currencyConverter ) {
        this.currencyConverter = currencyConverter;
    }

}
