package it.desimone.gsheetsaccess.ui;

import it.desimone.gsheetsaccess.ReportPublisher;
import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.gsheets.dto.TabellinoGiocatore;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;
import it.desimone.gsheetsaccess.htmlpublisher.HtmlPublisher;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.utils.MyLogger;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

public class RisiKoDataManager extends JFrame {
    /**
     * The text area which is used for displaying logging information.
     */
    private JTextArea textArea;
    
    private JButton switchEnvironment = new JButton("Switch");
    private JLabel environmentLabel = new JLabel("UNDEFINED");
    private JButton buttonPublish = new JButton("Pubblica Tornei");
    private JButton buttonPublishHtml = new JButton("Pubblica Html");
    private JButton buttonDelete = new JButton("Elimina Torneo");
    private JButton buttonClear = new JButton("Clear");
    private JLabel annoDiRiferimentoLabel = new JLabel("Anno");
    private JTextField annoRif = new JTextField(4);
    private JLabel torneoToDeleteLabel = new JLabel("ID Torneo");
    private JTextField torneoToDelete = new JTextField(50);
    private JLabel mergeGiocatoreDaLabel = new JLabel("Merge giocatore ID");
    private JTextField idGiocatoreDa = new JTextField(30);
    private JLabel mergeGiocatoreALabel = new JLabel("in ID");
    private JTextField idGiocatoreA = new JTextField(30);
    private JButton buttonMergeGiocatore = new JButton("Merge");
     
    private PrintStream standardOut;
     
    
    public JLabel getEnvironmentLabel() {
		return this.environmentLabel;
	}

