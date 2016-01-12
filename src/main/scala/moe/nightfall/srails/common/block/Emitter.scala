package moe.nightfall.srails.common.block

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.block.property.PropertyReversible._
import moe.nightfall.srails.common.block.property.PropertyRotatable._
import moe.nightfall.srails.common.tileentity
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World


class Emitter extends SimpleBlock{

  SRails.log.info("created emitter")

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    world.getTileEntity(pos) match {
      case emitter: tileentity.Emitter =>
        if (player.isSneaking) {
          world.setBlockState(pos, state.cycleProperty(REVERSE))
        } else {
          rotateBlock(world, pos, side)
        }
        true
      case _ => false
    }
  }

  override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState = {
    //TODO get player facing (including pitch)

    getDefaultState
      .withProperty(ROTATION, facing)
      .withProperty(REVERSE, Boolean.box(placer.isSneaking))
  }

  override def createBlockState(): BlockState = {
    new BlockState(this,
      ROTATION,
      REVERSE
    )
  }


  override def getStateFromMeta(meta: Int): IBlockState = {
    getDefaultState
      .withProperty(ROTATION, EnumFacing.getFront(meta & 0x1))
      .withProperty(REVERSE, Boolean.box(((meta & 0xe) >>> 1) == 1))
  }

  override def getMetaFromState(state: IBlockState): Int = {
    {if(state.getValue(REVERSE).booleanValue()) 1 else 0 } | state.getValue(ROTATION).ordinal() << 1
  }

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = {
    val emitter = new tileentity.Emitter
    //emitter.meta_=(meta)
    emitter
  }
/*
  override def getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
    world.getTileEntity(pos) match {
      case emitter: tileentity.Emitter =>

        state
          //.withProperty(PropertyRotatable.FACING, emitter.facing) //data from tile entity
      case _ => state
    }
  }
*/
}
