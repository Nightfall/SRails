package moe.nightfall.srails.common.tileentity

import javax.vecmath.Vector3d

import moe.nightfall.srails.SRails
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{AxisAlignedBB, ITickable}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import scala.collection.JavaConverters._

class Emitter extends TileEntity with ITickable {
  //TODO find out how to do static final-like
  def maxVelo: Double = 0.25;
  //soft motion limit

  //non-static values
  lazy val bounds = new AxisAlignedBB(pos.getX - 3, pos.getY + 1, pos.getZ - 3, pos.getX + 4, pos.getY + 33, pos.getZ + 4) //creates a 3*3 dome above the Block

  //variables

  def jumpKeyDown = FMLClientHandler.instance.getClient.gameSettings.keyBindJump.isKeyDown

  MinecraftForge.EVENT_BUS.register(this);

  override def update() {
    //script parameter
    //val bounds = new AxisAlignedBB(pos.getX - 1, pos.getY + 1, pos.getZ - 1, pos.getX + 2, pos.getY + 10, pos.getZ + 2) //creates a 3*3 dome above the Block


    val world = getWorld()

    val buf: Seq[Entity] = world.getEntitiesWithinAABB(classOf[Entity], bounds).asScala

    buf.foreach(onEntityIntersect)


  }

  def onEntityIntersect(e: Entity) {
    if (e.canBePushed) {
      e match {
        case p: EntityPlayerSP =>

          if (!p.capabilities.isFlying) {
            //counteract gravity I hope this doesnt break
            p.motionY += 0.0784000015258789

          }

        case _ =>
          e.motionY += 0.0784000015258789
          e.onGround = true //non-player entities can stand in the field and jump to move upwards
      }

      //make sure to create after gravity magic
      val motion: Vector3d = new Vector3d(e.motionX, e.motionY, e.motionZ)

      //--entity controlled movement--//

      if (e.isSneaking) {
        motion.y -= 0.008
      }
      if (e.isInstanceOf[EntityPlayerSP] && jumpKeyDown) {
        motion.y += 0.008
      }

      //--limit velocity--//

      if (motion.length > maxVelo) {
        val scale = 1 - (((motion.length - maxVelo) / 2) / motion.length()) //TODO simplify if possible
        motion.scale(scale)

        if (e.isInstanceOf[EntityPlayerSP]) {
          SRails.log.info(s"scale: ${scale}")
        }
      }

      //--print debug--//

      if (e.isInstanceOf[EntityPlayerSP]) {
        SRails.log.info(e.toString)
        SRails.log.info(s"motion: ${e.motionX} ; ${e.motionY} ; ${e.motionZ}")
        SRails.log.info(s"velocity : ${motion.length}")
      }

      //--slow down--//

      motion.scale(1d - 1d / 64d) //general drag inside the field

      //--check velocity--//

      if (e.isInstanceOf[EntityPlayerSP] && motion.length < maxVelo) {
        SRails.log.info(s"speed limited to ${motion.length()} \n")
        //motion.scale(0) //indicate when it slowed down enough --debug-only :p
      }

      //--set-velocity--

      e.motionX = motion.x
      e.motionY = motion.y
      e.motionZ = motion.z
    }

  }

  @SubscribeEvent
  def onLivingJumpEvent(event: LivingJumpEvent) {
    //make sure to not call this on EntityPlayerSP as it is handled seperatly
    if (event.entity != null && !event.entity.isInstanceOf[EntityPlayerSP] && event.entity.getEntityWorld == getWorld) {
      val buf: Seq[EntityLivingBase] = getWorld.getEntitiesWithinAABB(classOf[EntityLivingBase], bounds).asScala
      if (buf.contains(event.entity)) {
        SRails.log.info(event.entity.toString)

        event.entity.motionY = 0.15
        event.entity.onGround = true
      }
    }
  }

}
