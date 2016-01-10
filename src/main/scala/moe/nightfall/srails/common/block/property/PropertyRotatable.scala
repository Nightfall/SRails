package moe.nightfall.srails.common.block.property

import net.minecraft.block.properties.{PropertyBool, PropertyDirection}

object PropertyRotatable {
  final val FACING = PropertyDirection.create("facing")
  final val REVERSE = PropertyBool.create("reverse")
}
