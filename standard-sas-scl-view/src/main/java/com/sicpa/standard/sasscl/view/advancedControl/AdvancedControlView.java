package com.sicpa.standard.sasscl.view.advancedControl;

import static com.sicpa.standard.gui.components.scroll.SmallScrollBar.createLayerSmallScrollBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.advancedControl.component.IControlViewComponent;

@SuppressWarnings("serial")
public class AdvancedControlView extends JPanel implements ISecuredComponentGetter {

	private final List<IControlViewComponent> views = new ArrayList<>();
	private JPanel panelViews;

	public AdvancedControlView() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0,gap 0 0 0 0"));
		add(createLayerSmallScrollBar(new JScrollPane(getPanelViews())), "grow,push");
	}

	private void addControlComponentToLayout(IControlViewComponent view) {
		getPanelViews().add(view.getComponent(), view.getConstraints());
	}

	public JPanel getPanelViews() {
		if (panelViews == null) {
			panelViews = new JPanel(new MigLayout(""));
		}
		return panelViews;
	}

	public void setViews(Collection<IControlViewComponent> comps) {
		for (IControlViewComponent comp : comps) {
			addView(comp);
		}
	}

	public void addView(IControlViewComponent view) {
		synchronized (views) {
			views.add(view);
		}
		addControlComponentToLayout(view);
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.ADVANCED_CONTROL;
	}

	@Override
	public String getTitle() {
		return "control.advanced";
	}

	@Override
	public JComponent getComponent() {
		return this;
	}
}
