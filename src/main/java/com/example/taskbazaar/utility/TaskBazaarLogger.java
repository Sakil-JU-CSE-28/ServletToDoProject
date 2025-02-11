package com.example.taskbazaar.utility;

import java.util.logging.Logger;

public class TaskBazaarLogger {
    private static Logger logger = Logger.getLogger(TaskBazaarLogger.class.getName());

    private TaskBazaarLogger() {
        logger.info("TaskBazaarLogger created");
    }
    public static Logger getLogger() {
        return logger;
    }

}
