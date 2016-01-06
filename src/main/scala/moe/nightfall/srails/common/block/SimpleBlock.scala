package moe.nightfall.srails.common.block

import moe.nightfall.srails.CreativeTab
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material

abstract class SimpleBlock(material: Material = Material.iron) extends BlockContainer(material) {
  setHardness(2f)
  setResistance(5)
  setCreativeTab(CreativeTab)


}
