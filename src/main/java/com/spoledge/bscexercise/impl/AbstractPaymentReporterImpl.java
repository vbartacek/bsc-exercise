package com.spoledge.bscexercise.impl;

import java.util.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spoledge.bscexercise.PaymentReporter;

import com.spoledge.bscexercise.model.Money;


/**
 * A parent of all PaymentReporter implementations.
 * Contains common reusable code.
 */
public abstract class AbstractPaymentReporterImpl implements PaymentReporter {

    /**
     * The default period.
     */
    public static final long DEFAULT_PERIOD = 60000L;


    /**
     * The last timer is used.
     * It is used for timer's thread names.
     */
    private static int lastTimerId = 0;


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    /**
     * The timer for automatic reporting.
     */
    protected Timer timer;

    /**
     * The automatic reporting period in ms.
     */
    protected long period = DEFAULT_PERIOD;

    /**
     * The flag for syncing timer start: seconds.
     */
    protected boolean syncSec;

    /**
     * The flag for syncing timer start: minutes.
     */
    protected boolean syncMin;

    /**
     * The flag for syncing timer start: hours.
     */
    protected boolean syncHour;

    /**
     * The logging object.
     */
    protected Log log = LogFactory.getLog( getClass());

    /**
     * The lock - we use our own lock to avoid any sync conflict with subclasses.
     */
    private Object lockTimer = new Object();


    ////////////////////////////////////////////////////////////////////////////
    // PaymentReporter
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Starts automatic payment reporting.
     * @param
     * @throws IllegalStateException when automatic payment reporting is already started
     */
    public void startPaymentReporting() {
        synchronized( lockTimer ) {
            if (timer != null) throw new IllegalStateException( "Already started" );

            timer = createTimer();

            log.info( "Automatic reporting has been started." );
        }
    }


    /**
     * Stops automatic payment reporting.
     * If the reporting was not started, then nothing happens.
     * Once the reporting is stopped, then it can be started again.
     */
    public void stopPaymentReporting() {
        synchronized( lockTimer ) {
            if (timer != null) {
                timer.cancel();
                timer = null;

                log.info( "Automatic reporting has been stopped." );
            }
        }
    }


    /**
     * Generates a new payment report.
     * This method can be called explicitly even when the automatic reporting is enabled.
     */
    public abstract void generatePaymentReport();


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    public long getPeriod() {
        return period;
    }

    /**
     * Sets the perios of automatic reporting in ms.
     */
    public void setPeriod( long period ) {
        this.period = period;
    }


    public boolean getSyncSec() {
        return syncSec;
    }

    /**
     * Sets the synchronization flag for start of the automatic reporting: seconds.
     * @param syncSec if true then milliseconds are cleared
     */
    public void setSyncSec( boolean syncSec ) {
        this.syncSec = syncSec;
    }


    public boolean getSyncMin() {
        return syncMin;
    }

    /**
     * Sets the synchronization flag for start of the automatic reporting: minutes.
     * @param syncMin if true then seconds are cleared
     */
    public void setSyncMin( boolean syncMin ) {
        this.syncMin = syncMin;
    }


    public boolean getSyncHour() {
        return syncHour;
    }

    /**
     * Sets the synchronization flag for start of the automatic reporting: hours.
     * @param syncHour if true then minutes are cleared
     */
    public void setSyncHour( boolean syncHour ) {
        this.syncHour = syncHour;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Protected
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new timer.
     */
    protected Timer createTimer() {
        Timer timer = new Timer( createTimerName());

        scheduleTimerTask( timer );

        return timer;
    }


    /**
     * Schedules timer task to the timer.
     * By default it schedules
     */
    protected void scheduleTimerTask( Timer timer ) {
        scheduleTimerTask( timer, period, syncSec, syncMin, syncHour );
    }


    /**
     * Schedules timer task to the timer.
     * By default it schedules
     */
    protected void scheduleTimerTask( Timer timer, long period,
                                        boolean syncSec, boolean syncMin, boolean syncHour ) {

        Date date = syncStartDate( new Date(), syncSec, syncMin, syncHour );

        timer.scheduleAtFixedRate(
            new TimerTask() {
                public void run() {
                    if (log.isDebugEnabled()) {
                        log.debug( "Task started." );
                    }

                    generatePaymentReport();
                }
            }, date, period );
    }


    /**
     * Creates a timer name.
     * By default the simple class name + order number is used.
     */
    protected String createTimerName() {
        synchronized( AbstractPaymentReporterImpl.class ) {
            return getClass().getSimpleName() + '-' + (++lastTimerId);
        }
    }


    /**
     * Calculates delay in ms if ms/min/hour should be synced.
     * E.g. if the start is "14:41:23,456", then values "14:41:24,000", "14:42:00,000" and "15:00:00,000"
     * are synchronized with seconds, minutes and hours respectively.
     */
    protected static Date syncStartDate( Date date, boolean syncSec, boolean syncMin, boolean syncHour ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );

        if (syncSec) {
            int ms = cal.get( Calendar.MILLISECOND );
            if (ms != 0) cal.add( Calendar.MILLISECOND, 1000 - ms );
        }

        if (syncMin) {
            int min = cal.get( Calendar.SECOND );
            if (min != 0) cal.add( Calendar.SECOND, 60 - min );
        }

        if (syncHour) {
            int hour = cal.get( Calendar.MINUTE );
            if (hour != 0) cal.add( Calendar.MINUTE, 60 - hour );
        }

        return cal.getTime();
    }


    /**
     * Sorts the list by the currency code.
     * NOTE: this method is destructive - it uses the Collections.sort() method !
     *
     * @param list the input list
     * @return the sorted list
     */
    protected static List<Money> sortByCurrencyCode( List<Money> list ) {
        if (list == null || list.size() < 2) return list;

        Collections.sort( list,
            new Comparator<Money>() {
                public int compare( Money m1, Money m2 ) {
                    return m1.getCurrency().getCurrencyCode().compareTo( m2.getCurrency().getCurrencyCode());
                }
            }
        );

        return list;
    }

}
