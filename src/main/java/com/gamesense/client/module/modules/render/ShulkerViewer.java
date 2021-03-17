package com.gamesense.client.module.modules.render;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.awt.*;

@Module.Declaration(name = "ShulkerViewer", category = Category.Render)
public class ShulkerViewer extends Module {

    public ColorSetting outlineColor = registerColor("Outline", new GSColor(255, 0, 0, 255));
    public ColorSetting fillColor = registerColor("Fill", new GSColor(0, 0, 0, 255));

    public void renderShulkerPreview(ItemStack itemStack, int posX, int posY, int width, int height) {
        GSColor outline = new GSColor(outlineColor.getValue(), 255);
        GSColor fill = new GSColor(fillColor.getValue(), 200);

        RenderUtil.draw2DRect(posX + 1, posY + 1, width - 2, height - 2, 1000, fill);

        RenderUtil.draw2DRect(posX, posY, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY + height - 1, width, 1, 1000, outline);
        RenderUtil.draw2DRect(posX, posY, 1, height, 1000, outline);
        RenderUtil.draw2DRect(posX + width - 1, posY, 1, height, 1000, outline);

        GlStateManager.disableDepth();
        FontUtil.drawStringWithShadow(ModuleManager.getModule(ColorMain.class).customFont.getValue(), itemStack.getDisplayName(), posX + 3, posY + 3, new GSColor(255, 255, 255, 255));
        GlStateManager.enableDepth();

        NonNullList<ItemStack> contentItems = NonNullList.withSize(27, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(itemStack.getTagCompound().getCompoundTag("BlockEntityTag"), contentItems);

        for (int i = 0; i < contentItems.size(); i++) {
            int finalX = posX + 1 + i % 9 * 18;
            int finalY = posY + 31 + (i / 9 - 1) * 18;
            GameSenseGUI.renderItem(contentItems.get(i), new Point(finalX, finalY));
        }
    }
}