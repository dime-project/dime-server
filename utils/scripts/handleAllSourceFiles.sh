#!/bin/bash

pattern=$1
shift

option=$1
shift

for i in "$@" 
do
  java -jar /home/simon/projekte/SourceHelper/dist/SourceHelper.jar "$option" "$pattern" "$i"

done