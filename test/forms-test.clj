; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.forms-test
  (:use clojure.test))

;-------------------------------------------------------------------------------
; numeric type
(is 
  (= 6 (+ 1 2 3)) 
  "전위 표기법(prefix notation)이 가진 잇점 
  - 인자 수에 관계 없이 표기법이 일정") 

(is
  (= 0 (+))
  "전위 표기법(prefix notation)이 가진 잇점
  - 인자가 없는 경우도 처리. 경계 조건 처리가 수월")

(is 
  (= 22/7 (/ 22 7))
  "ratio 타입을 가지고 있다")

(is 
  (= clojure.lang.Ratio 
     (class (/ 22 7))))

(is
  (= 2.5 (/ 5.0 2))
  "부동소수점 형태로 사용하면 결과가 소수점으로 나온다")

(is (= 3 (quot 22 7)))
(is (= 1 (rem 22 7)))

(is 
  (= java.lang.Long
     (class (* 1000 1000 1000 ))))

; 책과 다르게 알아서 잘 변환해주지 않는다. TODO
;(is
;    (= java.math.BigInteger
;       (class (* 1000M 1000 1000 1000 1000 1000 1000 1000))))

;-------------------------------------------------------------------------------
; character, string

; . (dot operator) - 자바 메서드 호출
(is 
  (= "HELLO"
     (.toUpperCase "hello"))
  "클로저 문자열은 자바 문자열이며 java 메서드 대부분을 바로 호출할 수 있다.")

(is
  (= "123"
     (str 1 2 nil 3))
  "클로저가 추상화한 toString 함수. 
  여러 인자를 받을 수 있고 nil을 에러없이 처리")

(is 
  (= "hey you" 
     (str \h \e \y \space \y \o \u))
  "클로저 문자는 곧 자바 문자. 문자 하나를 '\\문자'로 표시")

; TODO dot operator를 안 써도 그냥 호출되는 이유.
(is 
  (= \S 
     (Character/toUpperCase \s))
  "자바 문자 메서드를 그대로 사용")

(is
  (= [\a \1 \b \2 \c \3]
     (interleave "abc" "123"))
  "interleave는 문자열을 조합해 시퀀스로 리턴")

(is 
  (= "a1b2c3"
     (apply str (interleave "abc" "123")))
  "apply는 argseq 자체를 인자로 넘기는 게 아니라 풀어서 인자로 넘긴다
  (str (interleave ...))으로 호출하면 시퀀스 자체를 인자로 넘긴다")

(is
  (= "abc"
     (apply str (take-nth 2 "a1b2c3")))
  "첫번째 문자를 취하고 nth마다 문자를 취한 시퀀스를 리턴")


;-------------------------------------------------------------------------------
; boolean, nil
(is (= "false" (if false "true" "false")))

(is
  (= "nil is false"
     (if nil "nil is true" "nil is false"))
  "nil은 false로 평가된다")

(is
  (= "we are in clojure!"
     (if () "we are in clojure!" "we are in lisp!"))
  "빈 리스트는 true로 평가된다")

(is
  (= "zero is true"
     (if 0 "zero is true" "zero is false"))
  "0은 true로 평가된다, false와 nil을 제외하고는 다 true로 평가된다")

