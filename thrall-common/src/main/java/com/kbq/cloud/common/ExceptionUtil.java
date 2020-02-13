package com.kbq.cloud.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    private ExceptionUtil(){
    }

    public static String getExceptionMessage(Throwable e) {
        return e.getMessage();
    }

    public static String getExceptionSprintStackTrace(Throwable e) {
        try (StringWriter sw = new StringWriter();
              PrintWriter pw = new PrintWriter(sw)){
            e.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }

    }
}
