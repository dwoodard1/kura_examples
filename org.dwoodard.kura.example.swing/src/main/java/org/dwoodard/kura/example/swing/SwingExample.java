package org.dwoodard.kura.example.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Code copied from: https://maxrohde.com/2010/06/06/slow-startup-for-swing-applications-in-eclipse-equinox/
 */
public class SwingExample {
	private static final Logger logger = LoggerFactory.getLogger(SwingExample.class);
	
	protected void activate() {
		logger.info("Activating SwingExample");
		Runnable launcher = new Runnable() {
            @Override
            public void run() {
            	JFrame aWindow = new JFrame("Swing Test OSGi");
            	int windowWidth = 420;
	            int windowHeight = 170;
	            aWindow.setBounds(55, 150,
	            windowWidth, windowHeight);
	            aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            aWindow.setVisible(true);
            }
		};
		SwingUtilities.invokeLater(launcher);
	}
	
	protected void deactivate() {
		logger.info("Deactivating SwingExample");
	}

}
