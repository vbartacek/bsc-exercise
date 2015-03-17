package com.spoledge.bscexercise.model;

import java.util.HashMap;


/**
 * The curency.
 * The assignment says "any 3-letter uppercase currency code is valid".
 * So when using the standard java.util.Currency an exception is thrown - e.g. for "RMB".
 * We follow the approach of the system Currency class - for instantiation please
 * use getInstance() method.
 */
public class Curr {

    private static HashMap<String, Curr> allMap = new HashMap<String, Curr>();


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    private String currencyCode;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    private Curr( String currencyCode ) {
        this.currencyCode = currencyCode;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    public String getCurrencyCode() {
        return currencyCode;
    }


    @Override
    public String toString() {
        return currencyCode;
    }


    /**
     * Returns an instance of the given currency.
     * For same codes it returns exactly the same objects.
     * @param code 3-letter uppercase currency code
     * @throws NullPointerException when the code param is null
     * @throws IllegalArgumentException when the code param is not 3-letter uppercase string
     */
    public static Curr getInstance( String code ) {
        synchronized( allMap ) {
            // assume everything is ok first:
            Curr ret = allMap.get( code );

            if (ret == null) {
                if (code == null) throw new NullPointerException( "Missing code param" );

                if (code.length() != 3 || !code.toUpperCase().equals( code )) {
                    throw new IllegalArgumentException( "Invalid currency code '" + code + "'" );
                }

                ret = new Curr( code );
                allMap.put( code, ret );
            }

            return ret;
        }
    }

}
