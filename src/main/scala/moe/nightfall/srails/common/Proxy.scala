package moe.nightfall.srails.common

import moe.nightfall.srails.common.init.Blocks
import moe.nightfall.srails.common.tileentity.effect.EffectOnTick
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

private[srails] class Proxy {

  def preInit(e: FMLPreInitializationEvent) {
    MinecraftForge.EVENT_BUS.register(this);
    Blocks.init()

  }


  def init(e: FMLInitializationEvent) {


  }


  def postInit(e: FMLPostInitializationEvent) {

  }

  def registerModel(instance: Item, id: String) {}

  def registerModel(instance: Block, id: String) {}

  val isClient = false

  @SubscribeEvent
  def onTickEvent(event: TickEvent): Unit = {
    EffectOnTick.onTickEvent(event)
  }


}
