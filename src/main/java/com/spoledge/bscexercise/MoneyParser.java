package com.spoledge.bscexercise;

import com.spoledge.bscexercise.model.Money;


/**
 * Money parser component interface.
 */
public interface MoneyParser {

    /**
     * Parses money string line.
     * @param line the string line containing both currency and amount
     * @return the parsed money - always non-null
     * @throws MoneyParseException when the money cannot be parsed
     * @throws NullPointerException when the parameter is null
     */
    public Money parseMoney( String line ) throws MoneyParseException;

    /**
     * Parses money string line.
     * @param currency the currency representation
     * @param amount the amount representation
     * @return the parsed money - always non-null
     * @throws MoneyParseException when the money cannot be parsed
     * @throws NullPointerException when any of the parameters are null
     */
    public Money parseMoney( String currency, String amount ) throws MoneyParseException;

}
