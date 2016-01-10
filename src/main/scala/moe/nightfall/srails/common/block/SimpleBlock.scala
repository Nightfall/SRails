package moe.nightfall.srails.common.block

import moe.nightfall.srails.CreativeTab
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.item.ItemStack

abstract class SimpleBlock(material: Material = Material.iron) extends BlockContainer(material) {
  setHardness(2f)
  setResistance(5)
  setCreativeTab(CreativeTab)

  def createItemStack(amount: Int = 1) = new ItemStack(this, amount)

  override def getRenderType: Int = 3
}
