package moe.nightfall.srails.common.init

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.block.{Emitter, SimpleBlock}
import moe.nightfall.srails.common.tileentity
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.GameRegistry

object Blocks {

  val emitterBlock: Block = new Emitter

  def init() {

    registerBlock(emitterBlock, "emitter")
    GameRegistry.registerTileEntity(classOf[tileentity.Emitter], s"${SRails.ID}:emitter")
  }

  private def registerBlock[T <: Block](instance: T, id: String) = {
    instance match {
      case simple: SimpleBlock =>
        instance.setUnlocalizedName(s"$id")
        GameRegistry.registerBlock(simple, classOf[ItemBlock], id)
      case _ =>
    }
    SRails.proxy.registerModel(instance, id)
    instance
  }

}