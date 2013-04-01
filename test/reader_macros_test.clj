; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test
; * the reader - clojure
; ** http://clojure.org/reader

(ns reader-macros-test
  (:use clojure.test))

; 리더(reader) - 텍스트를 클로저 자료구조로 변환
; 리더 매크로(reader macros) - 리더로 하여금 특수한 행동을 하게 하는 문자
; 주석(comment)도 리더 매크로

(is 
  (= (quote (1 2)) '(1 2))
  "'은 표현식 값을 평가하지 않는 quote 함수로 만드는 리더 매크로")

(use '[clojure.string :only (split)])
(is 
  (= 
    ["fine" "day"] 
    (filter #(> (count %) 2) (split "A fine day" #"\W+")))
  "fn이 아닌 #로 익명함수를 정의할 수 있다. 인자가 하나일 경우 %로 접근가능")

(def foo 10)
(is (= #'foo (var foo))
    "#' 리더 매크로 - var를 리턴")

(defn ^{:tag String} shout [^{:tag String} s] (.toUpperCase s))
(is (= java.lang.String
       (:tag (meta #'shout)))
    "리더 매크로 ^를 사용해 var에 키/값 쌍을 추가로 더했다.")

(is (= '("a" "bcd" "efg") (re-seq #"\w+" "a bcd efg"))
    "#\"\" - 리더 매크로는 정규식을 만든다.")

(def current-track (ref "song1"))
(is (= "song1" (deref current-track)))
(is (= "song1" @current-track))

; TODO
; 구문 따옴표 - `x
; 평가 기호 - ~
; 이음 평가 기호 - ~@

