# MicroC-feature
MicroC is a restricted version of C supported by [Why3](http://why3.lri.fr/) for proving program correctness.

This repository contains an XText grammar for Micro C, which supports the semantic annotations used to define predicates, theorems, invariants etc.

It also adds a `Open in Why3` item to the `Run As…` menu, and a `Generate MicroC2Isa` item to the `Acceleo Model to Text`menu.
The first items runs the why IDE on the C file. The second item exports the definitions and lemmas to an Isabelle theory file for proving lemmas that are not automatically proven by the SMT solvers.

There is also a plug-in trhat adds a `Open in Isabelle` item to the `Run As…` menu when an Isabelle theory file is selected.

License
==================

[Apache License Version 2.0, January 2004](http://www.apache.org/licenses/LICENSE-2.0)

By [Frédéric Boulanger](https://github.com/Frederic-Boulanger-UPS)
