#!/bin/sh
#
# This script sets required LOCALCLASSPATH and by default CLASSPATH 
# if no arguments.Otherwise use "set" option to set CLASSPATH
# and use "quiet" option to suppress prinitng of messages
# It must be run by source its content to modify current environment
#    . classpath.sh [build|run] [set] [quiet]
#

LOCALCLASSPATH=.
if [ ! "`echo lib/junit/*.jar`" = "lib/junit/*.jar" ] ; then
    LOCALCLASSPATH=`echo lib/junit/*.jar | tr ' ' ':'`:$LOCALCLASSPATH
fi
if [ "$1" = "build" ] ; then 
    LOCALCLASSPATH=`echo lib/ant/*.jar | tr ' ' ':'`:$LOCALCLASSPATH
    LOCALCLASSPATH=$JAVA_HOME/lib/tools.jar:$LOCALCLASSPATH
    if [ "$2" = "set" ] ; then
        CLASSPATH=$LOCALCLASSPATH
        if [ ! "$3" = "quiet" ] ; then
            echo $LOCALCLASSPATH
        fi
    elif [ ! "$2" = "quiet" ] ; then
        echo $LOCALCLASSPATH
    fi
else 
    LOCALCLASSPATH=build/samples:build/classes:build/tests:$LOCALCLASSPATH
    if [ ! "`echo lib/impl_xmlpull_v1_api/*.jar`" = "lib/impl_xmlpull_v1_api/*.jar" ] ; then
       LOCALCLASSPATH=`echo lib/impl_xmlpull_v1_api/*.jar | tr ' ' ':'`:$LOCALCLASSPATH
    fi
    if [ "$1" = "run" ] ; then
        if [ "$2" = "set" ] ; then
            CLASSPATH=$LOCALCLASSPATH
            if [ ! "$3" = "quiet" ] ; then
                echo $LOCALCLASSPATH
            fi
        elif [ ! "$2" = "quiet" ] ; then
            echo $LOCALCLASSPATH
        fi
    else 
        CLASSPATH=$LOCALCLASSPATH
        if [ ! "$1" = "quiet" ] ; then
            echo $CLASSPATH
        fi
    fi
fi
export CLASSPATH

