; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns multimethods-test
  (:use clojure.test))

;-------------------------------------------------------------------------------
; 다중메서드 없이 구현
; 좋은 방식이 아니다. 
; 경우를 판단하고 나누는 과정과 각 경우에 대한 구현이 뒤섞이기 때문
(defn my-print-vector [ob]
  (.write *out* "[")
  (.write *out* (clojure.string/join " " ob))
  (.write *out* "]"))

(defn my-print [ob]
  (cond
    (vector? ob) (my-print-vector ob)
    (nil? ob) (.write *out* "nil")
    (string? ob) (.write *out* ob)))

(defn my-println [ob]
  (my-print ob)
  (.write *out* "\n"))

(my-println nil) 
(my-println [1 2 3]) 

;-------------------------------------------------------------------------------
; 다중메서드 
; cond에 새 절을 추가해서 크기를 늘이는 대신, 다중메서드를 사용해 타입별 구현을
; 분리할 수 있다. 언제나 새로운 구현을 추가할 수 있다.

; dispatch-fn - 받은 인자 타입을 리턴하는 함수
; 클로저에서 제공하는 class 함수가 이런 일을 할 수 있다.
(defmulti my-print class)
; dispatch-val로 String을 사용. 즉 인자를 넣어 class 리턴 타입이 String인 경우
; 실행할 메서드
(defmethod my-print String [s]
  (.write *out* s))

(my-println "hello")
; hello

(defmethod my-print nil [s]
  (.write *out* "nil"))

(my-println nil)
; nil

(defmethod my-print Number [n]
  (.write *out* (.toString n)))

(my-print 42)
; 42
(is (= java.lang.Long (class 42))
    "Number가 아니라 Long이다")
(is (isa? Long Number)
    "상속 관계. 다중 메서드에서 상속 관계도 고려한다")

; :default로 dispatch-fn 적용한 결과가 
; 어떤 dispatch-val과 일치하지 않을 경우 처리
(defmethod my-print :default [s]
  (.write *out* "#<")
  (.write *out* (.toString s))
  (.write *out* ">"))

(my-print (java.sql.Date. 0))
; #<1970-01-01>

(defmulti my-print2 class :default :everything-else)
(defmethod my-print2 :everything-else [_]
  (.write *out* "Not implemented yet..."))

(my-print2 (java.util.Random.))
; Not implemented yet...

(defmethod my-print java.util.Collection [c]
  (.write *out* "(")
  (.write *out* (clojure.string/join " " c))
  (.write *out* ")"))
(my-print [1 2 3])
; (1 2 3)

(defmethod my-print clojure.lang.IPersistentVector [c]
  (.write *out* "[")
  (.write *out* (clojure.string/join " " c))
  (.write *out* "]"))

; (my-print [1 2 3])
; 예외 발생 Collection이면서 IPersistentVector이기 때문

; 디스패치 값 사이에 우선 값을 정의할 수 있다.
(prefer-method my-print clojure.lang.IPersistentVector java.util.Collection)

(my-print [1 2 3])
; [1 2 3]

