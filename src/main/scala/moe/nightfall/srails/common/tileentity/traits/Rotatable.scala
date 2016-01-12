package moe.nightfall.srails.common.tileentity.traits

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.block.property.PropertyRotatable._
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

trait Rotatable extends TileEntity{
  def rotatableProperty: PropertyDirection = ROTATION

  def rotation: EnumFacing = {
    if (getWorld != null) {
      getWorld.getBlockState(getPos).getValue(rotatableProperty)
    } else {
      SRails.log.info("facing return default value")
      EnumFacing.UP
    }
  }
}
