#!/bin/sh

LICENSE=$1
shift

for file in $*; do
  package=`head -1 $file | grep "package com.threerings"`
  if [ "$package" != "" ]; then
    (echo '0a'; cat $LICENSE; echo '.'; echo 'wq') | ed -s $file
  fi
done
