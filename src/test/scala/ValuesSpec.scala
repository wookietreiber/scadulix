package scadulix
package util

import org.specs2.mutable._

class ValuesSpec extends Specification {
  case class Foo(n: Int)
  case class Bar()

  object FooBarz extends Values {
    val f1 = new Foo(1)
    val f2 = new Foo(2)
    val b1, b2, b3 = new Bar()
  }

  "a Values implementing object" should {
    "return the right amount of values" in {
      FooBarz.values[Bar].size must_== 3
    }

    "return the right value by name" in {
      FooBarz.byName[Foo]("f2").get must_== FooBarz.f2
    }
  }
}
