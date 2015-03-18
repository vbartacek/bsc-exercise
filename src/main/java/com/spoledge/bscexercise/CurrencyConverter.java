package com.spoledge.bscexercise;

import com.spoledge.bscexercise.model.Curr;
import com.spoledge.bscexercise.model.Money;


/**
 * Curency converter.
 * Converts money to the one target currency.
 */
public interface CurrencyConverter {

    /**
     * Returns the target currency
     */
    public Curr getTargetCurrency();


    /**
     * Converts money to the target currency.
     * @param money the money to be converted
     * @return the converted money or null if no conversion rate is set
     *  for the input currency
     */
    public Money convertMoney( Money money );

}
