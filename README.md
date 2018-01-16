akka-persistence-chaos-journal [![Build Status](https://travis-ci.org/psliwa/akka-persistence-chaos-journal.svg?branch=master)](https://travis-ci.org/psliwa/akka-persistence-chaos-journal)
==============================

It is a plugin for akka-persistence that stores messages in memory with configurable failure rate. In may be used in persistence fault tolerance tests. The plugin is written on the top of [akka-persistence-inmemory][1].

Installation
============

```
libraryDependencies += "org.psliwa" %% "akka-persistence-chaos-journal" % "2.5.1.1.0"
```

How to use?
===========

The exemplary project with the usage of the plugin for tests is [here][3].

Add in yours `application.conf`:

```
akka.persistence {
    journal.plugin = "akka.persistence.chaos.journal"
    read-journal.plugin = "akka.persistence.chaos.read-journal"
    snapshot.plugin = "inmemory-snapshot-store"
}
```

Configuration details you can find in [reference.conf][2].

[1]: https://github.com/dnvriend/akka-persistence-inmemory
[2]: akka-persistence-chaos-journal/src/main/resources/reference.conf
[3]: example/

