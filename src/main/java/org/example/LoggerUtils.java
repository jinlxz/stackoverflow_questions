package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: xialiu8b
 * Date: 1/30/2023
 * Time: 11:44 AM
 */
public class LoggerUtils {
    private static Logger logger=LogManager.getLogger(LoggerUtils.class);
    public static void logMethodTime(String... msg){
        String callerInfo="[time] "+getCaller();

        if(msg!=null){
            callerInfo+=" => "+String.join(",",msg);
        }
//        System.out.println(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+" "+callerInfo);
        logger.info(callerInfo);
    }
    private static String getCaller(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[3];
        return stackTraceElement.getClassName()+"#"+ stackTraceElement.getMethodName() +":"+ stackTraceElement.getLineNumber();
    }
}
