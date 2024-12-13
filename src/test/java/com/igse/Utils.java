package com.igse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class Utils {

   public static ListAppender<ILoggingEvent> setLogLevel(Logger logger,ListAppender<ILoggingEvent> listAppender){
       listAppender.start();
       logger.setLevel(Level.DEBUG);
       logger.addAppender(listAppender);
       return listAppender;
   }
}
