package com.spoledge.bscexercise.impl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.spoledge.bscexercise.model.Balance;
import com.spoledge.bscexercise.model.Money;

import static com.spoledge.bscexercise.TestUtils.*;


public class MemoryPaymentProcessorTest {

    ////////////////////////////////////////////////////////////////////////////
    // Tests
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testNewEmpty() {
        MemoryPaymentProcessorImpl impl = new MemoryPaymentProcessorImpl();

        Balance balance = impl.getBalance();

        assertNotNull( "balance", balance );
        assertEquals( "last tx id", 0, balance.getLastTransationId());
        assertNotNull( "list of money", balance.getAllMoney());
        assertTrue( "list of money empty", balance.getAllMoney().isEmpty());
    }


    @Test
    public void testExistingEmpty() {
        MemoryPaymentProcessorImpl impl = new MemoryPaymentProcessorImpl( 34 );

        Balance balance = impl.getBalance();

        assertNotNull( "balance", balance );
        assertEquals( "last tx id", 34, balance.getLastTransationId());
        assertNotNull( "list of money", balance.getAllMoney());
        assertTrue( "list of money empty", balance.getAllMoney().isEmpty());
    }


    @Test
    public void testPayments() {
        MemoryPaymentProcessorImpl impl = new MemoryPaymentProcessorImpl();

        assertEquals( "payment #", 1, impl.registerPayment( usd( "10" )));
        assertEquals( "payment #", 2, impl.registerPayment( usd( "10" )));
        assertEquals( "payment #", 3, impl.registerPayment( usd( "30" )));
        assertEquals( "payment #", 4, impl.registerPayment( usd( "-0.01" )));

        Balance balance = impl.getBalance();

        assertNotNull( "balance", balance );
        assertEquals( "last tx id", 4, balance.getLastTransationId());
        assertNotNull( "list of money", balance.getAllMoney());
        assertEquals( "size of list of money", 1, balance.getAllMoney().size());
        assertEquals( "USD balance", new BigDecimal( "49.99" ), ofCurrencyAmount( balance.getAllMoney(), usd()));

        assertEquals( "payment #", 5, impl.registerPayment( eur( "-1" )));
        assertEquals( "payment #", 6, impl.registerPayment( eur( "10" )));
        assertEquals( "payment #", 7, impl.registerPayment( eur( "-30" )));
        assertEquals( "payment #", 8, impl.registerPayment( eur( "-0.50" )));

        balance = impl.getBalance();

        assertNotNull( "[2] balance", balance );
        assertEquals( "[2] last tx id", 8, balance.getLastTransationId());
        assertNotNull( "[2] list of money", balance.getAllMoney());
        assertEquals( "size of list of money", 2, balance.getAllMoney().size());
        assertEquals( "USD balance", new BigDecimal( "49.99" ), ofCurrencyAmount( balance.getAllMoney(), usd()));
        assertEquals( "EUR balance", new BigDecimal( "-21.50" ), ofCurrencyAmount( balance.getAllMoney(), eur()));
    }


    @Test
    public void testZeroPayments() {
        MemoryPaymentProcessorImpl impl = new MemoryPaymentProcessorImpl();

        assertEquals( "payment #", 1, impl.registerPayment( usd( "0" )));

        Balance balance = impl.getBalance();

        assertNotNull( "balance", balance );
        assertEquals( "last tx id", 1, balance.getLastTransationId());
        assertNotNull( "list of money", balance.getAllMoney());
        assertEquals( "size of list of money", 1, balance.getAllMoney().size());
        assertTrue( "USD balance", ofCurrency( balance.getAllMoney(), usd()).isZero());

        assertEquals( "payment #", 2, impl.registerPayment( usd( "10" )));
        assertFalse( "[2] USD balance", ofCurrency( impl.getBalance().getAllMoney(), usd()).isZero());

        assertEquals( "payment #", 3, impl.registerPayment( usd( "-10" )));
        assertTrue( "[3] USD balance", ofCurrency( impl.getBalance().getAllMoney(), usd()).isZero());
    }


    ////////////////////////////////////////////////////////////////////////////
    // Private
    ////////////////////////////////////////////////////////////////////////////

    private BigDecimal ofCurrencyAmount( List<Money> list, Currency curr ) {
        return ofCurrency( list, curr ).getAmount();
    }


    private Money ofCurrency( List<Money> list, Currency curr ) {
        for (Money money : list) {
            if (curr == money.getCurrency()) return money;
        }

        fail( "Missing currency " + curr + " in the balancies list" );

        return null;
    }

}
