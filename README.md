# Repl-tasks

[![Build Status](http://img.shields.io/travis/obarbeau/repl-tasks.svg?style=flat)](https://travis-ci.org/obarbeau/repl-tasks)
[![Dependency Status](https://www.versioneye.com/user/projects/54cb6c0cde7924b7ed000189/badge.png?style=flat)](https://www.versioneye.com/user/projects/54cb6c0cde7924b7ed000189)
[![Version](http://img.shields.io/badge/version-3.2.0-blue.svg?style=flat)](https://github.com/obarbeau/repl-tasks/releases)
[![License](http://img.shields.io/badge/license-EPL-blue.svg?style=flat)](https://www.eclipse.org/legal/epl-v10.html)

**This project is no longer supported**
**some libs like `spyscope` and `vinyasa` do not conform to clojure.spec, at the time of writing,**
**so I rather use a common repl init namespace with few selected utility functions.**

Call lein tasks (and some lein plugins) directly from the REPL.
No need to exit to deploy, release, ...

## Configuration

TODO describe profile's update: add jar to dependencies + add fcts with vinyasa

## Usage

TODO

Start a REPL (in a terminal: `lein repl`, or from Emacs: open a `clj/cljs`
file in the project, then do `M-x cider-jack-in`. Make sure CIDER is up to
date).

In the REPL, type:

```clojure
(>lein-pprint)
```

The project's map should be displayed.

## Changelog

* 140902 1.0.0 : initial version
* 140922 1.1.0
* 140925 2.0.0
* 140926 2.1.0
* 141001 2.2.0
* 150114 2.3.0 : info message for prj `cljsbuild-ui`;
                 add license and publish on GitHub
* 150122 2.4.0 : use forms instead of strings for load-string
* 150130 2.4.1 : update `io.aviso/pretty`
* 150130 2.5.0 : add Build & Dependency Status
* 150130 2.6.0 : add travis.yml
* 150202 2.6.1 : `(eval '(do ...` is better that `load-str`
* 150206 2.6.2 : update pretty dependency
* 150212 2.6.3 : add `goto` function
* 150212 2.7.0 : use `source-paths` for `goto` function to avoid potential
    namespaces in `checkouts` (linked projects).
* 150320 2.7.1 : update pretty and tools.namespace dependencies
* 150519 2.7.2 : update deps
* 150611 2.8.0 : clojure 1.7.0-RC1
* 150630 2.9.0 : clojure 1.7
* 160111 3.0.0 : clojure 1.8-RC3
* 160622 3.1.0 : upd deps; remove `lein-checks`, rather done via cmd line
* 160628 3.2.0 : remove scope=test dependencies when getting classpath

## License

Copyright © 2015 Olivier Barbeau. All rights reserved.

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
which can be found in the file LICENSE at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license.
You must not remove this notice, or any other, from this software.
