# Repl-tasks

[![Build Status](http://img.shields.io/travis/obarbeau/repl-tasks.svg?style=flat)](https://travis-ci.org/obarbeau/repl-tasks)
[![Dependency Status](https://www.versioneye.com/user/projects/54cb6c0cde7924b7ed000189/badge.png?style=flat)](https://www.versioneye.com/user/projects/54cb6c0cde7924b7ed000189)
[![Version](http://img.shields.io/badge/version-2.4.0-blue.svg?style=flat)](https://github.com/obarbeau/repl-tasks/releases)
[![License](http://img.shields.io/badge/license-EPL-blue.svg?style=flat)](https://www.eclipse.org/legal/epl-v10.html)

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

## License

Copyright © 2015 Olivier Barbeau. All rights reserved.

The use and distribution terms for this software are covered by the
Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
which can be found in the file LICENSE at the root of this distribution.
By using this software in any fashion, you are agreeing to be bound by
the terms of this license.
You must not remove this notice, or any other, from this software.
