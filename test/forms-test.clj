; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.forms-test
  (:use clojure.test))

(is 
  (= 6 (+ 1 2 3)) 
  "전위 표기법(prefix notation)이 가진 잇점 
  - 인자 수에 관계 없이 표기법이 일정") 

