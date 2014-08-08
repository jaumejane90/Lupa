#!/bin/bash
echo "Instaling Redis..."
echo "unzip...."
unzip unstable.zip
echo "cd redis-unstable"
cd redis-unstable
echo "make ..."
make
echo "make install ... "
sudo make install
echo "cd utils"
cd utils
#sudo apt-get -y install expect
#echo "install_server.sh"
echo "\n" | sudo ./install_server.sh
#sudo expect -c "spawn /home/storm/redis-unstable/utils/install_server.sh; send \"\n\"; send \"\n\";send \"\n\";send \"\n\";send \"\n\"; expect -re \"1\";"
sudo service redis_6379 start
#echo "Configure Redis listening for all the network interfaces ... "
#cd ..
#sed -i -r 's/^(bind 127\.0\.0\.1)/# \1/' redis.conf


