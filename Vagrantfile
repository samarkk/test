# -*- mode: ruby -*-
# vi: set ft=ruby :

# Avoid having the worker starting before the master node
ENV['VAGRANT_NO_PARALLEL'] = 'yes'

Vagrant.configure(2) do |config|

config.hostmanager.enabled = true
config.hostmanager.manage_guest = true
config.hostmanager.manage_host = true
config.hostmanager.ignore_private_ip = false
config.hostmanager.include_offline = true
#   config.vm.provision "shell", path: "bootstrap.sh"
# admin, client node
config.vm.define "admin" do |admin|
    admin.vm.box = "centoskafka_admin"
    admin.vm.hostname = "admin.e4rlearning.com"
    admin.hostmanager.aliases = "admin"
    admin.vm.network "private_network", ip: "192.168.50.5"
    admin.vm.network "private_network", ip: "192.168.33.2", virtualbox__intnet: true
    # do not need to link these files. pick them up from the shared /vagrant folder directly
    # admin.vm.provision "file", source:"kafka.client.truststore.jks", destination:"/home/vagrant/kafka.client.truststore.jks"
    # admin.vm.provision "file", source:"kafka.client.keystore.jks", destination:"/home/vagrant/kafka.client.keystore.jks"
    # admin.vm.provision "file", source:"client.properties", destination: "/home/vagrant/client.properties"
    # admin.vm.provision "file", source:"kafka_client_kerberos.properties", destination: "/home/vagrant/kafka_client_kerberos.properties"
    admin.vm.provider "virtualbox" do |v| 
        v.name = "admin"
        v.memory = 2048
        v.cpus = 2
        # v.gui = true
    end
    admin.vm.provision "shell", inline: "
    sudo systemctl start docker
    docker run -d -p 9001:9000  elkozmon/zoonavigator:latest
    docker run -d -p 9000:9000 -e ZK_HOSTS=192.168.50.2:2181 hlebalbau/kafka-manager:stable
    docker run -d -p 8001:8000 -e KAFKA_REST_PROXY_URL=192.168.50.2:8082 -e PROXY=true landoop/kafka-topics-ui
    docker run -d -p 8002:8000 -e SCHEMAREGISTRY_URL=http://192.168.50.2:8081 -e PROXY=true landoop/schema-registry-ui
    sudo systemctl start prometheus
    sudo systemctl start grafana-server
    ", run: "always"

    # admin.vm.network "forwarded_port", guest: 9000, host: 9000
    # admin.vm.network "forwarded_port", guest: 9001, host: 9001
    # admin.vm.network "forwarded_port", guest: 8000, host: 8000
    # admin.vm.network "forwarded_port", guest: 8001, host: 8001
    # admin.vm.network "forwarded_port", guest: 8002, host: 8002
    # admin.vm.network "forwarded_port", guest: 9090, host: 9090
    # admin.vm.network "forwarded_port", guest: 3000, host: 3000
    # admin.vm.network "forwarded_port", guest: 8000, host: 8000
  end
  # Master Node
  config.vm.define "master" do |master|
    master.vm.box = "centoskafka_kafka"
    #master.vm.synced_folder "../mnt/master", "/data-from-host"
	master.vm.hostname = "master.e4rlearning.com"
    master.hostmanager.aliases = "master"
    master.vm.network "private_network", ip: "192.168.50.2"
    master.vm.network "private_network", ip: "192.168.33.3", virtualbox__intnet: true
    # master.vm.network "forwarded_port", guest: 2181, host: 2181
    master.vm.provision "file", source: "server.properties.master.xzkauth", destination: "/home/vagrant/confluent/etc/kafka/server.properties"
    master.vm.provision "file", source: "server.properties.master.xzkauth", destination: "/home/vagrant/confluent/etc/kafka/server.properties.xzkauth"
    master.vm.provision "file", source: "server.properties.master.zkauth", destination: "/home/vagrant/confluent/etc/kafka/server.properties.zkauth"
    # get thse files below directly from shared folder /vagrant
    # master.vm.provision "file", source:"kafka.master.server.keystore.jks", destination:"/home/vagrant/kafka.master.server.keystore.jks"
    # master.vm.provision "file", source:"kafka.server.truststore.jks", destination:"/home/vagrant/kafka.server.truststore.jks"
    # master.vm.provision "file", source:"kafka.master.service.keytab", destination:"/home/vagrant/kafka.master.service.keytab"
    # master.vm.provision "file", source: "kafka_server_master_jaas.conf", destination: "/home/vagrant/kafka_server_master_jaas.conf"
    master.vm.provision "file", source: "cassandra.yaml", destination: "/home/vagrant/cassandra/conf/cassandra.yaml"

    master.vm.provider "virtualbox" do |v|
      v.name = "master"
      v.memory = 4096
      v.cpus = 2
    #   v.gui = true
    end
    # below not needed as the services are configured common across machines
    # master.vm.provision "shell", inline: "
    # sudo cp /vagrant/kafka.service.master /etc/systemd/system/kafka.service
    # "
    # master.vm.provision "shell", inline:"
    # sudo systemctl start zookeeper;", run: "always"

    master.vm.provision "shell", inline:"
    sudo systemctl start zookeeper;(while ! nc -z -v -w1 master.e4rlearning.com 2181 2>/dev/null; do echo Waiting for port 2181 on master.e4rlearning.com to open...; sleep 2; done); sleep 5;sudo systemctl start kafka",run:"always"
  end

  NodeCount = 3

  (1..NodeCount).each do |i|
    config.vm.define "node#{i}" do |worker|
      worker.vm.box = "centoskafka_kafka"      
	  worker.vm.hostname = "node#{i}.e4rlearning.com"
      worker.hostmanager.aliases = "node#{i}"
    #   worker.vm.synced_folder "../mnt/worker-#{i}", "/data-from-host"  
    if i < 3    
	  worker.vm.network "private_network", ip: "192.168.50.#{2 + i}"
    else
      worker.vm.network "private_network", ip: "192.168.50.#{3 + i}"
    end
    worker.vm.network "private_network", ip: "192.168.33.#{i+3}", virtualbox__intnet: true
    #  worker.vm.provision "file", source: "id_rsa.node#{i}", destination: "/home/vagrant/.ssh/id_rsa"
    worker.vm.provision "shell", path: "bootstrap_worker.sh"
    worker.vm.provision "file", source: "server.properties.node#{i}.xzkauth", destination: "/home/vagrant/confluent/etc/kafka/server.properties"
      worker.vm.provision "file", source: "server.properties.node#{i}.xzkauth", destination: "/home/vagrant/confluent/etc/kafka/server.properties.xzkauth"
      worker.vm.provision "file", source: "server.properties.node#{i}.zkauth", destination: "/home/vagrant/confluent/etc/kafka/server.properties.zkauth"
    # get these directly from /vagrant
    #    worker.vm.provision "file", source: "kafka.service.worker", destination: "/etc/systemd/system/kafka.service"
    #   worker.vm.provision "file", source: "kafka.node#{i}.server.keystore.jks", destination: "/home/vagrant/kafka.node#{i}.server.keystore.jks"
    #   worker.vm.provision "file", source: "kafka.server.truststore.jks", destination: "/home/vagrant/kafka.server.truststore.jks"
	#   worker.vm.provision "file", source: "kafka.node#{i}.service.keytab", destination: "/home/vagrant/kafka.node#{i}.service.keytab"
    #   worker.vm.provision "file", source: "kafka_server_node#{i}_jaas.conf", destination: "/home/vagrant/kafka_server_node#{i}_jaas.conf"
      worker.vm.provision "file", source: "cassandra#{i+1}.yaml", destination: "/home/vagrant/cassandra/conf/cassandra.yaml"
      worker.vm.provider "virtualbox" do |v|
        v.name = "node#{i}"
        v.memory = 1024
        v.cpus = 1
        # v.gui = true
      end
   
    # not needed services configured
    # worker.vm.provision "shell", inline: "sudo cp /vagrant/kafka.service.node#{i} /etc/systemd/system/kafka.service"
    worker.vm.provision "shell", inline: "
    sudo systemctl start kafka", run: "always"
    end
  end

end