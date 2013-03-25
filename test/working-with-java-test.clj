; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.working-with-java-test
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

