#!/bin/bash

cat bin/stub.sh target/jtaches-$1-jar-with-dependencies.jar > jtaches-$1
chmod +x ./jtaches-$1
