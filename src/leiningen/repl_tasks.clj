(ns leiningen.repl-tasks
  (:require [clojure.java.browse]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [leiningen.ancient]
            [leiningen.bikeshed]
            [leiningen.check]
            [leiningen.deps-tree]
            [leiningen.deploy]
            [leiningen.do]
            [leiningen.eastwood]
            [leiningen.install]

            [leiningen.run]
            [midje.repl]
            [vinyasa.inject :as inject]
            [vinyasa.lein :only [lein]]))

(set! *warn-on-reflection* true)

;(defn lein-midje []
;  (let [old-print-length *print-length*]
;    (set! *print-length* nil)
;    (vinyasa.lein/lein with-profile +dev midje)
;    (set! *print-length* old-print-length)))

(defmacro with-full-print-length [& body]
  `(let [old-print-length# *print-length*]
     (set! *print-length* nil)
     (~@body)
     (set! *print-length* old-print-length#)))

(defmacro do-lein [& body]
  `(with-full-print-length vinyasa.lein/lein ~@body))

(defn merge-profiles
  "Si projet clojurescript, ajoute automatiquement le profile `cljs`"
  [profiles]
  (->> (or profiles [])
       (into (if (some #{'org.clojure/clojurescript}
                       (map first (:dependencies (leiningen.core.project/read))))
               [:default :cljs]
               [:default]))))

(defn project-with-profiles
  ([] (project-with-profiles []))
  ([profiles]
   (->> (merge-profiles profiles)
        (leiningen.core.project/read "project.clj"))))

(defn lein-midje []
  ;(do-lein with-profile +local midje))
  (midje.repl/load-facts))

(defn lein-checks []
  ;(do-lein with-profile +local "do" "ancient," "ancient" "profiles," "bikeshed," "check," "eastwood," "kibit"))
  (let [proj (project-with-profiles)]
    (leiningen.ancient/ancient proj)
    (leiningen.ancient/ancient proj "profiles")
    ;(with-full-print-length
      (leiningen.bikeshed/bikeshed proj)
    ;)
    (leiningen.check/check proj)
    (leiningen.eastwood/eastwood proj)
    ; kibit a un peu de mal...
    ))

;; vinyasa.lein ne veut pas me donner la stream out donc on fait
;; les appels intermédiaires soi-même

(defn lein-deps
  ([] (lein-deps []))
  ([profiles]
   (spit "/tmp/dependencies.txt"
         (-> (project-with-profiles profiles)
             (#'leiningen.deps-tree/make-dependency-tree)
             (#'leiningen.deps-tree/print-tree 4)
             (with-out-str)))
   (clojure.java.browse/browse-url "/tmp/dependencies.txt")))

(defn lein-classpath []
  (spit "/tmp/classpath.txt"
        (with-out-str
          (println (str "--> Bien mettre à jour la version du jar du projet dans le lanceur shell, "
                        "car ce n'est pas inclus dans ce classpath!\n\n"))
          (->> (leiningen.core.classpath/get-classpath (leiningen.core.project/read))
               (map #(clojure.string/replace % #"/home/olivier/\.m2/repository" "\\${M2_REPO}"))
               (drop-while #(not (.contains ^String % "M2_REPO")))
               (clojure.string/join ":")
               (str "VERSION=xxx\n\nCP=\"${PROJECT_JAR}:")
               print)
          (print "\"")))
  (clojure.java.browse/browse-url "/tmp/classpath.txt"))

(defn lein-pprint
  "project map."
  ([] (lein-pprint []))
  ([profiles]
   (with-full-print-length spit "/tmp/pprint.txt"
     (-> (project-with-profiles profiles)
         (clojure.pprint/pprint)
         (with-out-str)))
   (clojure.java.browse/browse-url "/tmp/pprint.txt")))

(defn lein-run []
  (leiningen.run/run (project-with-profiles)))

(defn lein-install []
  (lein-midje)
  (leiningen.install/install (project-with-profiles)))

(defn lein-deploy []
  (lein-install)
  (leiningen.deploy/deploy "acs"))

;;(sh/sh "ls")

(defn repl-tasks
  "I don't do a lot."
  [project & args]
  (println "Hi!" project))
