package moe.nightfall.srails.common.tileentity.effect

import moe.nightfall.srails.SRails
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, WorldTickEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object AntiGravity extends EffectOnClientTick with EffectOnWorldTick{
  final val g = 0.08D * 0.9800000190734863D
  //0.0784000015258789 //Gravity

  //need that here but maybe move it later
  @SideOnly(Side.CLIENT)
  lazy val gameSettings = FMLClientHandler.instance.getClient.gameSettings

  SRails.log.debug(s"created antigravity effect object $this")

  override def onEntityIntersect(entity: Entity, worldTickEvent: WorldTickEvent): Unit = {
    //reverse gravity
    entity.motionY += g
  }

  override def onEntityIntersect(entity: Entity, clientTickEvent: ClientTickEvent): Unit = {

  }
  def reverseGravity(e: Entity) {

      //reverse gravity
      e match {
        case p: EntityPlayerSP =>
          if (!p.capabilities.isFlying && Minecraft.getMinecraft.inGameHasFocus) {
            //counteract gravity I hope this doesnt break
            p.motionY += g
          }
        case _ =>
          e.motionY += g
      }

      //SRails.log.info(s"motionY ${e.motionY}")
    }

}
