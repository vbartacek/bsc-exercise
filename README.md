# bsc-exercise
An exercise


## Requirements

* Java Development Kit (JDK) version 6 or higher
* Apache Maven 3
* Command line :)


## How to build the project

1. Using command line do: `mvn package`
2. The directory `target/dist` contains the distribution,
    you can copy or move it to elsewhere and run it from there


## Running

### Quick Start

1. Change the directory to the distribution directory: `cd target/dist`
2. Start the java executable: `java -jar bsc-exercise-1.0.jar`


### Command line starting options

There are several extra options which can be passed to the application
during the start. To see the usage / help message, then just do:

```
$ java -jar bsc-exercise-1.0.jar -?
usage: java -jar bsc-exercise-1.0.jar [OPTIONS] [FILE1 [FILE2...]]
BSC-Example - payment tracker.

 OPTIONS:
  -d, --decimal NUMBER         max number of decimal points (default=2)
  -D, --directory DIR          parent directory used for relative paths (input)
  -p, --period SECONDS         the reporting period, default is 60 seconds.
  -S, --sync-period {s|m|h}    sync reporting with clock's seconds|minutes|hours
  -?, --help                   prints this help and exits
      --usage
  -V, --version                prints the version and exits
```

So if you want to load a sample file (included in the repo as well) and be able
to load the files from the samples directory also directly from the application,
then start it as:

`java -jar bsc-exercise-1.0.jar -D ../../src/test/samples example.txt`


### Runtime commands

When the application is started, then in-app console is shown with the following
message:

```
=======================================================
BSC Exercise
Version 1.0
=======================================================
>> Hello! Type 'help' for help.

```

The console is expecting an input command or a payment to be recorded.
Type `help` to see the allowed commands:

```
help
>> Please enter a payment (CurrencyCode Value) or a command:
>>     quit [delay_in_sec]  - exits the app
>>     report               - prints the report immediatelly
>>     file F1 [F2 [F3...]] - loads payments from file(s)
>>     help                 - prints this info
```

An example of (CurrencyCode Value) pair is `USD 100` - try to type it:

```
USD 100
>> Registered payment USD 100 as tx[1]
```

The application just registered your first transaction. You can also specify
money amount including decimal point, but the application checks that number
of decimal positions is not greater than the maximum specified on startup
(option `-d, --decimal NUMBER` which is 2 by default).
So if you type `USD 0.01` or `USD 0.01000000`, then it is ok,
because the number of **effective** decimal positions is not greater than 2.
But the following payment will not be registered:

```
USD 0.001
>> ERROR - invalid payment format: Too many decimal points: 0.001
```

Sometimes you can see an automatic report on the screen:

```
---- Report after tx[3] ----
USD 100.02
----------------------------
```

The period of printing the report can be configured by the startup option
`-p, --period SECONDS` - the default value is 60 (seconds).
You can also force syncing with the system clock - e.g. when the report
period is one minute, then syncing minutes with the clock means that
the report will be printed just after a system clock's minute is changed:

```
java -jar bsc-exercise-1.0.jar -p 60 -S m
```

If you do not want to wait for the next automatic report,
then just type 'report'.

If you want to load a file or more containing the transactions
(same functionality as loading the files on startup), the just type
'file FILENAME'. You can specify more than one file at a moment.
If you passed the `-D, --directory DIR` parameter on startup,
then you can just specify a relative filepath now.

Example:

```
$ java -jar bsc-exercise-1.0.jar -D ../../src/test/samples
=======================================================
BSC Exercise
Version 1.0
=======================================================
>> Hello! Type 'help' for help.

file example.txt decimals.txt wrong.txt
>> Loading file ../../src/test/samples/example.txt...
>> File ../../src/test/samples/example.txt successfully loaded (5 lines).
>> Loading file ../../src/test/samples/decimals.txt...
>> File ../../src/test/samples/decimals.txt successfully loaded (5 lines).
>> Loading file ../../src/test/samples/wrong.txt...
>> ERROR Loading file ../../src/test/samples/wrong.txt, aborting: com.spoledge.bscexercise.MoneyParseException: Invalid currency code 'USSR' at line 4
```

Finally if you want to quit (yes, CTRL-C should be working, but let
the application do a graceful quit), then just type `quit` and
the app will quit immediatelly. Or you can schedule a quit - passing
a number of seconds after which the app will exit:

```
quit 60
>> Delayed quit scheduled
...(60 seconds delay)...
>> Bye!
```

## Implementation Notes

### Assumptions

1. Model of Money - own Currency class (Curr).
    As the requirement says that _any uppercase 3-letter code_
    is a valid  currency, then we had to leave the standard
    java.util.Currency class and provide a new one.
    Although the standard class would checks for _real_
    currency codes automatically, this would be against
    the requirement.

2. Model of Money - decimal point.
    We decided to allow decimal values as amount of money.
    For this purpose java.math.BigDecimal was chosen
    because it provides almost any necessary functionality.
    We also decided to canonize the output to certain number
    of decimal positions and also to limit the number of them
    on input. This lead to interesting problems and solutions.

3. In-App console - non-blocking reading.
    Although we could do simple `System.exit( 0 )` when user
    types _quit_, we decided to implement a solution which
    could be operated on systems without possibility
    to stop the entire JVM. This lead to non-blocking reading
    of the standard input 
    (see http://www.javaspecialists.eu/archive/Issue153.html).
    For confirmation of this functionality we added
    the delayed quit commad: `quit <number_of_seconds>`

4. Registering of payments / money.
    We added _transaction_ -like functionality to ensure
    that user will know if the payment was successfully
    registered or not. So when automatic report shows the 
    final balance, it also shows the last _transaction_
    id pointing to the last payment successfuly registered
    and taking into the current report's account.

5. Parsing of files - validation first.
    When we load any file (during the start or in-app),
    the we first try to parse all lines - we call it
    validating. If any of the lines is invalid (incorrect
    currency or amount or entire format), then no
    line is imported at all.
    Only when the first validation phase is without errors,
    a second phase is starting: the loading if payments
    itself.
    We are aware of the situation when the underlying
    file would be changed between these two phases,
    so it could lead to partial import of the file.
    But we preferred this solution because it is
    memory-saving (assuming large files) and we do not
    expect changing the files during time.


### Differences between the requirements and implementation

We found these differences which are basically supersets of the
original requirements:

* The application is able to load more than one file during the start
* The application understand more commands than only the `quit`


### Application Components

```
 +------------------+       +-----------------+
 | PaymentProcessor |------>| PaymentReporter |
 +------------------+       +-----------------+
                  ^          ^
                  |          |
                 +------------+      +-------------+
                 + Controller + <--> | MoneyParser |
                 +------------+      +-------------+
                  ^          ^
                  |          |
                  |         +~~~~~~~~~~~+
                  |         | MoneyFile |
                  |         +~~~~~~~~~~~+
                  |          ^
                  |          |
            +=======+       +============+
              INPUT           FILESYSTEM
            +=======+       +============+
```

## Known Issues

1. The output of the automatic balances reporting can be mixed with 
    the commands being typed to the in-app console. This could be
    solved by reading the input characters one by one and repeating
    them after the report is printed.

2. The in-app non-blocking console is working in Windows environment
    except of echoing the characters immediatelly. The characters
    are echoed only when Enter is pressed.

