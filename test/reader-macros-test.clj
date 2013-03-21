; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.reader-macros-test
  (:use clojure.test))

; 리더(reader) - 텍스트를 클로저 자료구조로 변환
; 리더 매크로(reader macros) - 리더로 하여금 특수한 행동을 하게 하는 문자
; 주석(comment)도 리더 매크로

(is 
  (= (quote (1 2)) '(1 2))
  "'은 표현식 값을 평가하지 않는 quote 함수로 만드는 리더 매크로")

; TODO
; 익명 함수 - #(.toUpperCase %)
; deref - @form => (deref form)
; meta - ^form => (meta form)
; 메타데이터 - #^metadata form
; 정규식 패턴 - #"foo" => java.util.regex.Pattern
; 구문 따옴표 - `x
; 평가 기호 - ~
; 이음 평가 기호 - ~@
; var-quote - #'x => (var x)

