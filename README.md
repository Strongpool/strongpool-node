# Strongpool Node

The Strongpool node is a user and operationally friendly bundling of the
[Arweave server](https://github.com/ArweaveTeam/arweave) and supporting
services.

[![Build](https://github.com/Strongpool/strongpool-node/actions/workflows/build.yml/badge.svg)](https://github.com/Strongpool/strongpool-node/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/Strongpool/strongpool-node/branch/main/graph/badge.svg?token=J4W4BGZ6KK)](https://codecov.io/gh/Strongpool/strongpool-node)

## Status

**Alpha**

It is currently in use by several miners and should not cause any harm to your
data (backups still recommended). However, it provides minimal functionality
beyond a marginally better getting started experience. It is being made
publicly available to get early feedback from those interested in its future
development.

## Current Features

- User friendly node control interface
- Improved config validation
- Containerized Arweave server
- Server healthcheck

## Prerequisites

- A computer or virtual machine with Linux installed. [Ubuntu
  20.04](https://ubuntu.com/download/server) is recommended, but other
  distributions should also work.
- Free disk space for Arweave data. For mining, you will want at least 6TB free.
- Docker installed. On Ubuntu 20.04, run `sudo apt install docker.io` to install
  it. For other distributions, follow the official Docker [install
  instructions](https://docs.docker.com/engine/install/).

## Getting Started

1. Clone the `main` branch of this repo: `git clone -b main https://github.com/Strongpool/strongpool-node.git`
2. Change into the `strongpool-node` directory: `cd strongpool-node`
3. If you have existing miner data, copy it into `data/` in the
   `strongpool-node` directory (created by the `git clone` above).
4. Copy `config/strongpool-example.edn` to `config/strongpool.edn` in the
   `stongpool-node` directory: `cp config/strongpool-example.edn config/strongpool.edn`
5. Edit the config file (`config/strongpool.edn`) and set your miner address
   (e.g. with nano: `nano config/strongpool.edn`). We recommend using
   [ArConnect](https://arconnect.io/) to create a wallet if you don't already
   have one.
6. Copy your mining wallet key into `wallets/arweave` with a file name matching
   your mining address (e.g.
   `wallets/arweave/V6iZkMkNEqfEYYiXdqS0AkvPNbAV8cfj6FOAwPl2vUU.json`). Note:
   the name of the file must exactly match the miner address you set in the
   config file (with a `.json` extension appended).
7. Ensure your user has beed added to the Docker group: `usermod -aG docker $USER`
8. Start the node by running `./spnctl start`.

## Upgrading

1. Change into the strongpool-node directory: `cd strongpool-node`
2. Stop the strongpool-node if it's running: `./spnctl stop`
3. Pull the latest update: `git pull`
4. Start the strongpool-node: `./spnctl start`

## Usage

### Start the node

```
$ ./spnctl start
Starting Strongpool node...
Strongpool node started.
```

### Stop the node

```
./spnctl stop
Stopping Strongpool node...
Strongpool node stopped.
```

### Show logs

```
./spnctl logs
arweave_1  | Launching Erlang Virtual Machine...
arweave_1  | Exec: /arweave/erts-11.1.4/bin/erlexec -noinput +Bd -boot /arweave/releases/2.4.2.0/start -mode embedded -boot_var SYSTEM_LIB_DIR /arweave/lib -config /arweave/releases/2.4.2.0/sys.config -args_file /arweave/releases/2.4.2.0/vm.args -- foreground +Ktrue +A20 +SDio20 +sbwtvery_long +sbwtdcpuvery_long +sbwtdiovery_long +swtvery_low +swtdcpuvery_low +swtdiovery_low +Bi -run ar main data_dir /data mine mining_addr ...
```

## Rationale

[Arweave](https://www.arweave.org/) differs from other crypto currencies in
providing a permanent immutable storage service in addition to a token that can
be used for transactions and as a store of value. As such, those building on
Arweave are likely to care both about the security of the token and access to
the data in the network.  While adding one or two nodes to the Arweave network
adds only marginal additional hashing power to secure the token, it can make a
large difference in how quickly and reliably data can be accessed for a given
purpose or at a particular location. Given this dynamic, we expect many Arweave
developers will either run their own nodes or use a service or protocol that
guarantees nodes with a given set of properties are running (e.g. in a
particular geographic location and having copies of certain parts of the
Permaweb). By providing a user and operationally friendly bundling of the
Arweave server and supporting services, the Strongpool node seeks to become the
preferred way for those developers and others interested in Arweave as a
permanent storage system to run Arweave nodes.
