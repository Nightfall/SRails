package moe.nightfall.srails.client.renderer.block

import moe.nightfall.srails.SRails
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item

import scala.collection.mutable

object ModelInitialization {

  private val meshableItems = mutable.ArrayBuffer.empty[Item]

  def init {
    registerItems()
  }

  def registerModel(instance: Item, id: String): Unit = {
    meshableItems += instance
  }

  def registerModel(instance: Block, id: String): Unit = {
    val item = Item.getItemFromBlock(instance)
    registerModel(item, id)
  }

  private def registerItems(): Unit = {
    val modelMeshes = Minecraft.getMinecraft.getRenderItem.getItemModelMesher
    for (item: Item <- meshableItems) {
      val ressourceString = s"${SRails.ID}:${item.getUnlocalizedName.substring(5)}"
      modelMeshes.register(item, 0, new ModelResourceLocation(ressourceString, "inventory"))
      SRails.log.info(s"registering ${item.getUnlocalizedName} $ressourceString")
    }
    meshableItems.clear()
  }

}
