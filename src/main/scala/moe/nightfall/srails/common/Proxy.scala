package moe.nightfall.srails.common

import moe.nightfall.srails.common.init.Blocks
import moe.nightfall.srails.common.tileentity.Emitter
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
private[srails] class Proxy {

  def preInit(e: FMLPreInitializationEvent) {
    FMLCommonHandler.instance().bus().register(this);
    Blocks.init()

  }


  def init(e: FMLInitializationEvent) {


  }


  def postInit(e: FMLPostInitializationEvent) {

  }

  def registerModel(instance: Item, id: String) {}

  def registerModel(instance: Block, id: String) {}

  @SubscribeEvent
  def onTickEvent(event: TickEvent): Unit = {
    Emitter.onTickEvent(event)
  }


}
