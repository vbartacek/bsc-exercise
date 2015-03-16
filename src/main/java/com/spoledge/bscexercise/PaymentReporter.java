package com.spoledge.bscexercise;

/**
 * An abstraction of the payment reporter.
 * The implementation will typically start a new thread for generating the reports.
 * <pre>
 *  PaymentReporter reporter = ...;
 *  reporter.startPaymentReporting();
 *  ...
 *  if (userWantsExtraReport) {
 *      reporter.generatePaymentReport();
 *  }
 *  ...
 *  reporter.stopPaymentReporting();
 * </pre>
 */
public interface PaymentReporter  {

    /**
     * Starts automatic payment reporting.
     * @throws IllegalStateException when automatic payment reporting is already started
     */
    public void startPaymentReporting();

    /**
     * Stops automatic payment reporting.
     * If the reporting was not started, then nothing happens.
     * Once the reporting is stopped, then it can be started again.
     */
    public void stopPaymentReporting();

    /**
     * Generates a new payment report.
     * This method can be called explicitly even when the automatic reporting is enabled.
     */
    public void generatePaymentReport();

}
