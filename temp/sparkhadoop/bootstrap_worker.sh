#!/bin/bash

# Join worker nodes to the Kubernetes cluster
# sudo cp /vagrant/kafka.service.worker /etc/systemd/system/kafka.service
sudo systemctl disable mysqld
sudo systemctl disable docker
sudo systemctl disable zookeeper
