#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done

SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo "$*"
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
noncase= false
case "`uname`" in
  Darwin*)
    darwin=true
    noncase= true
    ;;
  CYGWIN*)
    cygwin=true
    noncase= true
    ;;
  MING*)
    msys=true
    noncase= true
    ;;
esac

# For Cygwin, force commands to be in UNIX mode.
if $cygwin ; then
    [ -n "$CYGWIN" ] && cygwin=false
fi

# Attempt to set up a Gradle environment.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/bin/java" ] ; then
        java="$JAVA_HOME/jre/bin/java"
    else
        java="$JAVA_HOME/bin/java"
    fi
else
    java="java"
fi

##### WARNING #####
# Due to Cygwin issues, continue to use backward slash and quote vars.
# Do not "fix" this to forward slash.

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$noncase" != "true" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# Collect all arguments for the java command, escaping any quotation marks that we have.
# that would  otherwise break the quoting.
for arg in $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
           "-Dorg.gradle.appname=$APP_BASE_NAME" \
           -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
           org.gradle.wrapper.GradleWrapperMain "$@" ; do
    case "$arg" in
        *" "* )
            QUOTED_ARGS="$QUOTED_ARGS \"$arg\""
            ;;
        *)
            QUOTED_ARGS="$QUOTED_ARGS $arg"
            ;;
    esac
done

eval "$java $QUOTED_ARGS"
