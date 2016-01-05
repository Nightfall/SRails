package moe.nightfall.srails.common.init

import moe.nightfall.srails.common.block.Emitter
import net.minecraftforge.fml.common.registry.GameRegistry
import moe.nightfall.srails.common.tileentity

object Blocks {
  def init() {
    GameRegistry.registerTileEntity(classOf[tileentity.Emitter], "emitter")

    GameRegistry.registerBlock(new Emitter, "srails:emitter")
  }
}
