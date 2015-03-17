package com.spoledge.bscexercise.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CurrTest {

    @Test
    public void testGetInstance() {
        Curr usd1 = Curr.getInstance( "USD" );
        Curr usd2 = Curr.getInstance( "USD" );

        assertNotNull( "not null 1", usd1 );
        assertNotNull( "not null 2", usd2 );
        assertSame( "same instances", usd1, usd2 );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testGetInstance_lowercase() {
        Curr.getInstance( "USd" );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testGetInstance_short() {
        Curr.getInstance( "US" );
    }


    @Test( expected = IllegalArgumentException.class )
    public void testGetInstance_long() {
        Curr.getInstance( "USSR" );
    }


    @Test
    public void testToString() {
        assertEquals( "USD", Curr.getInstance( "USD" ).toString());;
    }

}
