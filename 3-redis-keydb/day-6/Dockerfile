FROM eqalpha/keydb:latest

USER root

RUN apt-get update && \
    apt-get install -y iptables iputils-ping && \
    rm -rf /var/lib/apt/lists/*

CMD ["keydb-server", "/etc/keydb/keydb.conf"]
