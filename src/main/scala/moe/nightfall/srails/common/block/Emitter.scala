package moe.nightfall.srails.common.block

import moe.nightfall.srails.SRails
import moe.nightfall.srails.common.block.property.PropertyRotatable
import moe.nightfall.srails.common.tileentity
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{MathHelper, EnumFacing, BlockPos}
import net.minecraft.world.{IBlockAccess, World}


class Emitter extends SimpleBlock {

  SRails.log.info("created emitter")

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    world.getTileEntity(pos) match {
      case emitter: tileentity.Emitter =>
        if (player.isSneaking) {
          world.setBlockState(pos, state.cycleProperty(PropertyRotatable.REVERSE))
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
      .withProperty(PropertyRotatable.FACING, facing)
      .withProperty(PropertyRotatable.REVERSE, Boolean.box(placer.isSneaking))
  }

  override def createBlockState(): BlockState = {
    new BlockState(this,
      PropertyRotatable.FACING,
      PropertyRotatable.REVERSE
    )
  }

  def isValidFacing(world: World, pos: BlockPos, facing: EnumFacing): Boolean = true //TODO check for solid face

  override def rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean = {
    //TODO implement isSolid check later
   val state: IBlockState = world.getBlockState(pos)
    var facing: EnumFacing = state.getValue(PropertyRotatable.FACING)
    SRails.log.info(s"before rotation: facing $facing")

    //ignore the original rotation
    for(i: Int <- 1 to 3) {
      facing = facing.rotateAround(axis.getAxis)
      //SRails.log.info(s"for 1 until 4 at $i")

      if (isValidFacing(world, pos, facing)) {

        SRails.log.info(s"rotated around axis $axis facing $facing")

        world.setBlockState(pos,
          state.withProperty(PropertyRotatable.FACING, facing),
          3)

        return true
      }

    }
    false

  }

  override def getStateFromMeta(meta: Int): IBlockState = {
    getDefaultState
      .withProperty(PropertyRotatable.FACING, EnumFacing.getFront(meta & 0x1))
      .withProperty(PropertyRotatable.REVERSE, Boolean.box(((meta & 0xe) >>> 1) == 1))


  }

  override def getMetaFromState(state: IBlockState): Int = {
    {if(state.getValue(PropertyRotatable.REVERSE).booleanValue()) 1 else 0 } | state.getValue(PropertyRotatable.FACING).ordinal() << 1
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
