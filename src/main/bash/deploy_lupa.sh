#!/bin/bash
PR_KEY=$1
IP_NIMBUS=$2
TOPOLOGY=$3
IP_SUPERVISOR_1=$4
IP_SUPERVISOR_2=$5
INSTALL_REDIS=$6
INSTALL_FREELING=$7
FREELING_PATCH=$8
NAMEDEPLOY=$9



#Install Redis to Supervisor 1
scp -i $PR_KEY $INSTALL_REDIS storm@$IP_SUPERVISOR_1:~/. 
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 wget https://github.com/antirez/redis/archive/unstable.zip
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 ./install_redis.sh

# # # #Install Freeling both supervisors
# scp -i $PR_KEY $INSTALL_FREELING storm@$IP_SUPERVISOR_1:~/. 
# scp -i $PR_KEY $FREELING_PATCH storm@$IP_SUPERVISOR_1:~/. 
# ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 wget http://devel.cpl.upc.edu/freeling/downloads/32
# ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 ./install_freeling.sh
# ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 analyze --flush -f en.cfg --server --port 5050 &


scp -i $PR_KEY $INSTALL_FREELING storm@$IP_SUPERVISOR_2:~/.
scp -i $PR_KEY $FREELING_PATCH storm@$IP_SUPERVISOR_2:~/. 
ssh -i $PR_KEY storm@$IP_SUPERVISOR_2 wget http://devel.cpl.upc.edu/freeling/downloads/32
ssh -i $PR_KEY storm@$IP_SUPERVISOR_2 ./install_freeling.sh
ssh -i $PR_KEY storm@$IP_SUPERVISOR_2 analyze --flush -f en.cfg --server --port 5050 &



# # #Upload topology to nimbus
scp -i $PR_KEY $TOPOLOGY  storm@$IP_NIMBUS:~/. 
#ssh -i $PR_KEY storm@$IP_NIMBUS unzip Lupa.zip

## Install htop
ssh -i $PR_KEY storm@$IP_NIMBUS sudo apt-get install -y htop
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 sudo apt-get install -y htop
ssh -i $PR_KEY storm@$IP_SUPERVISOR_2 sudo apt-get install -y htop
# #Open securty groups for redis/freeling/ui
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 wget https://bitbucket.org/pypa/setuptools/raw/bootstrap/ez_setup.pywget https://bitbucket.org/pypa/setuptools/raw/bootstrap/ez_setup.py
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 sudo python ez_setup.py
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 sudo easy_install pip
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 sudo easy_install awscli
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 mkdir .aws
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 'echo "[default] 
aws_access_key_id = AKIAJGJ4LRG46FYG3POA
aws_secret_access_key = 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 
region = us-west-2" > .aws/config'
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 sudo aws ec2 authorize-security-group-ingress --group-name jclouds#supervisor-$NAMEDEPLOY --protocol tcp --port 6379 --cidr 0.0.0.0/0
ssh -i $PR_KEY storm@$IP_SUPERVISOR_1 sudo aws ec2 authorize-security-group-ingress --group-name jclouds#supervisor-$NAMEDEPLOY --protocol tcp --port 5050 --cidr 0.0.0.0/0
ssh -i $PR_KEY -L 9999:localhost:6379 storm@$IP_SUPERVISOR_1  # al localhost 9999 tindrem el redis 6379 del user@IP -> http://momolog.info/2011/12/02/connect-to-redis-via-ssh-tunneling/

