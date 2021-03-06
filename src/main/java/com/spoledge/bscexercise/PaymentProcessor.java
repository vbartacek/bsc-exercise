package com.spoledge.bscexercise;

import com.spoledge.bscexercise.model.Balance;
import com.spoledge.bscexercise.model.Money;


/**
 * The payment processor interface.
 * Abstraction of the payment processor functionality.
 */
public interface PaymentProcessor {

    /**
     * Registers a single payment.
     * @param payment the amount and type of money to be registered
     * @return the transaction id of the payment being registered
     * @throws NullPointerException when the parameter is null
     */
    public int registerPayment( Money payment );


    /**
     * Returns the balance.
     */
    public Balance getBalance();

}
