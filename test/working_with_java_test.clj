; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns working-with-java-test
  (:use clojure.test))

; new 함수를 사용해 java 객체 생성
(def rnd (new java.util.Random))
; . 을 사용해 메서드 호출
(. rnd nextInt)
(. rnd nextInt 10)
; . 을 사용해 클래스 필드와 정적 메서드 호출
(. Math PI)

(import '(java.util Random Locale)
        '(java.text MessageFormat))
(is (= java.util.Random
       Random)
    "import를 사용해 현재 네임스페이스로 가져온다")

; (new Random)과 같다
(Random.)

(is (= (. Math PI) Math/PI)
    "classname/membername")

; (. rnd nextInt)와 같다
(.nextInt rnd)

(is 
  (=
   (.getLocation
     (.getCodeSource (.getProtectionDomain (.getClass '(1 2)))))
   (.. '(1 2) getClass getProtectionDomain getCodeSource getLocation))
  ".. 매크로로 여러 계층을 거쳐서 호출하는 코드를 깔끔하게 짤 수 있다.
  '(1 2)에 대해 getClass를 먼저 호출하고 
  그 리턴값으로 다시 getProtectionDomain을 호출한다...")

; doto를 사용해 한 객체에 대해 차례로 여러 메서드를 호출
(doto (System/getProperties)
  (.setProperty "name" "Stuart")
  (.setProperty "favoriteColor" "blue"))

; 자바 배열은 어떤 자바 인터페이스도 구현하지 않는다.
; 자바 배열이 쓰일 자리에 클로저 컬렉션 또는 자바 컬렉션을 사용할 수 없다.

(defn painstakingly-create-array 
  "make-array 함수를 사용해 자바 배열을 만들 수 있다"
  [] 
  (let [arr (make-array String 5)]
    (aset arr 0 "Painstaking")
    (aset arr 1 "to")
    (aset arr 2 "fill")
    (aset arr 3 "in")
    (aset arr 4 "arrays")
    arr))
(is (= "Painstaking" (aget (painstakingly-create-array) 0)))
(is (= 5 (alength (painstakingly-create-array))))

(is (= "first: 2, second: 26"
       (String/format "first: %s, second: %d"
                      (to-array [2 26])))
    "to-array를 사용해 기존 컬렉션에서 자바 배열을 만든다.
    예를 위해서 위처럼 사용했다. 이런 경우엔 clojure.core/format이 낫다.")

; to-array는 Object 배열을 만든다.
; into-array를 사용해 타입을 명시적으로 선언. 만약 타입을 빼면 유추한다.
(into-array String ["Easier" "array" "creation"])
(into-array ["Easier" "array" "creation"])

(def strings (into-array ["some" "strings" "here"]))
(is (=
     ["SOME" "STRINGS" "HERE"]
     (seq (amap strings idx _ (.toUpperCase (aget strings idx)))))
    "amap을 사용해 원소를 변경할 수 있다.
    ret이 들어갈 자리에 _를 사용한 이유는 리턴값이 필요없기 때문
    자바 배열이 리턴되는데, 비교를 하기위해 seq로 시퀀스를 생성")
(is (= 7
       (areduce strings idx ret 0 
                (max ret (.length (aget strings idx)))))
    "areduce로 종합하는 함수를 만들 수 있다.")

(is (= ["A" "B"]
       (map (memfn toUpperCase) ["a" "b"]))
    "toUpperCase는 자바 메서드여서 클로저 함수처럼 인자로 못 넘긴다.
    대신 memfn 매크로를 사용하면 가능")

(is (= ["A" "B"]
       (map #(.toUpperCase %) ["a" "b"]))
    "익명 함수로도 가능. 이 방식을 선호한다고 한다.")

(is (= true (instance? Long 10)) "어떤 클래스의 인스턴스인지 검사")
(is (= true (instance? Comparable 10)))
(is (= false (instance? String 10)))

(is (= "name:A, age:18"
       (format "name:%s, age:%d" "A" 18))
    "자바 Formatter 클래스 래퍼")

; reflection warning 표시
; Class 메타 데이터를 추가해서 타입 힌트를 준다.
; [^Class c] 대신 [c]만 사용하면 c 타입을 알지 못해 경고가 발생한다.
; 경고가 나오지만 타입 유추에 성공해서 제대로 호출된다.
(set! *warn-on-reflection* true)
(defn describe-class [^Class c]
  {:name (.getName c)
   :final (java.lang.reflect.Modifier/isFinal (.getModifiers c))})

(is (= "java.lang.StringBuffer"
       (:name (describe-class StringBuffer))))
(set! *warn-on-reflection* false)

; 새로운 스레드로 실행될 Runnable의 동적 서브클래스를 생성
(.start (Thread. (proxy [Runnable] [] (run [] (println "I ran!")))))

; 클로저 함수는 Runnable, Callable 인터페이스를 자동으로 구현한다
(#(println "foo"))
(.run #(println "foo"))
(.call #(println "foo"))

(defn class-available? [class-name]
  (try
    (Class/forName class-name) true
    (catch ClassNotFoundException _ false)))
(is (= true (class-available? "java.lang.String")))
(is (= false (class-available? "blahblah")))

;-------------------------------------------------------------------------------
; java proxy

(import '(org.xml.sax InputSource)
        '(org.xml.sax.helpers DefaultHandler)
        '(java.io StringReader)
        '(javax.xml.parsers SAXParserFactory))

(def print-element-handler
  "proxy 함수를 사용해 java 클래스 확장이 가능하다."
  (proxy [DefaultHandler] []
    (startElement [uri local qname atts]
      (println (format "Saw element: %s" qname)))))

(defn demo-sax-parse [source handler]
  (.. SAXParserFactory newInstance newSAXParser
      (parse (InputSource. (StringReader. source))
             handler)))

(demo-sax-parse "<foo><bar>Body of bar</bar></foo>" print-element-handler)

