package moe.nightfall.srails.common

import moe.nightfall.srails.common.init.Blocks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

private[srails] class Proxy {

  def preInit(e: FMLPreInitializationEvent) {
    MinecraftForge.EVENT_BUS.register(this)
    FMLCommonHandler.instance.bus.register(this)

    Blocks.init()

  }

  def init(e: FMLInitializationEvent) {


  }

  def postInit(e: FMLPostInitializationEvent) {

  }
}
