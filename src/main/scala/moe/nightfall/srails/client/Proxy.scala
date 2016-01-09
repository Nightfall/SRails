package moe.nightfall.srails.client

import moe.nightfall.srails.client.renderer.block.ModelInitialization
import moe.nightfall.srails.common.{Proxy => CommonProxy}
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}


object Proxy {

}

private[srails] class Proxy extends CommonProxy {


  override def preInit(e: FMLPreInitializationEvent) {
    super.preInit(e)

  }

  override def init(e: FMLInitializationEvent) {
    super.init(e)

    ModelInitialization.init
  }
  override def registerModel(instance: Item, id: String) = ModelInitialization.registerModel(instance, id)

  override def registerModel(instance: Block, id: String) = ModelInitialization.registerModel(instance, id)

  override def isClient = true
}
