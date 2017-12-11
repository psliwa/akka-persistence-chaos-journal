akka-persistence-chaos-journal
==============================

It is a plugin for akka-persistence that stores messages in memory with configurable failure rate. In may be used in persistence fault tolerance tests. The plugin is written on the top of [akka-persistence-inmemory][1].

Installation
============

The plugin is not yet released, it will be soon available in central maven repository.

Configuration
=============

```
akka.persistence {
    journal.plugin = "akka.persistence.chaos.journal"
    read-journal.plugin = "akka.persistence.chaos.read-journal"
    snapshot.plugin = "inmemory-snapshot-store"
}
```

Configuration details you can find in [reference.conf][2]

[1]: https://github.com/dnvriend/akka-persistence-inmemory
[2]: src/main/resources/reference.conf

