package com.sicpa.standard.gui.demo.components.sicpa.SelectionFlowScreen;

import java.awt.image.BufferedImage;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.DefaultSelectableItem;
import com.sicpa.standard.gui.utils.ImageUtils;

public class LabelTypeItem extends DefaultSelectableItem {

	static enum LabelType {
		labelType1(ImageUtils.createRandomStrippedImage(500,100)), labelType2(ImageUtils.createRandomStrippedImage(500,100));

		private BufferedImage image;

		public BufferedImage getImage() {
			return this.image;
		}

		private LabelType(final BufferedImage image) {
			this.image = image;
		}
	};

	private LabelType type;

	public LabelTypeItem(final int index, final LabelType type) {
		super(index, null,type.image);
		this.type = type;
	}

	public LabelType getType() {
		return this.type;
	}
}
