## Overview
    Project:       Reality:Shard main source code repo
    Info:          Aims to create a simple server-platform for online-games and the like.
    Languages:     Java
    Organization:  RealityShard

**So what is that whole thing now?**

Reality:Shard is a generic online server that is based on the design of the `javax.servlet` framework, but is not compatible with it. It was created to be a basis for mmo game-servers and their needs, yet at the same time providing the possibility to interface with other network clients (such as HTTP based ones). In fact, you can design and use your own protocol! This project can either load deployed compiled java classes, not unlike Java's servlet containers, or can be used like a framework - most likely during the initial development phase of your product.

_Also consider the LICENSE document from this repo._


**These other repos are not included here, because they are either tools or non-source-code projects:**

 - The documentation and other written stuff can be found in the [Documentation](https://github.com/RealityShard/Documentation) project.
 - A production environment application can be found in the [ProductionEnvironment](https://github.com/RealityShard/ProductionEnvironment) project.


### Hints and install notes:
 - _Users can install this project by cloning this repo and running `./install.sh`_
 - _If you're a dev, have a look at the existing guide here:_  [The Developer's Guide](https://github.com/RealityShard/Documentation/blob/master/Dev-HowTo.md)
 - _Check out our glossary, if you're lost in translation:_ [The Glossary](https://github.com/RealityShard/Documentation/blob/master/Glossary.md)
