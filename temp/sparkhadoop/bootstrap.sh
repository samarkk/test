#!/bin/bash

# Update hosts file
echo "[TASK 1] Update /etc/hosts file"
cat >>/etc/hosts<<EOF
192.168.50.2 master.e4rlearning.com master
192.168.50.3 node1.e4rlearning.com node1
192.168.50.4 node2.e4rlearning.com node2
192.168.50.5 admin.e4rlearning.com admin
192.168.50.6 node3.e4rlearning.com node3
EOF
