package moe.nightfall.srails.client

import moe.nightfall.srails.common.{Proxy => CommonProxy}
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent

/*
object Proxy {

}
*/
private[srails] class Proxy extends CommonProxy {

  override def preInit(e: FMLPreInitializationEvent) {
    super.preInit(e)

  }

  @SubscribeEvent
  def onKeyInput(event: InputEvent.KeyInputEvent) {

  }
}
