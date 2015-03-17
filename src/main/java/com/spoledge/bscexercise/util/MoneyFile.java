package com.spoledge.bscexercise.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spoledge.bscexercise.MoneyParseException;
import com.spoledge.bscexercise.MoneyParser;
import com.spoledge.bscexercise.model.Money;


/**
 * Utility class for fetching money data from a file.
 * It allows to easily fetch the data (incrementally) and also validate that 
 * all data in the file are ok.
 *
 * This class is not thread safe - only one thread and one action can be performed at the moment
 * (e.g. validation cannot be called during iteration).
 *
 * <pre>
 *  MoneyFile mf = new MoneyFile( ... );
 *  try {
 *      // optionally validate
 *      if (shouldValidate) mf.validate();
 *
 *      for (Iterator<Money> iter=mf.fetch(); iter.hasNext()) {
 *          Money money = iter.next();
 *          ...
 *      }
 *  }
 *  catch (Exception e) {
 *      ...
 *  }
 *  finally {
 *      mf.close();
 *  }
 * </pre>
 */
public class MoneyFile {

    ////////////////////////////////////////////////////////////////////////////
    // Inner
    ////////////////////////////////////////////////////////////////////////////

    /**
     * An iterator which pre-fetches one line ahead.
     * So any exceptions are found
     */
    class MoneyIterator implements Iterator<Money> {
        BufferedReader reader;
        boolean itemFetched;
        Money money;
        int lineNumber = 1;

        MoneyIterator( BufferedReader reader ) {
            this.reader = reader;
        }

        public boolean hasNext() {
            ensureFetched();

            return money != null;
        }

        public Money next() {
            ensureFetched();

            if (money == null) throw new java.util.NoSuchElementException();

            Money ret = money;
            money = null;
            itemFetched = false;

            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        void ensureFetched() {
            if (itemFetched) return;

            money = null;
            String line = null;

            try {
                line = reader.readLine();
                lineNumber++;
            }
            catch (IOException e) {
                log.error( "IOException while reading file '" + file + "' near line " + lineNumber, e );
            }

            itemFetched = true;

            if (line != null) {
                money = moneyParser.parseMoney( line, lineNumber );
            }
            else {
                close();
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    private MoneyParser moneyParser;
    private File file;
    private MoneyIterator currentIterator;

    private Object lock = new Object();

    protected Log log = LogFactory.getLog( getClass());


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new money file.
     * It just remembers the file, no resources are opened yet.
     */
    public MoneyFile( MoneyParser moneyParser, File file ) {
        if (moneyParser == null) throw new NullPointerException( "Missing money parser param" );
        if (file == null) throw new NullPointerException( "Missing file param" );

        this.moneyParser = moneyParser;
        this.file = file;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Validates that the file can be fetched without errors.
     */
    public void validate() throws IOException, MoneyParseException {
        int count = 0;
        boolean ok = false;

        try {
            for (Iterator<Money> iter = fetch(); iter.hasNext();) {
                iter.next();
                count++;
            }
            ok = true;
        }
        finally {
            if (ok) log.info( "Successfully validated " + count + " records in file '" + file + "'");
            else log.warn( "Validation error detected in file '" + file + "' near line " + (count+1));
        }
    }


    /**
     * Fetches the data incrementally.
     */
    public Iterator<Money> fetch() throws IOException {
        synchronized (lock) {
            if (currentIterator != null) throw new IllegalStateException( "Already fetching" );

            BufferedReader reader = null;
            FileInputStream fis = null;
            boolean ok = false;

            try {
                reader = new BufferedReader( new InputStreamReader( fis = new FileInputStream( file )));
                ok = true;
            }
            finally {
                if (!ok && fis != null) try { fis.close(); } catch (IOException e) {}
            }

            currentIterator = new MoneyIterator( reader );

            log.info( "File '" + file + "' has been opened." );

            return currentIterator;
        }
    }


    /**
     * Closes any opened resources.
     * This method can be called even if no file has been opened.
     * If called before the iterator finishes, then any call to the iterator may fail.
     */
    public void close() {
        synchronized( lock ) {
            if (currentIterator != null) {
                try { currentIterator.reader.close(); } catch (Exception e) {}

                currentIterator = null;

                log.info( "File '" + file + "' has been closed." );
            }
        }
    }

}

