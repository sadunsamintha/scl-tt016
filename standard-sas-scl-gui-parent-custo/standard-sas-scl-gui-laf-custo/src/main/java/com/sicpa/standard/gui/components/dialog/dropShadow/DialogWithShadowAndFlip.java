package com.sicpa.standard.gui.components.dialog.dropShadow;

import java.awt.Window;

import javax.swing.JComponent;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.flipapble.ReversableComponent;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class DialogWithShadowAndFlip extends DialogWithDropShadow {

	private static final long serialVersionUID = 1L;

	protected ReversableComponent reversableComp;

	public DialogWithShadowAndFlip(final Window parent) {
		super(parent);
		this.reversableComp = new ReversableComponent();

		getContentPane().setLayout(new MigLayout("fill"));

		SicpaLookAndFeelCusto.flagAsWorkArea(this.reversableComp);

		getContentPane().add(this.reversableComp, "push,grow, span, gap right 5 , gap bottom 5");
	}

	public void setFront(final JComponent comp) {
		this.reversableComp.setFront(comp);
	}

	public void setBack(final JComponent comp) {
		this.reversableComp.setBack(comp);
	}

	public void setAnimDuration(final int animDuration) {
		this.reversableComp.setAnimDuration(animDuration);
	}

	public void flip() {
		this.reversableComp.flip();
	}

	public void flip(final JComponent comp) {
		this.reversableComp.flip(comp);
	}
}
