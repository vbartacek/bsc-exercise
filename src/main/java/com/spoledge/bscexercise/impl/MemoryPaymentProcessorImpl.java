package com.spoledge.bscexercise.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.spoledge.bscexercise.PaymentProcessor;

import com.spoledge.bscexercise.model.Balance;
import com.spoledge.bscexercise.model.Curr;
import com.spoledge.bscexercise.model.Money;


/**
 * The in-memory implementation of the PaymentProcessor.
 * The implementation is thread-safe.
 */
public class MemoryPaymentProcessorImpl implements PaymentProcessor {

    private HashMap<Curr, Money> allCurrenciesMap = new HashMap<Curr, Money>();
    private int lastTransationId;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new memory payment processor.
     * The last transaction id is set to 0.
     */
    public MemoryPaymentProcessorImpl() {
        this( 0 );
    }


    /**
     * Creates a new memory payment processor.
     * @param lastTransationId the initial value of the last transaction id
     */
    public MemoryPaymentProcessorImpl( int lastTransationId ) {
        this.lastTransationId = lastTransationId;
    }


    ////////////////////////////////////////////////////////////////////////////
    // PaymentProcessor
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Registers a single payment.
     * @param payment the amount and type of money to be registered
     * @return the transaction id of the payment being registered
     * @throws NullPointerException when the parameter is null
     */
    public synchronized int registerPayment( Money payment ) {
        if (payment == null) throw new NullPointerException( "Missing payment" );

        Curr currency = payment.getCurrency();
        Money balance = allCurrenciesMap.get( currency );

        // NOTE: we keep the balance even if it is zero,
        //      so it is possible to find out which currencies were really registered
        //      Avoiding the zero balances can be done by the output formatters

        if (balance != null) {
            balance = balance.add( payment );
        }
        else {
            balance = payment;
        }

        allCurrenciesMap.put( currency, balance );

        return ++lastTransationId;
    }


    /**
     * Returns the balance.
     */
    public synchronized Balance getBalance() {
        // this is bad, because the list would be bacjed up by the map,
        // so any change would raise an exception or even unpredictable behavior:
        //      list = allCurrenciesMap.values()
        //
        // instead of that we must copy the values to a new list:

        return new Balance( lastTransationId, new ArrayList<Money>( allCurrenciesMap.values()));
    }

}
