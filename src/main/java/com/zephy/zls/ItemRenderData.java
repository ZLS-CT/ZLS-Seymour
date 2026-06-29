package com.zephy.zls;

import net.minecraft.world.item.ItemStack;

//#if MC<=12111
//$$import net.minecraft.client.gui.GuiGraphics;
//#else
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#endif

public class ItemRenderData {
    //#if MC<=12111
    //$$public final GuiGraphics drawContext;
    //#else
    public final GuiGraphicsExtractor drawContext;
    //#endif
    public final ItemStack itemStack;
    public final int x, y, z;

    public ItemRenderData(
        //#if MC<=12111
        //$$GuiGraphics drawContext,
        //#else
        GuiGraphicsExtractor drawContext,
        //#endif
        ItemStack itemStack, int x, int y, int z
    ) {
        this.drawContext = drawContext;
        this.itemStack = itemStack;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
