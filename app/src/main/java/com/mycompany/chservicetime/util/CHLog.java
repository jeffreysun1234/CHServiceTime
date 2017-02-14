/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.chservicetime.util;

import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Logging class used for all logging of this application.
 */
public class CHLog {
    private static final String LOG_PREFIX = "CH_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;
    private static final boolean isSaveLog = false; // if save logs to SDCard.

    private static final String TAG = makeLogTag("Default");

    private static Logger logger;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    /**
     * Simple logging interface for log messages
     *
     * @see #setLogger(Logger)
     */
    public interface Logger {

        /**
         * @param level one of {@link Log#VERBOSE}, {@link Log#DEBUG},{@link Log#INFO},
         *              {@link Log#WARN},{@link Log#ERROR}
         * @param tag   log tag, caller
         * @param msg   message to log
         */
        void log(final int level, final String tag, final String msg);
    }

    /**
     * predefined logger using {@link Log} to print into Logcat with the default tag.
     *
     * @see #setLogger(Logger)
     */
    public static Logger LOGCAT = new Logger() {
        @Override
        public void log(final int level, final String tag, final String msg) {
            Log.println(level, TAG, tag + ": " + msg);
        }
    };

    /**
     * predefined logger using {@link Log} to print to the console with default tag.
     *
     * @see #setLogger(Logger)
     */
    public static Logger TESTOUT = new Logger() {
        @Override
        public void log(final int level, final String tag, final String msg) {
            System.out.println(tag + "::" + msg);
        }
    };

    /**
     * Log a verbose message with logy.
     */
    public static void v(final String tag, String msg) {
        if (logger != null) {
            if (msg == null)
                msg = "\"NULL\"";
            logger.log(Log.VERBOSE, tag, msg);
        }
    }

    /**
     * Log a debug message with logy.
     */
    public static void d(final String tag, String msg) {
        if (logger != null) {
            if (msg == null)
                msg = "\"NULL\"";
            logger.log(Log.DEBUG, tag, msg);
        }
    }

    /**
     * Log an info message with logy.
     */
    public static void i(final String tag, String msg) {
        if (logger != null) {
            if (msg == null)
                msg = "\"NULL\"";
            logger.log(Log.INFO, tag, msg);
        }
    }

    /**
     * Log a warning message with logy.
     */
    public static void w(final String tag, String msg) {
        if (logger != null) {
            if (msg == null)
                msg = "\"NULL\"";
            logger.log(Log.WARN, tag, msg);
        }
    }

    /**
     * Log an error message with logy.
     */
    public static void e(final String tag, String msg) {
        if (logger != null) {
            if (msg == null)
                msg = "\"NULL\"";
            logger.log(Log.ERROR, tag, msg);
        }
    }

    /**
     * Log an debug message with class name and method name.
     */
    public static void d(String msg) {
        if (logger != null) {
            CHLog.d(getCurrentClassName(), getCurrentMethodName() + "(Line:" + getLineNumber() + "): " + msg);
        }
    }

    /**
     * Log an debug message.
     */
    private static void log(final String tag, String msg) {
        CHLog.d(tag, msg);
    }

    /**
     * Log an josn message with structural format with logy.
     */
    public static void json(final String tag, Object source) {
        if (logger != null) {
            Object o = getJsonObjFromStr(source);
            if (o != null) {
                try {
                    if (o instanceof JSONObject) {
                        format(tag, ((JSONObject) o).toString(2));
                    } else if (o instanceof JSONArray) {
                        format(tag, ((JSONArray) o).toString(2));
                    } else {
                        format(tag, source);
                    }
                } catch (JSONException e) {
                    format(tag, source);
                }
            } else {
                format(tag, source);
            }
        }
    }

    /**
     * Log an array message with structural format with logy.
     */
    public static void array(final String tag, Object[] source) {
        if (logger != null) {
            log(tag, source.toString());
        }
    }

    private static String getSplitter(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append("-");
        }
        return builder.toString();
    }

    private static void format(String tag, Object source) {
        tag = " " + tag + " ";
        log(" ", " ");
        log(" ", getSplitter(50) + tag + getSplitter(50));
        log(" ", "" + source);
        log(" ", getSplitter(100 + tag.length()));
        log(" ", " ");
    }

    private static StackTraceElement getCallerStackTraceElement() {
        // Change the level of StackTrace based on the code structure. ( methods invoke )
        return Thread.currentThread().getStackTrace()[5];
    }

    private static String getCurrentMethodName() {
        return getCallerStackTraceElement().getMethodName();
    }

    private static int getLineNumber() {
        return getCallerStackTraceElement().getLineNumber();
    }

    private static String getCurrentClassName() {
        String className = getCallerStackTraceElement().getClassName();
        String[] temp = className.split("[\\.]");
        className = temp[temp.length - 1];
        return className;
    }

    private static Object getJsonObjFromStr(Object test) {
        Object o = null;
        try {
            o = new JSONObject(test.toString());
        } catch (JSONException ex) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    o = new JSONArray(test);
                }
            } catch (JSONException ex1) {
                return null;
            }
        }
        return o;
    }

    private CHLog() {
        throw new AssertionError("no instances");
    }

    /**
     * set a custom logger, {@code null} to disable logging
     * <p>
     * <p>
     * Use the default logcat logger for Android:
     * <code>
     * <pre>
     * CHLog.setLogger(CHLog.LOGCAT);
     * </pre>
     * </code>
     * <p>
     * Combine it with Timber:<br>
     * <p>
     * <code>
     * <pre>
     * CHLog.setLogger(new CHLog.Logger() {
     *    &#64;Override
     *    public void log(final int level, final String tag, final String msg) {
     *        Timber.tag(tag).log(level, msg);
     *    }
     * });
     * </pre>
     * </code>
     * <p>
     * <p>
     * Use the TestOut logger for Unit Test. Print messages to teh console:
     * <code>
     * <pre>
     * CHLog.setLogger(CHLog.TESTOUT);
     * </pre>
     * </code>
     */
    public static void setLogger(@Nullable final Logger logger) {
        CHLog.logger = logger;
    }
}
