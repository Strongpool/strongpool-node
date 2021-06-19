# Strongpool Node

[![lint](https://github.com/Strongpool/strongpool-node/actions/workflows/lint.yml/badge.svg)](https://github.com/Strongpool/strongpool-node/actions/workflows/lint.yml)

## Rationale

Arweave differs from most block chains in providing a storage service. It is
expected that projects building on Arweave will want to run their own Arweave
nodes for additional assurance of access to their data and lower latency. The
Strongpool Node aims to make that as painless as possible by providing a safe,
ops friendly, batteries included, way to run Arweave nodes.

## Features

### Implemented

- User friendly node control interface
- Enhanced config validation
- Containerized Arweave server
- Server healthcheck
- Clear licensing

### Planned

- Caching reverse proxy
- Bundled gateway

## Getting Started

1. Clone the `stable` branch of this repo: `git clone -b stable --depth 1 https://github.com/Strongpool/strongpool-node.git`
2. If you have existing miner data, copy it into `data/` in the `strongpool-node` directory.
3. Copy `config-example.edn` to `config.edn` in the `stongpool-node` directory and edit it to set your mining address.
4. Start the miner by running `./spnctl start`.
