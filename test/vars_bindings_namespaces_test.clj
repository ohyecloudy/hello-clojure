; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.vars-bindings-namespaces-test
  (:use clojure.test))

;-------------------------------------------------------------------------------
; vars

(is 
  (= 
    #'hello-clojure.vars-bindings-namespaces-test/foo
    (def foo 10))
  "def, defn으로 새 객체를 정의할 때, 객체는 var 속에 저장. 
  .../foo라는 var를 생성")

(def foo 10)
(is (= 10 foo)
    "foo라는 심벌을 평가하면 심벌이 가리키는 var 값이 리턴")

(is (= #'foo (var foo))
    "var 자체에 접근할 수 있다.")

; TODO 심벌과 다른 var 특징
; 1. 똑같은 이름의 var가 여러 이름 공간에서 사용될 수 있다
; 2. 메타데이터를 가질 수 있다.
; 3. 스레드별로 동적으로 다시 바인딩될 수 있다.

;-------------------------------------------------------------------------------
; bindings

(defn triple [number] (* 3 number))
(is (= 30 (triple 10))
    "함수 파라미터 바인딩은 lexical scope. 10은 number라는 이름에 바인딩.")

(defn let-test [a b c]
  (let [sum (+ a b c) avg (/ sum 3)]
    sum
    avg))

(is (= 2 (let-test 1 2 3))
    "let으로 만든 바인딩은 바인딩 뒤에 오는 exprs 안에서만 유효.
    반환값은 마지막 exprs 값. 즉 sum이 아니라 가장 마지막인 avg 평가값")

;-------------------------------------------------------------------------------
; destructuring

(defn greet-author-1 [author]
  (str "Hello, " (:first-name author)))

(is (= "Hello, Vernor"
       (greet-author-1 {:last-name "Vinge" :first-name "Vernor"}))
    "author 자체를 바인딩.")

(defn greet-author-2 [{fname :first-name}]
  (str "Hello, " fname))

(is (= "Hello, Vernor"
       (greet-author-1 {:last-name "Vinge" :first-name "Vernor"}))
    ":first-name 심볼을 키로 하는 값만 바인딩.")

(is (= 2 (let [[x y] [1 2 3]] y))
    "1 2를 x y에 바인딩하고 y를 리턴")

(is (= 3 (let [[_ _ z] [1 2 3]] z))
    "_는 바인딩에 신경을 안 쓴다는 관용적인 표현.")

(is (= 2 (let [[_ _ z] [1 2 3]] _))
    "_는 관용적인 표현일 뿐 특별히 다르게 처리되는 건 아니다.
    _에 1 2 순서대로 바인딩된다.")

(is (= "1,2,4"
       (let [[x y :as col] [1 2 3 4]]
         (str x "," y "," (count col))))
    ":as를 사용하면 컬렉션을 바인딩할 수 있다")

;-------------------------------------------------------------------------------
; namespaces

; 이름 공간을 변경하거나 생성
(in-ns 'namespaces-test)
(clojure.core/use 'clojure.test)

(is (= java.lang.String
       String)
    "이름 공간을 생성했을 때, 기본적으로 java.lang 패키지는 사용 가능")

(import '(java.io InputStream File))
(is (= "\\" File/separator)
    "import는 자바 클래스만 가능")

(require 'clojure.string)
(is (= "hello"
       (clojure.string/lower-case "HELLO"))
    "require는 java class loader역할")

(use '[clojure.string :only (lower-case)])
(is (= "hello"
       (lower-case "HELLO"))
    "현재 이름 공간이 lower-case var만 참조 :only 가 없으면 모든 public var를 참조")

(is (= #'clojure.string/lower-case
       (clojure.core/resolve 'lower-case))
    "resolve는 심벌이 가리키는 var를 리턴")

