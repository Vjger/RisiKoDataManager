package it.desimone.gsheetsaccess.statistiche;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.htmlpublisher.HtmlPublisher;
import it.desimone.gsheetsaccess.htmlpublisher.StyleGenerator;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.utils.MyLogger;

public class ReportPublisher {

	private static ReportTournaments reportTournaments = new ReportTournaments();
	
	public static final String FOLDER_PATH = ResourceWorking.htmlPagesPath();

	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.INFO);
		String year = "2025";
		Configurator.loadConfiguration(Configurator.Environment.PRODUCTION);
		List<TorneoPubblicato> torneiPubblicati = TorneiUtils.caricamentoTornei(year);
		Stream<TorneoPubblicato> streamTorneiPubblicati = torneiPubblicati.stream();
		streamTorneiPubblicati.forEach(torneoPubblicato -> convertTournamentsIntoMap(torneoPubblicato));

		File statisticheClub = new File(FOLDER_PATH, "statisticheClub"+year+".html");
		statistichePublisher(reportTournaments, statisticheClub, year);
	}

	private static void convertTournamentsIntoMap(TorneoPubblicato torneoPubblicato) {
		List<MatchByYearAndClubValue> values;
		MatchByYearAndClubKey matchByYearAndClubKey = new MatchByYearAndClubKey(torneoPubblicato.getTorneoRow().getOrganizzatore());
		if (reportTournaments.containsStatistics(matchByYearAndClubKey)) {
			values = reportTournaments.getStatistics(matchByYearAndClubKey);
		}else {
			values = new ArrayList<MatchByYearAndClubValue>();
		}
		torneoPubblicato.getPartite().stream().forEach(partita -> manageMatch(partita, torneoPubblicato.getTorneoRow().getTipoTorneo(), values));
		reportTournaments.putStatistics(matchByYearAndClubKey, values);
	}

	private static void manageMatch(PartitaRow partita, String tipoTorneo, List<MatchByYearAndClubValue> values) {
		String dataPartita = partita.getDataTurno();
		MatchByYearAndClubValue matchByYearAndClubValue = new MatchByYearAndClubValue(dataPartita);
		if (values.contains(matchByYearAndClubValue)) {
			matchByYearAndClubValue = values.get(values.indexOf(matchByYearAndClubValue));
		}else {
			values.add(matchByYearAndClubValue);
		}
		matchByYearAndClubValue.setTipoTorneo(tipoTorneo);
		matchByYearAndClubValue.addNumeroTavoli(1);
	}
	
	public static void statistichePublisher(ReportTournaments reportTournaments, File statisticheClub, String year){
		
		MyLogger.getLogger().info("Inizio scrittura statistiche per l'anno "+year);
		
	    Properties p = new Properties();
	    p.setProperty("input.encoding", "UTF-8");
	    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
	    p.setProperty("runtime.log.logsystem.log4j.logger","ReportPublisher");
	    
	    Velocity.init( p );

		VelocityContext context = new VelocityContext();
		Template template = null;

		try{
		  template = Velocity.getTemplate("StatisticheClub2.vm", "UTF-8");
		}catch( ResourceNotFoundException rnfe ){
			MyLogger.getLogger().severe(rnfe.getMessage());
		}catch( ParseErrorException pee ){
			MyLogger.getLogger().severe(pee.getMessage());
		}catch( MethodInvocationException mie ){
			MyLogger.getLogger().severe(mie.getMessage());
		}catch( Exception e ){
			MyLogger.getLogger().severe(e.getMessage());
		}

		context.put( "year", year );
		context.put( "years", Configurator.getTorneiYears() );
		context.put( "reportTournaments", reportTournaments );
		context.put( "styleGenerator", StyleGenerator.class);
		context.put( "htmlPublisher", HtmlPublisher.class);
		context.put( "data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(statisticheClub);
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
	

}
