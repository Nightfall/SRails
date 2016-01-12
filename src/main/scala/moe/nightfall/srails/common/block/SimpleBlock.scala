package moe.nightfall.srails.common.block

import java.util

import moe.nightfall.srails.CreativeTab
import moe.nightfall.srails.common.block.property.PropertyRotatable._
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

abstract class SimpleBlock(material: Material = Material.iron) extends BlockContainer(material) {
  setHardness(2f)
  setResistance(5)
  setCreativeTab(CreativeTab)

  def createItemStack(amount: Int = 1) = new ItemStack(this, amount)

  override def getRenderType: Int = 3


  def isRotatable: Boolean = true

  def rotatableProperty: PropertyDirection = ROTATION

  def isValidRotation(world: World, pos: BlockPos, facing: EnumFacing): Boolean = true

  def isValidAxis(axis: EnumFacing.Axis) = true

  override def rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean = {
    if(isRotatable && isValidAxis(axis.getAxis)) {
      val state: IBlockState = world.getBlockState(pos)
      var facing: EnumFacing = state.getValue(rotatableProperty)
      val validFacings: util.Collection[EnumFacing] = rotatableProperty.getAllowedValues

      //find the next valid rotation
      for(i: Int <- 1 to 3) {
        facing = facing.rotateAround(axis.getAxis)

        if (validFacings.contains(facing) && isValidRotation(world, pos, facing)) {

          world.setBlockState(pos,
            state.withProperty(rotatableProperty, facing),
            3)

          return true
        }
      }
    }
    false
  }
}
