; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test
; * The Reader - clojure.org
; ** http://clojure.org/reader

(ns macros-test
  (:use clojure.test))

(defn defn-unless [expr form]
  (println "about to test...")
  (if expr nil form))

(defn-unless true (println "this should print"))
; this should print
; about to test...
; nil
; 함수 인자가 defn-unless에 넘겨지기 전에 평가된다.
; true인데도 몸체를 평가해서 출력하고 있다.

(defmacro unless [expr form]
  (list 'if expr nil form))
; unless 몸체 부분은 매크로 익스팬션 타임에 실행되어
; 컴파일을 위한 if 구문을 만들어 낸다.

(unless false (println "this should print"))
; this should print

(is (= '(if false nil (println "hello"))
       (macroexpand-1 '(unless false (println "hello"))))
    "매크로 익스팬션 타임에 어떤 일이 일어나는 지 볼 수 있다.")

(defmacro bad-unless [expr form]
  (list 'if 'expr nil form))
(is (= '(if expr nil (println "hello"))
       (macroexpand-1 '(bad-unless false (println "hello"))))
    "expr 앞에 quote를 붙여서 익스팬션 타임에 그냥 벗겨져서 expr이 됐다.")

(is (= '(. (. arm getHand) getFinger)
       (macroexpand '(.. arm getHand getFinger)))
    "macroexpand를 사용하면 끝까지 전개된 코드를 볼 수 있다.")

(is (= '(if true (do (print "1") (print "2")))
       (macroexpand-1 '(when true (print "1") (print "2"))))
    "if와 when이 다른 점은 when은 else 절이 없고 
    여러 인자를 받아 do안에 넣고 실행")

; .. 과 같은 chain-old 매크로 구현
(defmacro chain-old 
  ([x form] (list '. x form))
  ([x form & more] (concat (list 'chain-old (list '. x form)) more)))
(is (= '(chain-old (. a b) c d)
       (macroexpand-1 '(chain-old a b c d))))
(is (= '(chain-old (. (. a b) c) d)
       (macroexpand-1 '(chain-old (. a b) c d))))
(is (= '(. (. (. a b) c) d)
       (macroexpand '(chain-old a b c d))))

; 매크로를 쉽게 만들 수 있는 템플릿이 있다
; foo#
; (gensym prefix?)
; (macroexpand form)
; (macroexpand-1 form)
; (list-frag? ~@form list-frag?)
; `form
; ~form

(defmacro chain 
  ([x form] `(. ~x ~form))
  ([x form & more] `(chain (. ~x ~form) ~@more)))
(is (= '(. (. arm getHand) getFinger)
       (macroexpand '(chain arm getHand getFinger)))
    "
    ` 구문 따옴표(syntax quote)는 일반적인 따옴표와 비슷하지만
    리스트 안에서 ~ 평가 기호(unquote)를 사용해 따옴표 효과를 없앨 수 있다.
    ~@ 이음 평가 기호(splicing unquote)를 사용하지 않으면
    more 또한 리스트이므로 괄호를 포함하게 된다.")

; 심벌 캡처(symbol capture)라고 불리는 매크로 버그를 만들지 않기 위해
; # 를 사용해 고유한 ID를 붙인 심벌을 자동으로 생성하게 할 수 있다.
(defmacro bench [expr]
  `(let [start# (System/nanoTime)
         result# ~expr]
     {:result result# :elapsed (- (System/nanoTime) start#)}))
(bench (str "a" "b"))

; 특수 구문
; * 기본적인 제어 구조를 제공하는 특수구문 (if, recur)
; * 자바에 직접 접근할 수 있게 해주는 특수 구문 (new, .)
; * 이름 및 바인딩을 생성하는 특수 구문 (def, let, binding,...)

; and, or 둘 다 short-circuit evaluation
; http://ohyecloudy.com/pnotes/archives/542
(is (= nil (and 1 0 nil false)))
(is (= 1 (or 1 0 nil false)))

; 인자를 전혀 평가하지 않는다. 종종 파일 마지막에 API 사용법을 보이는데 사용
; comment 내에서 본문을 강조하기 위해 이런 스타일을 사용
(comment

(load-file "src/inspector.clj")
(refer 'inspector)
(inspect-tree {:a 1}...)

)

(defmacro defstruct-macro
  [name & keys]
  `(def ~name (create-struct ~@keys)))
; 간단하지만 함수로 만들 수 없다. def가 특수 구문이기 때문에.
; def는 매크로 타임에 생성돼야만 하고 런타임에 동적으로 def를 호출할 수 없다.

;-------------------------------------------------------------------------------

(= (last (sort (rest (reverse [2 5 4 1 3 6]))))
   (-> [2 5 4 1 3 6] reverse rest sort last)
   "-> 매크로는 왼쪽에서 오른쪽으로 적용")

(= (reduce + (map inc (take 3 (drop 2 [2 5 4 1 3 6]))))
   (->> [2 5 4 1 3 6] (drop 2) (take 3) (map inc) (reduce +)))

