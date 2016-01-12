package moe.nightfall.srails.common.tileentity.traits

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.block.property.PropertyReversible
import net.minecraft.tileentity.TileEntity

trait Reversible extends TileEntity{
  def isReverse: Boolean = {
    if(getWorld != null) {
      getWorld.getBlockState(getPos).getValue(PropertyReversible.REVERSE).booleanValue()
    } else {
      SRails.log.info("isReverse return default value")
      false
    }
  }

}
