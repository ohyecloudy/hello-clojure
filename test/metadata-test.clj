; 참고
; * 프로그래밍 클로저 - 스튜어트 할로웨이
; ** http://goo.gl/jmhrP
; * clojure.test - ClojureDocs
; ** http://clojuredocs.org/clojure_core/clojure.test

(ns hello-clojure.metadata-test
  (:use clojure.test))

; 메타데이터(metadata) - 객체의 논리적인 값과는 무관한 데이터

(def stu {:name "Stu" :email "stu@thinkrelevance.com"})
(def serializable-stu (with-meta stu {:serializable true}))

(is (= stu
       serializable-stu)
    "with-meta로 metadata를 추가할 수 있다.
    객체 값에 의존하는 연산 자체에는 영향을 주지 않기 떄문에,
    stu와 serializable-stu는 같은 것으로 취급한다.")

(is (= false
       (identical? stu serializable-stu))
    "identical?은 레퍼런스가 같은지 평가")

(is (= nil (meta stu)))
(is (= {:serializable true}
       (meta serializable-stu))
    "meta로 메타데이터를 얻을 수 있다.")

(def stu-with-address
  (assoc serializable-stu :state "NC"))
(is (= {:serializable true}
       (meta stu-with-address))
    "assoc 함수는 기존 맵에 새 키/값 쌍을 추가한 맵을 만든다.
    이때, 기존 맵 메타데이터를 그대로 가져온다")

; TODO - 책과 다르다. 책에서 설명하는 ^는 사라지고 #^가 ^ 역할
; TODO - 동작이 정확히 이해가 안 된다.
(defn ^{:tag String} shout [^{:tag String} s] (.toUpperCase s))
(is (= java.lang.String
       (:tag (meta #'shout)))
    "리더 매크로 ^를 사용해 var에 키/값 쌍을 추가로 더했다.")
(is (= "HELLO" (shout "hello")))

(defn ^String shout2 [^String s] (.toUpperCase s))
(is (= "HELLO" (shout2 "hello"))
    ":tag 메타 데이터가 흔하게 쓰이기 때문에 간단한 표현식이 있다.
    그냥 ^기호만 사용하면 :tag로 확장")

(defn shout3
  ([s] (.toUpperCase s))
  {:tag String})
(is (= "HELLO" (shout3 "hello"))
    "metadata를 뒤에 정의할 수 있다. 다만 이때는 리더 매크로 ^를 사용 못함")

(def ^{:testdata true} foo
  (with-meta [1 2 3] {:ascending false}))
(is (= true
       (:testdata (meta #'foo)))
    "리더 매크로 ^는 var와 인자에 metadata를 추가")
(is (= nil (:ascending (meta #'foo)))
    "with-meta는 foo에 바인딩된 [1 2 3]이라는 값에 metadata를 추가")
(is (= false (:ascending (meta foo))))

