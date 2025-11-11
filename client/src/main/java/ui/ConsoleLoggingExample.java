package ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

public class ConsoleLoggingExample {
    public static void main(String[] args) throws IOException {

        Logger logger = Logger.getLogger("myLogger");
        logger.config("main: " + String.join(", ", args));

    }

    private static void logSQL(String statement, Object... params) {
        var stringList = Arrays.stream(params).map(String::valueOf).toList();
        var paramList = String.join(", ", stringList);

        System.out.printf("- %s [SQL]: %s (%s)\n", new Date(), statement, paramList);
    }

}
