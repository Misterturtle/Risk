import org.mockito.Mockito
import org.mockito.stubbing.Answer
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Harambe on 7/5/2017.
  */

class DefinedFuncClass(){

  var var1 = 0
  var var2 = 0

  def method1(): Unit ={
    var1 += 1
  }

  def method2(): Unit ={
    var2 += 1
  }

}

class AnonFuncClass(){

  def runFunctions(function: ()=>Unit): Unit = {
    function()
  }
}



class MockSpike extends FreeSpec with Matchers with MockitoSugar {


  "Working with Answers in Mockito" - {

    "What is returned from the args of an answer?" in {

      val mockDefClass = mock[DefinedFuncClass]






    }



  }




}
