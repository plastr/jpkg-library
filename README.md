Jpkg - Java library and tools for operating system package creation.

#### Summary
Jpkg is both a library and a set of Apache Ant tasks for building operating system packages. Currently, only the Debian .deb format is supported.

#### Features
* Currently the Debian .deb format is supported.
* Detailed validation of package fields to be conformant with the Debian package specification for the package features supported by Jpkg. Not all Debian package features are supported, only those necessary to generate useful packages.
* Supports custom package maintainer scripts, backed by Velocity templates.
* Contains a library called Antidote, which is an attempt to make certain aspects of writing Ant tasks easier, especially dealing with the mutable state of Ant objects and validation of Ant supplied data.
* Also included is a Java library for working with Unix ar(1) archives.
* Includes extensive unit tests.

#### Downloads
[jpkg-bin-1.5.zip (1.5 MB)](http://leplastrier.github.io/jpkg-library/downloads/jpkg-bin-1.5.zip) : The Jpkg library and ant tasks as well as all dependencies.

[jpkg-src-1.5.zip (3.8 MB)](http://leplastrier.github.io/jpkg-library/downloads/jpkg-src-1.5.zip) : The source bundle, includes javadocs.

#### Documentation
[Javadocs version 1.5](http://leplastrier.github.io/jpkg-library/javadoc/)

#### Usage
Basic pattern for including the Ant task in your build:

```xml
<taskdef resource="antlib.xml" classpath="path/to/jpkg-combined.jar"/>
```

Most of the common fields are shown in this example. See `ANT_SCHEMA` the full Ant task schema for more details.
```xml
<dpkg output="dist/dpkg_out" prefix="/usr/local/" distribution="unstable">
  <package destroot="dist/destroot">
    <info>
        <name>packagename</name>
        <version>1.2</version>
        <arch>i386</arch>
        <description>Package description</description>
        <maintainer>
            <name>Package Maintainer</name>
            <email>maintainer@package.com</email>
        </maintainer>
        <priority>optional</priority>
        <section>misc</section>
    </info>
    <permissions> 
        <permission user="username" group="groupname" mode="755" recursive="true">
            <path>bin/</path>
        </permission> 
    </permissions>
    <dependencies>
      <require package="packagename">
          <equalTo>1.4</equalTo>
      </require>
      <conflict package="conflictswith"/>
      <replacement package="replacethis"/>
      <alternatives>
          <require package="option1">
              <equalOrLesserThan>12.1a</equalOrLesserThan>
          </require>
          <require package="option2"/>
      </alternatives>
    </dependencies>
    <scripts>
        <postinst source="script_source/postinst.sh"/>
        <prerm command="echo test prerm message"/>
    </scripts>
  </package>
</dpkg>
```

#### Distribution Files:
##### Contained within **jpkg-bin.zip**:
* **jpkg-combined.jar** - The Jpkg library and ant tasks as well as all dependencies.
* **antidote.jar** - The Antidote library.
* **jpkg-ant.jar** - The Jpkg Ant tasks. Depends on the Jpkg library.
* **jpkg-lib.jar** - The Jpkg library.

##### Also distributed:
* **jpkg-src.zip** - The source bundle, includes javadocs.

#### Dependencies:

For both the library and ant tasks:
* **mail-1.4.1.jar**
* **commons-io-1.3.2.jar**

For the library:
* **commons-codec-1.3.jar**

For the Ant tasks [commons-collections for velocity]:
* **commons-collections-3.2.jar**
* **velocity-1.5-dev.jar**
* **uudecode** is a requirement when executing the maintainer scripts on the target machine.

For the Antidote library:
* The Ant runtime.

#### Custom Maintainer Scripts

See src/java/com/threerings/jpkg/ant/dpkg/scripts/standard/HelloWorld.java for an example.
If you create any custom !TemplateScript classes you will need to do something like the following.

##### Example custom-scripts.xml:
```xml
<?xml version="1.0"?>
<antlib>
    <typedef name="customscript" classname="com.example.CustomScript"/>
</antlib>
```

##### Pattern for including the Ant task in your build with custom TemplateScript classes:
```xml
<path id="jpkg.classpath">
  <pathelement location="${buildlibs.dir}/jpkg-combined.jar"/>
  <pathelement location="${buildlibs.dir}/custom-scripts.jar"/>
</path>
<taskdef resource="antlib.xml" classpathref="jpkg.classpath" loaderref="jpkg.loader"/>
<typedef resource="custom-scripts.xml" classpathref="jpkg.classpath" loaderref="jpkg.loader"/>
```

#### Release Notes
* Version 1.5 - Documentation improvements.
* Version 1.4 - Allow maintainer scripts to optionally fail quietly.
* Version 1.3 - Improve script_runner error handling.
* Version 1.2 - Make the recursive property of the `<permission>` field optional and default to false.
* Version 1.1 - Fix bug in applying recursive permissions.
* Version 1.0 - Initial release.

#### Library:
The Jpkg library aims to be a reusable Java library for building operating system packages. It currently contains an implementation of the Debian .deb file format. This includes a library for working with ar(1) archives. Wherever possible the library has sought to be compliant with the package schema published by the Debian project. However, not all features of the .deb file format are supported, only those necessary for the packages to be useful. In other words, Jpkg does not guarantee that the packages it generates are conformant with all Debian guidelines necessary to have a package accepted into the Debian project repositories.

#### Ant Task:
The `<dpkg>` Ant task is the only currently implemented interface for using the Jpkg library. It contains a complete set of Ant xml attributes for describing packages to be built by Jpkg. It also contains a framework for writing .deb maintainer scripts, allowing for more than one script source to be used for a given script type, e.g. postinst. This script library also allows for script sources to be backed by Velocity templates, allowing for scripts to be constructed in a robust manner from a declarative Ant field, instead of string replacement on a file source, which is however still supported. See com/threerings/jpkg/ant/dpkg/scripts/standard/HelloWorld.java for an example. The entire schema for the Ant task can be seen in the ANT_SCHEMA file.

#### Antidote Library:
Jpkg also contains a library called Antidote, which is an attempt to make certain aspects of writing Ant tasks easier, especially dealing with the mutable state of Ant objects and validation of Ant supplied data. It is designed to be useful standalone, and could easily be incorporated into other projects.

#### Author:
Jpkg was written by Jonathan Le Plastrier, based on an original Python implementation by Landon Fuller. It is copyright [Three Rings Design](http://threerings.net) and is released under a BSD license. See the LICENSE file.
