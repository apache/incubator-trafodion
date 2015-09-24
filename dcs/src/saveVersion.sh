#!/bin/sh

# This file is used to generate the annotation of package info that
# records the user, url, revision and timestamp.

#
# (C) Copyright 2013 Hewlett-Packard Development Company, L.P.
# 
#   Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
# 
#       http://www.apache.org/licenses/LICENSE-2.0
#  
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

unset LANG
unset LC_CTYPE
version=$1
outputDirectory=$2
user=`whoami`
date=`date`
cwd=`pwd`
if [ -d ../../.git ]; then
   revision=`git log -1 --pretty=format:"%H"`
   hostname=`hostname`
   url="git://${hostname}${cwd}"
else
  revision="Unknown"
  url="file://$cwd"
fi

mkdir -p "$outputDirectory/org.trafodion.dcs"
cat >"$outputDirectory/org.trafodion.dcs/package-info.java" <<EOF
/*
 * Generated by src/saveVersion.sh
 */
@VersionAnnotation(version="$version", revision="$revision",
                         user="$user", date="$date", url="$url")
package org.trafodion.dcs;
EOF
