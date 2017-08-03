import scala.collection.mutable

class BlockableID()
case class Blockable[T](id: BlockableID, blockedValue: T, currentValue: T, isBlocked: Boolean){
 def getValue: T = {
  if(isBlocked)
   blockedValue
  else
   currentValue
 }
}

val blockableMap: mutable.Map[BlockableID, Blockable[Any]] = mutable.Map[BlockableID, Blockable[Any]]()

def registerBlockable(value:Any): BlockableID = {
  val blockableID = new BlockableID
  blockableMap(blockableID) = Blockable[Any](blockableID, value, value, false)
  blockableID
}

def blockableGetter[T](id: BlockableID): T ={
 blockableMap(id).getValue.asInstanceOf[T]
}


def blockableSetter[T <: Any](id:BlockableID, newValue:T): Unit ={
 blockableMap(id) = blockableMap(id).copy(currentValue = newValue)
}

def blockVariable(id: BlockableID) = {
  blockableMap(id) = blockableMap(id).copy(isBlocked = true)
}

def unblockVariable(id: BlockableID) = {
  blockableMap(id) = blockableMap(id).copy(isBlocked = false)
}

val armiesID = registerBlockable(0)
def armies:Int = blockableGetter(armiesID)
def armies_= [T](value:T):Unit = blockableSetter(armiesID, value)


val textID = registerBlockable("Hi")
def text:String = blockableGetter(textID)
def text_= [T](value:T):Unit = blockableSetter(armiesID, value)


blockableMap


text
text = "You"

armies
armies
blockVariable(armiesID)
armies = 5
armies
unblockVariable(armiesID)
armies



blockableMap






