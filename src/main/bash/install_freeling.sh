#!/bin/bash
echo "Installing Freeling dependencys(libboost-regex-dev libicu-dev)"
sudo apt-get install -y libboost-regex-dev libicu-dev

echo "Installing Freeling dependencys( libboost-system-dev libboost-program-options-dev)"
sudo apt-get install -y libboost-system-dev libboost-program-options-dev

echo "Installing Freeling dependency(install libboost-filesystem-dev)"
sudo apt-get install -y libboost-filesystem-dev

echo "Installing Freeling dependency(zlib1g-dev)"
sudo apt-get install -y zlib1g-dev

echo "Installing Freeling dependency(libboost-thread-dev)"
sudo apt-get -y install libboost-thread-dev

#sudo apt-get install -y libboost-all-dev
 

echo "Downloading Freeling..."
echo "tar freeling"
tar xzvf 32
rm 32

echo "Applying patch to Freeling ... "
cd /home/storm/freeling-3.1/src/main/sample_analyzer
patch socket.h < ~/freeling-3.1.patch

echo "Installing Freeling ..."
echo "cd freeling-3.1"
cd /home/storm/freeling-3.1/
echo "./configure ..."
./configure
echo "make ... "
make
echo "make install ... "
sudo make install



