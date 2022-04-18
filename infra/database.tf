# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

resource "digitalocean_database_cluster" "db-cluster-bouncer" {
  name                 = "bouncer-db-cluster-${local.region}"
  engine               = "pg"
  version              = "14"
  size                 = "db-s-1vcpu-1gb"
  region               = local.region
  node_count           = 1
  private_network_uuid = local.vpc_uuid
}

resource "digitalocean_database_db" "db-bouncer" {
  cluster_id = digitalocean_database_cluster.db-cluster-bouncer.id
  name       = "bouncer"
}

resource "digitalocean_database_firewall" "db-cluster-bouncer-fw" {
  cluster_id = digitalocean_database_cluster.db-cluster-bouncer.id

  rule {
    type  = "app"
    value = digitalocean_app.bouncer-app.id
  }
}

resource "digitalocean_database_user" "db-user-bouncer" {
  cluster_id = digitalocean_database_cluster.db-cluster-bouncer.id
  name       = "bouncer-service"
}