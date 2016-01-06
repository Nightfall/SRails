package moe.nightfall.srails.common

import moe.nightfall.srails.common.init.Blocks
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

private[srails] class Proxy {

  def preInit(e: FMLPreInitializationEvent) {

    Blocks.init()

  }


  def init(e: FMLInitializationEvent) {


  }


  def postInit(e: FMLPostInitializationEvent) {

  }

  /*
  @SubscribeEvent
  def onLivingJumpEvent(event: LivingJumpEvent) {
    if (event.entity != null) {
      SRails.log.info(event.entity.toString)
      // player.setVelocity(player.motionX, player.motionY + addY, player.motionZ);
      //event.entity.addVelocity(0.0D, 1.0D, 0.0D);
      event.entity.motionY += 0.1
      event.entity.isAirBorne = false
    }
  }
  */

}
