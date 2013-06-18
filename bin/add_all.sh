#!/bin/sh

wcfind --name \\.java src/ | xargs ./bin/add_license.sh LICENSE
