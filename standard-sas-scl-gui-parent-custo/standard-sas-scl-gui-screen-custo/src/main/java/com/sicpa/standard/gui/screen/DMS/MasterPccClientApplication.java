package com.sicpa.standard.gui.screen.DMS;

import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXDatePicker;

import com.sicpa.standard.gui.components.windows.WindowFadeInManager;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto.EFontComponent;
import com.sicpa.standard.gui.screen.loader.AbstractApplicationLoader;
import com.sicpa.standard.gui.screen.loader.LoadApplicationScreen;
import com.sicpa.standard.gui.utils.ThreadUtils;

public abstract class MasterPccClientApplication {
	private File file;
	private FileChannel channel;
	private FileLock lock;

	private void initUi() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				Font f = SicpaFont.getFont(10);
				SicpaLookAndFeelCusto.setMenuFont(f.deriveFont(18f));
				SicpaLookAndFeelCusto.setWindowTitleFont(f.deriveFont(20f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.ToolTip, f.deriveFont(14f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.Button, f.deriveFont(20f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.CheckBox, f.deriveFont(20f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.ComboBox, f.deriveFont(25f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.Label, f.deriveFont(20f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.XDataPicker, f.deriveFont(25f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.TitledBorder, f.deriveFont(18f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.SelectionBackground, f.deriveFont(20f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.TableHeader, f.deriveFont(20f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.Table, f.deriveFont(18f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.ToggleButton, f.deriveFont(25f));
				SicpaLookAndFeelCusto.setFont(EFontComponent.TextField, f.deriveFont(22f));
				SicpaLookAndFeelCusto.setMessageFont(f.deriveFont(18f));
			}
		});
	}

	public void start() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
			}
		});

		final AbstractApplicationLoader loader = new AbstractApplicationLoader() {
			@Override
			protected void done() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// give some time to show the close transition
						ThreadUtils.sleepQuietly(450);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								// work around (swing bug) to refresh the
								// langage text in the uimanager
								SicpaLookAndFeelCusto.install();
								JOptionPane.setDefaultLocale(Locale.getDefault());
								JXDatePicker.setDefaultLocale(Locale.getDefault());
								initUi();

								MasterPCCClientFrame f = getMainFrame();
								WindowFadeInManager.fadeIn(f);
							}
						});
					}
				}).start();
			}

			@Override
			protected void loadApplication() {
				try {
					setProgressText("testing if application is already running");

					if (isAppActive()) {
						ThreadUtils.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(null, "Application is already running.");
								System.exit(1);
							}
						});
					}
					setProgress(20);
					ThreadUtils.sleepQuietly(200);

					setProgressText("Config init");
					loadConfigProperties();
					setProgress(50);
					ThreadUtils.sleepQuietly(1000);

					setProgressText("Language init");
					loadLanguageProperties();
					setProgress(100);
					ThreadUtils.sleepQuietly(600);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		};

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				LoadApplicationScreen loadScreen = new LoadApplicationScreen(loader);
				loadScreen.setText(getApplicationName());
				loadScreen.setSize(getLoadingScreenSize());
				loadScreen.start();
			}
		});
	}

	protected abstract void loadLanguageProperties() throws Exception;

	protected abstract void loadConfigProperties() throws Exception;

	protected Dimension getLoadingScreenSize() {
		return new Dimension(700, 480);
	}

	private boolean isAppActive() {
		try {
			this.file = new File("lock.tmp");
			this.channel = new RandomAccessFile(this.file, "rw").getChannel();
			try {
				this.lock = this.channel.tryLock();
			} catch (OverlappingFileLockException e) {
				// already locked
				closeLock();
				return true;
			}

			if (this.lock == null) {
				closeLock();
				return true;
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				// destroy the lock when the JVM is closing
				@Override
				public void run() {
					closeLock();
					MasterPccClientApplication.this.file.delete();
				}
			});
			return false;
		} catch (Exception e) {
			closeLock();
			return true;
		}
	}

	private void closeLock() {
		try {
			this.lock.release();
		} catch (Exception e) {
		}
		try {
			this.channel.close();
		} catch (Exception e) {
		}
	}

	public static String getVERSION() throws IOException, URISyntaxException {
		URL url = ClassLoader.getSystemResource("version/version");
		if (url == null) {
			url = ClassLoader.getSystemResource("version");
		}

		BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI().getPath())));
		String version = br.readLine();
		br.close();
		return version;
	}

	protected abstract String getApplicationName();

	protected abstract MasterPCCClientFrame getMainFrame();

}
