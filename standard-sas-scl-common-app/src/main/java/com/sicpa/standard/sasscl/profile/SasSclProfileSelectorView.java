package com.sicpa.standard.sasscl.profile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import com.sicpa.standard.client.common.app.profile.IProfileSelectorListener;
import com.sicpa.standard.client.common.app.profile.Profile;
import com.sicpa.standard.client.common.app.profile.ProfileSelectedEvent;
import com.sicpa.standard.client.common.app.profile.ProfileSelectorView;
import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;

import net.miginfocom.swing.MigLayout;

public class SasSclProfileSelectorView extends ProfileSelectorView {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel profilesPanel;
	private final AtomicBoolean profileSelected = new AtomicBoolean(false);
	private final List<IProfileSelectorListener> selectorListeners = new ArrayList<>();
	
	@Override
	public JPanel getProfilesPanel() {
		if (profilesPanel == null) {
			profilesPanel = new JPanel(new MigLayout());
			for (Profile profile : SasSclProfile.getAllAvailableProfiles()) {
				profilesPanel.add(createProfileButton(profile), "wrap,grow,pushx");
			}
		}
		return profilesPanel;
	}
	
	@Override
	public void addProfileSelectorListener(IProfileSelectorListener listener) {
		selectorListeners.add(listener);
	}

	@Override
	public void removeProfileSelectorListener(IProfileSelectorListener listener) {
		selectorListeners.remove(listener);
	}
	
	private AbstractButton createProfileButton(final Profile profile) {
		ToggleImageAndTextButton button = new ToggleImageAndTextButton(profile.getName() + "\n"
				+ profile.getDescription());
		button.setRatio(0.8f);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectProfile(profile);

			}
		});
		return button;
	}
	
	private void selectProfile(Profile profile) {
		synchronized (profileSelected) {
			if (!profileSelected.get()) {
				profileSelected.set(true);
				fireProfileSelected(profile);
				setVisible(false);
				dispose();
			}
		}
	}
	
	private void fireProfileSelected(Profile profile) {
		synchronized (selectorListeners) {
			for (IProfileSelectorListener listener : selectorListeners) {
				listener.onProfileSelected(new ProfileSelectedEvent(profile));
			}
		}
	}

}