	public RisiKoDataManager() {
        super("RisiKo! Data Manager");
        
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        //setUndecorated(true);
        setVisible(true);
         
        textArea = new JTextArea(250, 10);
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
         
        // keeps reference of standard output stream
        standardOut = System.out;
         
        // re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);
 
        // creates the GUI
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        int gridXposition = 0;
        constraints.gridx = gridXposition++;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;
         
        add(buttonPublish, constraints);

        constraints.gridx = gridXposition++;
        add(buttonPublishHtml, constraints);
        
        constraints.gridx = gridXposition++;
        add(buttonClear, constraints);
        
        constraints.gridx = gridXposition++;
        add(switchEnvironment, constraints);
        
        constraints.gridx = gridXposition++;
        add(environmentLabel, constraints);
        
        gridXposition = 0;
        constraints.gridy++;
        
        constraints.gridx = gridXposition++;
        add(annoDiRiferimentoLabel, constraints);
        annoRif.setMinimumSize(new Dimension(40,25));
        constraints.gridx = gridXposition++;
        add(annoRif, constraints);
        
        constraints.gridx = gridXposition++;
        add(torneoToDeleteLabel, constraints);
        
        //torneoToDelete.setPreferredSize(new Dimension(150,30));
        torneoToDelete.setMinimumSize(new Dimension(140,25));
        constraints.gridx = gridXposition++;
        add(torneoToDelete, constraints);
      
        constraints.gridx = gridXposition++;
        add(buttonDelete, constraints);
        
        constraints.gridx = gridXposition++;
        add(mergeGiocatoreDaLabel, constraints);
        
        constraints.gridx = gridXposition++;
        idGiocatoreDa.setMinimumSize(new Dimension(40,25));
        add(idGiocatoreDa, constraints);
        
        constraints.gridx = gridXposition++;
        add(mergeGiocatoreALabel, constraints);
        
        constraints.gridx = gridXposition++;
        idGiocatoreA.setMinimumSize(new Dimension(40,25));
        add(idGiocatoreA, constraints);
        
        constraints.gridx = gridXposition++;
        add(buttonMergeGiocatore, constraints);
        

        
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 10;
        //constraints.gridheight = 4;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
         
        add(new JScrollPane(textArea), constraints);
         
        // adds event handler for button Start
        buttonPublish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //printLog();
                publish();
            }
        });
        
        buttonPublishHtml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //printLog();
                publishHtml();
            }
        });
        
        buttonDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	String idTorneo = torneoToDelete.getText();
            	String year = annoRif.getText();
            	if (idTorneo == null || idTorneo.trim().length() == 0){
            		JOptionPane.showMessageDialog(null, "Indicare l'id del Torneo da cancellare");
            	}else if (year == null || year.trim().length() == 0){
                	JOptionPane.showMessageDialog(null, "Indicare l'anno del Torneo da cancellare");
            	}else{
            		deleteTorneo(idTorneo, year);
            	}
            }
        });
        
        buttonMergeGiocatore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	String idGiocatoreDaTxt = idGiocatoreDa.getText();
            	String idGiocatoreATxt = idGiocatoreA.getText();
            	String year = annoRif.getText();
            	if (idGiocatoreDaTxt == null || idGiocatoreDaTxt.trim().length() == 0 || idGiocatoreATxt == null || idGiocatoreATxt.trim().length() == 0){
            		JOptionPane.showMessageDialog(null, "Indicare gli ID dei giocatori di partenza ed arrivo");
//            	}else if (year == null || year.trim().length() == 0){
//                	JOptionPane.showMessageDialog(null, "Indicare l'anno del Torneo su cui agire");
            	}else{
            		Integer idGiocatoreDaInt, idGiocatoreAInt;
            		try{
            			idGiocatoreDaInt = Integer.valueOf(idGiocatoreDaTxt);
            			idGiocatoreAInt = Integer.valueOf(idGiocatoreATxt);
                		//mergeGiocatore(idGiocatoreDaInt, idGiocatoreAInt, year);
            			try{
            				confirmMerge(idGiocatoreDaInt, idGiocatoreAInt, year);
            			}catch(Exception e){
            				JOptionPane.showMessageDialog(null, e.getClass().getName()+" "+e.getMessage());
            			}
            		}catch(Exception e){
            			JOptionPane.showMessageDialog(null, "ID dei giocatori di partenza e/o di arrivo non numerici");
            		}
            	}
            }
        });
         
        // adds event handler for button Clear
        buttonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	idGiocatoreDa.setText(null);
            	idGiocatoreA.setText(null);
            	torneoToDelete.setText(null);
            	// clears the text area
                try {
                    textArea.getDocument().remove(0, textArea.getDocument().getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        // adds event handler for button Clear
        switchEnvironment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               Configurator.switchEnvironment();
               environmentLabel.setText(Configurator.getEnvironment().name());
            }
        });
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setSize(1080, 320);
        setLocationRelativeTo(null);    // centers on screen
    }
     
    /**
     * Prints log statements for testing in a thread
     */
    private void printLog() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    System.out.println("Time now is " + (new Date()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    
    private void publish() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
            	MyLogger.setConsoleLogLevel(Level.INFO);
            	ReportPublisher.main(null);
            }
        });
        thread.start();
    }
    
    private void publishHtml() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
            	MyLogger.setConsoleLogLevel(Level.INFO);
            	HtmlPublisher.publish(true);
            }
        });
        thread.start();
    }
    
    private void deleteTorneo(final String idTorneo, final String year) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
            	MyLogger.setConsoleLogLevel(Level.INFO);
            	TorneiUtils.deleteTorneo(idTorneo, year);
            }
        });
        thread.start();
    }
    
    private void confirmMerge(final Integer idGiocatoreDa, final Integer idGiocatoreA, final String anno) {
        MyLogger.setConsoleLogLevel(Level.INFO);
        Collection<String> years = null;
        if (anno != null && anno.trim().length() > 0){
        	years = Collections.singletonList(anno.trim());
        }else{
        	List<Integer> anniTorneo = Configurator.getTorneiYears();
        	years = new ArrayList<String>();
        	for (Integer year: anniTorneo){
        		years.add(year.toString());
        	}
        }
        StringBuilder buffer = new StringBuilder(); 
        for (String year: years){
	        TabellinoGiocatore[] torneiGiocati = TorneiUtils.getTabelliniPlayer(idGiocatoreDa, idGiocatoreA, year); 
	        buffer.append("Il giocatore "+torneiGiocati[0].getAnagraficaRidottaGiocatoreRowFrom()+" ha giocato i seguenti tornei nell'anno "+year+"\n");
	        Set<String> torneiFrom = new TreeSet<String>();
	        if (torneiGiocati[0].getTorneiGiocati() != null){
	//	        for (PartitaRow partita: torneiGiocati[0].getPartiteGiocate()){
	//	        	torneiFrom.add(partita.getIdTorneo());
	//	        }
	//	        for (String torneoFrom: torneiFrom){
	//	        	buffer.append(torneoFrom+"\n");
	//	        }
	        	for (TorneiRow torneoRow: torneiGiocati[0].getTorneiGiocati()){
	        		buffer.append(torneoRow.getIdTorneo()+"\n");
	        	}
	        }
	        buffer.append("Il giocatore "+torneiGiocati[1].getAnagraficaRidottaGiocatoreRowFrom()+" ha giocato i seguenti tornei nell'anno "+year+"\n");
	        Set<String> torneiTo = new TreeSet<String>();
	        if (torneiGiocati[1].getTorneiGiocati() != null){
	        	for (TorneiRow torneoRow: torneiGiocati[1].getTorneiGiocati()){
	        		buffer.append(torneoRow.getIdTorneo()+"\n");
	        	}
	        }
        }
        buffer.append("\n Confermi il merge dei dati dal giocatore con id ["+idGiocatoreDa+"] a quello con id ["+idGiocatoreA+"]?");
        
        int response = JOptionPane.showConfirmDialog(null, buffer.toString(), "Conferma merge", JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.OK_OPTION){
        	mergeGiocatore(idGiocatoreDa, idGiocatoreA, anno);
        }
    }
    
    private void mergeGiocatore(final Integer idGiocatoreDa, final Integer idGiocatoreA, final String anno) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
            	MyLogger.setConsoleLogLevel(Level.INFO);
            	if (anno != null && anno.trim().length() > 0){
            		TorneiUtils.mergePlayer(idGiocatoreDa, idGiocatoreA, anno);            
	            }else{
                	List<Integer> anniTorneo = Configurator.getTorneiYears();
                	for (Integer year: anniTorneo){
                		TorneiUtils.mergePlayer(idGiocatoreDa, idGiocatoreA, year.toString());  
                	}
	            }
            	TorneiUtils.deletePlayer(idGiocatoreDa);
	        }
        });
        thread.start();
    }
     
    /**
     * Runs the program
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	RisiKoDataManager risiKoDataManager = new RisiKoDataManager();
            	risiKoDataManager.setVisible(true);
            	risiKoDataManager.getEnvironmentLabel().setText(Configurator.getEnvironment().name());
            }
        });
    }
}