package moe.nightfall.srails.common.tileentity.effect

import moe.nightfall.srails.SRails
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{ClientTickEvent, WorldTickEvent}

import scala.collection.JavaConverters._
import scala.collection.mutable

object EffectOnTick {
  val onWorldSet = mutable.Set.empty[EffectOnWorldTick]
  val onClientSet = mutable.Set.empty[EffectOnClientTick]

  def entitySet(world: World, set: mutable.Set[AxisAlignedBB]): mutable.Set[Entity] = {
    val entitySet = mutable.Set.empty[Entity]
    set.foreach(aabb => if (aabb != null) {
      entitySet ++= world.getEntitiesWithinAABB(classOf[Entity], aabb).asScala
    })
    entitySet
  }

  @SubscribeEvent
  def onTickEvent(event: TickEvent): Unit = {
    if ((event.`type` == TickEvent.Type.CLIENT || event.`type` == TickEvent.Type.WORLD)
      && event.phase == TickEvent.Phase.END) {

      var world: World = null
      var set: scala.collection.mutable.Set[AxisAlignedBB] = null

      event match {
        case worldTickEvent: WorldTickEvent =>
          world = worldTickEvent.world
          for (effect: EffectOnWorldTick <- onWorldSet) {
            set = effect.boundsSet
            for (entity <- entitySet(world, set)) {
              effect.onEntityIntersect(entity, worldTickEvent)
            }
          }
        case clientTickEvent: ClientTickEvent =>

          if (SRails.proxy.isClient) {
            val player: EntityPlayerSP = FMLClientHandler.instance.getClientPlayerEntity
            if (player != null) {
              world = player.worldObj
            } else return

            for (effect: EffectOnClientTick <- onClientSet) {
              set = effect.boundsSetClient
              for (entity <- entitySet(world, set)) {
                effect.onEntityIntersect(entity, clientTickEvent)
              }
            }
          }
        case _ =>

      }
    }
  }


  def registerBounds(effect: ForceFieldEffect, aabb: AxisAlignedBB): Unit = {
    effect match {
      case client: EffectOnClientTick =>
       client.registerBoundsClient(aabb)
      case _ =>
    }
    effect match {
      case world: EffectOnWorldTick =>
        world.registerBounds(aabb)
      case _ =>
    }
  }


  def unregisterBounds(effect: ForceFieldEffect, aabb: AxisAlignedBB): Unit = {
    effect match {
      case client: EffectOnClientTick =>
       client.unregisterBoundsClient(aabb)
      case _ =>
    }
    effect match {
      case world: EffectOnWorldTick =>
        world.unregisterBounds(aabb)
      case _ =>
    }
  }
}

abstract class EffectOnTick extends ForceFieldEffect

trait EffectOnWorldTick extends EffectOnTick {
  val boundsSet = mutable.Set.empty[AxisAlignedBB]

  def registerBounds(aabb: AxisAlignedBB): Unit = {
    if (boundsSet.isEmpty) EffectOnTick.onWorldSet += this
    boundsSet += aabb
    SRails.log.info(s"world register $aabb")
  }

  def unregisterBounds(aabb: AxisAlignedBB): Unit = {
    boundsSet -= aabb
    if (boundsSet.isEmpty) EffectOnTick.onWorldSet -= this
    SRails.log.info(s"world unregister $aabb")
  }

  def onEntityIntersect(entity: Entity, worldTick: WorldTickEvent): Unit
}

trait EffectOnClientTick extends EffectOnTick {
  val boundsSetClient = mutable.Set.empty[AxisAlignedBB]

  def registerBoundsClient(aabb: AxisAlignedBB): Unit = {
    if (boundsSetClient.isEmpty) EffectOnTick.onClientSet += this
    boundsSetClient += aabb
    SRails.log.info(s"client register $aabb")
  }

  def unregisterBoundsClient(aabb: AxisAlignedBB): Unit = {
    boundsSetClient -= aabb
    if (boundsSetClient.isEmpty) EffectOnTick.onClientSet -= this
    SRails.log.info(s"client unregister $aabb")
  }

  def onEntityIntersect(entity: Entity, clientTickEvent: ClientTickEvent): Unit
}