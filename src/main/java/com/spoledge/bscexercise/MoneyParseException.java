package com.spoledge.bscexercise;

/**
 * This exception is thrown when parsing Money.
 */
public class MoneyParseException extends RuntimeException {

    private int lineNumber;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new exception.
     */
    public MoneyParseException( String message ) {
        this( message, -1 );
    }


    /**
     * Creates a new exception.
     */
    public MoneyParseException( String message, int lineNumber ) {
        super( message );

        this.lineNumber = lineNumber;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the line number or -1 if not applicable.
     */
    public int getLineNumber() {
        return lineNumber;
    }


    @Override
    public String getMessage() {
        String ret = super.getMessage();

        if (lineNumber != -1) ret += " at line " + lineNumber;

        return ret;
    }

}
