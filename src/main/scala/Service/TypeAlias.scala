package Service

object Action {
  //todo: try to not have to use FlatAction object
  def apply[A](f: A => A): Action[A] = {
    new Action(f)
  }

  def apply[A](f: => Unit): Action[A] = Action { a:A => a }
}

object FlatAction {
  def apply[A](f: A => Action[A]): Action[A] = {
    new Action((a:A) => f(a).run(a))
  }
}

class Action[A](f: A => A){
  def run(a:A): A = f(a)
}

