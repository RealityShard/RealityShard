#!/bin/sh

##
# For copyright information see the LICENSE document.
# Belongs to the RealityShard project.
##

# This can be used to install Reality:Shard.
# ...ON A LINUX / UNIX SYSTEM!
#
# To use this framework, you will need to have the following tools installed:
# - Java 7 (JRE/JDK 1.7)
# - Maven 3


DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


clear
echo "-----------------------------------------------------------------------------"
echo "< RealityShard Install >"
echo "-----------------------------------------------------------------------------"
echo "The script was started with the following settings:"
echo ""
echo "Installation-path:"
echo "  $DIR"
echo ""
echo "Hints:"
echo "If your running maven for the first time with any projects, it may take"
echo "a while to download the additional dependencies needed for this project."
echo "-----------------------------------------------------------------------------"
echo ""
read -p "[Press any key to continue]" -n 1 -s
echo ""
echo ""


echo "Checking installed tools..."
command -v mvn >/dev/null 2>&1 || { echo >&2 "Failed to find maven. Did you install it correctly?"; exit 1; }
echo ""


echo "Installing the framework..."
read -p "[Press any key to continue]" -n 1 -s
echo ""
mvn clean install
echo ""


echo "-----------------------------------------------------------------------------"
echo "Reality:Shard Installation done."
echo ""
echo "If there were any errors during init/update/install of the this,"
echo "please report them on https://github.com/RealityShard/RealityShard ."
echo ""
echo "You can now use RealityShard like any other maven dependency in your project."
echo "-----------------------------------------------------------------------------"
