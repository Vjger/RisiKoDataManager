package it.desimone.gsheetsaccess.batch;

import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.gsheetsaccess.dto.ScorePlayer;
import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRidottaRow;
import it.desimone.gsheetsaccess.gsheets.dto.TabellinoGiocatore;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;
import it.desimone.gsheetsaccess.htmlpublisher.HtmlPublisher;
import it.desimone.gsheetsaccess.htmlpublisher.StyleGenerator;
import it.desimone.gsheetsaccess.ranking.RankingCalculator;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.utils.Capitalize;
import it.desimone.utils.MyLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class TabellinoGiocatoreLoader {
	
	private static final String playerData = "\nIl giocatore [%s] %s %s nato il %s ha giocato i seguenti tornei: ";
	private static final String playerDataSenzaData = "\nIl giocatore [%s] %s %s ha giocato i seguenti tornei: ";
	private static final String tournamentData = "%20s organizzato da %34s tra il %s e il %s";
	private static final String tournamentDataRidotti = "%20s - %34s";

	private static Path  inputPath = Paths.get(ResourceWorking.tabellinoLoaderInputAreaPath(), "tabellinoLoaderInput.txt");
	private static Path outputPath = Paths.get(ResourceWorking.tabellinoLoaderOutputAreaPath(), "tabellinoLoaderOutput.txt");
	
	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.INFO);
		MyLogger.getLogger().info("START");
		String year = "2019";
		List<Integer> ids = getInputIdNumeric();
		if (ids != null){
			
			List<TorneoPubblicato> torneiPubblicati = TorneiUtils.caricamentoTornei(year);
			List<ScorePlayer> tabellini = RankingCalculator.elaboraTabellini(year, torneiPubblicati, ids, null);
			
			Collections.sort(tabellini, new Comparator<ScorePlayer>() {
				@Override
				public int compare(ScorePlayer s1, ScorePlayer s2) {
					return s1.getAnagraficaGiocatore().getCognome().toLowerCase().compareTo(s2.getAnagraficaGiocatore().getCognome().toLowerCase());
				}
			});
			
			File tabelliniFile = new File(HtmlPublisher.ROOT_PATH,"tabellini.html");
			
		    Properties p = new Properties();
		    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
		    Velocity.init( p );

			VelocityContext context = new VelocityContext();

			context.put( "scorePlayers", tabellini );
			context.put( "styleGenerator", StyleGenerator.class);
			context.put( "Capitalize", Capitalize.class);
			context.put( "htmlPublisher", HtmlPublisher.class);

			Template template = null;

			try{
			  template = Velocity.getTemplate("TabellinoGiocatore.vm");
			}catch( ResourceNotFoundException rnfe ){
				MyLogger.getLogger().severe(rnfe.getMessage());
			}catch( ParseErrorException pee ){
				MyLogger.getLogger().severe(pee.getMessage());
			}catch( MethodInvocationException mie ){
				MyLogger.getLogger().severe(mie.getMessage());
			}catch( Exception e ){
				MyLogger.getLogger().severe(e.getMessage());
			}


			FileWriter writer = null;
			try {
				writer = new FileWriter(tabelliniFile);
				template.merge( context, writer );
			} catch (IOException e) {
				MyLogger.getLogger().severe(e.getMessage());
			}finally{
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		MyLogger.getLogger().info("END");
	}

	private static List<String> getInputId(){
		List<String> ids = null;	
		try {
			ids = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ids;
	}
	
	private static List<Integer> getInputIdNumeric(){
		List<Integer> ids = null;	
		try {
			List<String> idString = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
			if (idString != null && !idString.isEmpty()){
				ids = new ArrayList<Integer>();
				for (String s: idString){
					Integer i = Integer.valueOf(s);
					ids.add(i);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ids;
	}
	
	public static void main_old(String[] args) {
		MyLogger.setConsoleLogLevel(Level.INFO);
		MyLogger.getLogger().info("START");
		List<String> ids = getInputId();
		if (ids != null){
			
			FileOutputStream fos = null;
			BufferedWriter bw    = null;
			try{
				File outFile = new File(ResourceWorking.tabellinoLoaderOutputAreaPath(), "tabellinoLoaderOutput.txt");
				fos = new FileOutputStream(outFile);
				bw = new BufferedWriter(new OutputStreamWriter(fos));
				
				MyLogger.getLogger().info("Elaborazione di "+ids.size()+" id player");
				List<TabellinoGiocatore> tabellini = new ArrayList<TabellinoGiocatore>();
				for (String id: ids){
					TabellinoGiocatore tabellino = TorneiUtils.getTabellinoPlayer(Integer.valueOf(id.trim()), "2019");
					tabellini.add(tabellino);
				}
	
				for (TabellinoGiocatore tabellino: tabellini){
					AnagraficaGiocatoreRidottaRow anagraficaRidottaGiocatoreRowFrom = tabellino.getAnagraficaRidottaGiocatoreRowFrom();
					Set<TorneiRow> torneiGiocati = tabellino.getTorneiGiocati();
					String player = String.format(playerDataSenzaData, anagraficaRidottaGiocatoreRowFrom.getId(),anagraficaRidottaGiocatoreRowFrom.getNome(), anagraficaRidottaGiocatoreRowFrom.getCognome(), anagraficaRidottaGiocatoreRowFrom.getDataDiNascita()); 
					//MyLogger.getLogger().info(player);
					try {
						//Files.write(outputPath, player.getBytes(), StandardOpenOption.APPEND);
						bw.write(player);
						bw.newLine();
						for (TorneiRow torneoRow: torneiGiocati){
							String torneo = String.format(tournamentDataRidotti, torneoRow.getTipoTorneo(), torneoRow.getOrganizzatore(), torneoRow.getStartDate(), torneoRow.getEndDate());
							//Files.write(outputPath, torneo.getBytes(), StandardOpenOption.APPEND);
							//MyLogger.getLogger().info(torneo);
							bw.write(torneo);
							bw.newLine();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}catch(IOException ioe){
				MyLogger.getLogger().severe(ioe.getMessage());
			}finally{
				try {
					//fos.close();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		MyLogger.getLogger().info("END");
	}

}
