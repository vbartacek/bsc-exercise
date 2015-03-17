package com.spoledge.bscexercise.model;

import java.math.BigDecimal;


/**
 * Model of money.
 * This is an immutable object.
 */
public final class Money {

    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    private Curr currency;
    private BigDecimal amount;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new money.
     * @param currency the currency
     * @param amount the amount
     * @throws NullPointerException if any of the parameters is null
     */
    public Money( Curr currency, BigDecimal amount ) {
        if (currency == null) throw new NullPointerException( "Missing currency" );
        if (amount == null) throw new NullPointerException( "Missing amount" );
        
        this.currency = currency;
        this.amount= amount;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the currency.
     */
    public Curr getCurrency() {
        return currency;
    }


    /**
     * Returns the amount.
     */
    public BigDecimal getAmount() {
        return amount;
    }


    /**
     * Adds other money to this money.
     * @param other the other money
     * @return a new instance of Money representing the sum of all moeny
     * @throws NullPointerException when the parameter is null
     * @throws IllegalArgumentException when the other money's currency is different than this one's
     */
    public Money add( Money other ) {
        if (other == null) throw new NullPointerException( "Missing other money" );

        // we do not use equals() because (javadoc of java.util.Currency / Curr):
        // "The class is designed so that there's never more
        // than one Currency instance for any given currency"
        if (this.currency != other.currency) throw new IllegalArgumentException( "Currency mismatch" );

        return new Money( this.currency, this.amount.add( other.amount ));
    }


    /**
     * Returns true if this amount is representing zero (no money).
     * NOTE: BigDecimal distinguishes "0" and "0.00", but this method is aware of it.
     */
    public boolean isZero() {
        return java.math.BigInteger.ZERO.equals( amount.unscaledValue());
    }


    /**
     * Returns human readable format: "CCC n"
     * where CCC is the currency code and n is the amount in decimal format.
     */
    @Override
    public String toString() {
        return currency.toString() + ' ' + amount.toPlainString();
    }

}
