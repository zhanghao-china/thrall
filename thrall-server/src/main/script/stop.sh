#!/bin/bash
SHELL_FOLDER=$(dirname $(readlink -f "$0"))
exec sh $SHELL_FOLDER/sbm.sh stop
