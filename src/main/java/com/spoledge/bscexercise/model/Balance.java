package com.spoledge.bscexercise.model;

import java.util.List;


/**
 * The balance status.
 * It contains information about the last known transaction
 * and list of known balancies.
 */
public final class Balance {

    private int lastTransationId;
    private List<Money> allMoney;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new balance.
     */
    public Balance( int lastTransationId, List<Money> allMoney ) {
        this.lastTransationId = lastTransationId;
        this.allMoney = allMoney;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the last transaction id.
     */
    public int getLastTransationId() {
        return lastTransationId;
    }


    /**
     * Returns the list of money.
     */
    public List<Money> getAllMoney() {
        return allMoney;
    }

}
