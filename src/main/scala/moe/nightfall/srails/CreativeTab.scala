package moe.nightfall.srails

import net.minecraft.init.Items
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack

object CreativeTab extends CreativeTabs(CreativeTabs.getNextID, SRails.Name){
  private lazy val stack = new ItemStack(Items.minecart, 1)

  override def getTabIconItem = stack.getItem

  override def getIconItemDamage = stack.getItemDamage

  override def getTranslatedTabLabel = getTabLabel
}
