package moe.nightfall.srails.common.block

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.tileentity
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World

class Emitter extends SimpleBlock {
  /*
  //copied from openComputers
  and tutorials
    override def createBlockState(): BlockState = new BlockState(this, PropertyRotatable.Facing, property.PropertyRunning.Running)

    override def getStateFromMeta(meta: Int): IBlockState = getDefaultState.withProperty(PropertyRotatable.Facing, EnumFacing.getHorizontal(meta >> 1))

    override def getMetaFromState(state: IBlockState): Int = state.getValue(PropertyRotatable.Facing).getHorizontalIndex << 1 | (if (state.getValue(property.PropertyRunning.Running)) 1 else 0)

    */

  SRails.log.info("created emitter")

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new tileentity.Emitter

  override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
    super.breakBlock(worldIn, pos, state)
    worldIn.getTileEntity(pos).asInstanceOf[tileentity.Emitter].disable()
  }
}
