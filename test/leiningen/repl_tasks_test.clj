(ns repl-tasks.core-test
  (:require [clojure.test :refer :all]
            [repl-tasks.core :refer :all]
            [midje.repl :refer [facts]]))
;
; simplement pour que >lein-midje affiche des choses

(deftest a-test
  (testing "Simple test"
    (is (= 1 1))))

(facts "simple midje test"
       (= 1 1) => true)
