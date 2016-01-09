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

import scala.annotation.meta


class Emitter extends SimpleBlock {

  SRails.log.info("created emitter")

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    world.getTileEntity(pos) match {
      case emitter: tileentity.Emitter =>
        if (player.isSneaking) {
          world.setBlockState(pos, state.cycleProperty(PropertyRotatable.DIRECTION))
        } else {
          rotateBlock(world, pos, side)
        }
        true
      case _ => false
    }
  }

  override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState = {
    val direction = MathHelper.floor_double(((placer.rotationYaw * 4F) / 360F) + 0.5D) & 3
    //TODO get player facing (including pitch)

    getDefaultState
      .withProperty(PropertyRotatable.FACING, facing.getOpposite)
      .withProperty(PropertyRotatable.DIRECTION, EnumFacing.getFront(direction))
  }

  override def createBlockState(): BlockState = {
    new BlockState(this,
      PropertyRotatable.FACING,
      PropertyRotatable.DIRECTION
    )
  }

  def isValidFacing(world: World, pos: BlockPos, facing: EnumFacing): Boolean = true //TODO check for solid face

  override def rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean = {
    //TODO implement isSolid check later
    /*
    world.getTileEntity(pos) match {
      case emitter: tileentity.Emitter =>
        var facing: EnumFacing = emitter.facing
        var direction: EnumFacing = emitter.direction
        for(i: Int <- 1 to 3) {
          facing = facing.rotateAround(axis.getAxis)
          direction = direction.rotateAround(axis.getAxis)

          SRails.log.info(s"for 1 until 4 at $i")

          if (isValidFacing(world, pos, facing)) {

            SRails.log.info(s"rotated around axis $axis direction $direction facing $facing")

            //manipulate the values in the emitter?
            emitter.facing = facing
            emitter.direction = direction
            emitter.updateRotations()

            return true
          }

        }
        return true
      case _ => return false
    }
    */

    val state: IBlockState = world.getBlockState(pos)
    var facing: EnumFacing = state.getValue(PropertyRotatable.FACING)
    var direction: EnumFacing = state.getValue(PropertyRotatable.DIRECTION)

    //ignore the original rotation
    for(i: Int <- 1 to 3) {
      facing = facing.rotateAround(axis.getAxis)
      direction = direction.rotateAround(axis.getAxis)

      //SRails.log.info(s"for 1 until 4 at $i")

      if (isValidFacing(world, pos, facing)) {

        SRails.log.info(s"rotated around axis $axis direction $direction facing $facing")

        world.setBlockState(pos,
          state.withProperty(PropertyRotatable.FACING, facing)
          .withProperty(PropertyRotatable.DIRECTION, direction), 2)

        return true
      }

    }
    false

  }

  override def getStateFromMeta(meta: Int): IBlockState = {
    getDefaultState.withProperty(PropertyRotatable.FACING, EnumFacing.getFront(meta & 0x7))
      .withProperty(PropertyRotatable.DIRECTION, EnumFacing.getFront((meta & 0x63) >>> 3))
  }

  override def getMetaFromState(state: IBlockState): Int = {
    state.getValue(PropertyRotatable.FACING).ordinal() | state.getValue(PropertyRotatable.FACING).ordinal() << 3
  }

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new tileentity.Emitter(meta)

  override def getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
    world.getTileEntity(pos) match {
      case emitter: tileentity.Emitter =>

        state//.withProperty(PropertyRotatable.DIRECTION, emitter.direction)
          //.withProperty(PropertyRotatable.FACING, emitter.facing) //data from tile entity
      case _ => state
    }
  }
/*
  override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
    worldIn.getTileEntity(pos).asInstanceOf[tileentity.Emitter].disable()
    super.breakBlock(worldIn, pos, state)
  }
*/
}
