package org.getspout.spout.gui.texturepacks;

import org.getspout.spout.client.SpoutClient;
import org.lwjgl.Sys;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.addon.Addon;
import org.spoutcraft.spoutcraftapi.gui.Button;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.gui.GenericListView;
import org.spoutcraft.spoutcraftapi.gui.Label;

import com.pclewis.mcpatcher.mod.TextureUtils;

import net.minecraft.src.GuiMainMenu;
import net.minecraft.src.GuiScreen;

public class GuiTexturePacks extends GuiScreen {
	private GenericListView view;
	private Label screenTitle;
	private Button buttonDone, buttonOpenFolder, buttonSelect, buttonReservoir;
	private boolean instancesCreated = false;
	private TexturePacksModel model = SpoutClient.getInstance().getTexturePacksModel();
	
	private void createInstances() {
		if(instancesCreated) return;
		model.update();
		screenTitle = new GenericLabel("Texture Packs");
		view = new GenericListView(model);
		buttonDone = new GenericButton("Main Menu");
		buttonDone.setTooltip("Go back to notch's flashy Main Menu!");
		buttonOpenFolder = new GenericButton("Open Folder");
		buttonOpenFolder.setTooltip("Place your Texture Packs here");
		buttonSelect = new GenericButton("Select");
		buttonSelect.setTooltip("You can also doubleclick on an item");
		buttonReservoir = new GenericButton("Database");
		buttonReservoir.setTooltip("Get awesome new textures here!");
	}
	
	public void initGui() {
		
		Addon spoutcraft = Spoutcraft.getAddonManager().getAddon("Spoutcraft");
		
		createInstances();
		
		int top = 10;
		
		int swidth = mc.fontRenderer.getStringWidth(screenTitle.getText());
		screenTitle.setY(top).setX(width / 2 - swidth / 2).setHeight(11).setWidth(swidth);
		getScreen().attachWidget(spoutcraft, screenTitle);
		top+=15;
		
		view.setX(5).setY(top).setWidth(width - 10).setHeight(height - top - 55);
		getScreen().attachWidget(spoutcraft, view);
		
		top += 5 + view.getHeight();
		
		int totalWidth = Math.min(width - 10, 200*3+10);
		int cellWidth = (totalWidth - 10) / 3;
		int left = width / 2 - totalWidth / 2;
		int center = left + 5 + cellWidth;
		int right = center + 5 + cellWidth;
		
		buttonSelect.setX(right).setY(top).setWidth(cellWidth).setHeight(20);
		getScreen().attachWidget(spoutcraft, buttonSelect);
		
		top += 25;
		
		buttonOpenFolder.setX(left).setY(top).setWidth(cellWidth).setHeight(20);
		getScreen().attachWidget(spoutcraft, buttonOpenFolder);
		
		buttonReservoir.setX(center).setY(top).setWidth(cellWidth).setHeight(20);
		getScreen().attachWidget(spoutcraft, buttonReservoir);
		
		buttonDone.setX(right).setY(top).setWidth(cellWidth).setHeight(20);
		getScreen().attachWidget(spoutcraft, buttonDone);
		
		if(!instancesCreated) {
			int selected;
			selected = model.getTextures().indexOf(TextureUtils.getSelectedTexturePack());
			view.setSelection(selected);
		}
		
		instancesCreated = true;
	}
	
	public void drawScreen(int x, int y, float f) {
		drawDefaultBackground();
	}
	
	@Override
	public void buttonClicked(Button btn) {
		if(btn.equals(buttonDone)) {
			SpoutClient.getHandle().displayGuiScreen(new GuiMainMenu());
		}
		if(btn.equals(buttonOpenFolder)) {
			System.out.println(SpoutClient.getInstance().getTexturePackFolder().getAbsolutePath());
			Sys.openURL("file://"+SpoutClient.getInstance().getTexturePackFolder().getAbsolutePath());
		}
		if(btn.equals(buttonSelect) && view.getSelectedRow() != -1) {
			model.getItem(view.getSelectedRow()).select();
		}
		if(btn.equals(buttonReservoir)) {
			mc.displayGuiScreen(new GuiTexturePacksDatabase());
		}
	}
}