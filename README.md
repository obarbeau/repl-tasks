# PROJECTS|REPL-TASKS

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

Copyright © 2015 Olivier Barbeau

Distributed under the Eclipse Public License.
