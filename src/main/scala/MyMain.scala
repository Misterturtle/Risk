import scalaz.State

object MyMain extends App {


  case class Foo(id: Int, bar:Bar, someValue:String)
  case class Bar(id:Int, someValue:String)

  val a = State[Foo, Unit] { foo:Foo =>
    (foo, ())
  }

  val b = State[Bar, Unit] { bar:Bar =>
    (bar, ())
  }

  def liftFoo(foo:Foo): State[Foo, Unit] = {
    State[Foo, Unit] { _:Foo =>
      (foo, ())
    }
  }

  def liftBar(bar:Bar): State[Bar, Unit] = {
    State[Bar, Unit] { _:Bar =>
      (bar, ())
    }
  }

  def reduceFoo(bar:Bar): State[Foo, Unit] = {
    State[Foo, Unit] { foo:Foo =>
      val newFoo = foo.copy(bar = bar)
      (newFoo, ())
    }
  }


  def changeSomeValue(newValue:String): State[Bar, Unit] = {
    State[Bar, Unit] { bar: Bar =>
      (bar.copy(someValue = newValue), ())
    }
  }

  def flattenBar(barState:State[Bar,Unit]): State[Foo, Unit] = {
    State[Foo, Unit] { foo:Foo =>
      (foo.copy(bar = barState.exec(foo.bar)), ())
    }
  }


  val fooMonad = State[Foo, Unit] { foo:Foo =>
    (flattenBar(changeSomeValue("it worked")).exec(foo), ())
  }

  val result = fooMonad.exec(Foo(1, Bar(2, "origBar"), "origFoo"))

  println(result)
}
