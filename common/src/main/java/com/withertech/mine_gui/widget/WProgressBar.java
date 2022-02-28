package com.withertech.mine_gui.widget;

import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.widget.data.Texture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class WProgressBar extends WBar
{
	private static final ResourceLocation PROGRESS = new ResourceLocation(MineGui.MOD_ID, "textures/widget/progress_bar.png");
	public WProgressBar(int field, int maxField)
	{
		super(new Texture(PROGRESS).withUv(0, 0, 1, 0.5f), new Texture(PROGRESS).withUv(0, 0.5f, 1, 1), field, maxField, Direction.RIGHT);
	}

	@Override
	public String toString()
	{
		return "WProgressBar{" +
				"bg=" + bg +
				", bar=" + bar +
				", field=" + field +
				", max=" + max +
				", direction=" + direction +
				", maxValue=" + maxValue +
				", properties=" + properties +
				", tooltipLabel='" + tooltipLabel + '\'' +
				", tooltipTextComponent=" + tooltipTextComponent +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
