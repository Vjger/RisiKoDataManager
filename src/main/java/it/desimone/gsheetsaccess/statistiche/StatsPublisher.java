package it.desimone.gsheetsaccess.statistiche;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import it.desimone.ftputils.AlterVistaUtil;
import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;
import it.desimone.gsheetsaccess.htmlpublisher.StyleGenerator;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.utils.MyLogger;

public class StatsPublisher {

	private static ReportTournaments reportTournaments = new ReportTournaments();
	
	public static final String FOLDER_PATH = ResourceWorking.htmlPagesPath();

	public static void main (String[] args) {
		publish(false);
	}
	
	public static void publish(boolean withUpload) {
		MyLogger.setConsoleLogLevel(Level.INFO);
		
		Configurator.loadConfiguration(Configurator.Environment.PRODUCTION);
		
		List<Integer> years = Configurator.getTorneiYears();
		//years = Collections.singletonList(2025);
		for (Integer year: years) {
			List<TorneoPubblicato> torneiPubblicati = TorneiUtils.caricamentoTornei(year.toString());
			Stream<TorneoPubblicato> streamTorneiPubblicati = torneiPubblicati.stream();
			streamTorneiPubblicati.forEach(torneoPubblicato -> getStatistics(torneoPubblicato));
		}
		
		for (Integer year: years) {
			File statisticheClub = new File(FOLDER_PATH, "statisticheClub"+year+".html");
			statistichePublisher(reportTournaments, statisticheClub, "StatisticheClub4_Tab.vm", year.toString());

			File statistichePlayer = new File(FOLDER_PATH, "statisticheGiocatori"+year+".html");
			statistichePublisher(reportTournaments, statistichePlayer, "StatisticheGiocatori_Tab.vm", year.toString());
			
			if (withUpload){
				try{
					uploadFiles(statisticheClub);
					uploadFiles(statistichePlayer);
				}catch(IOException ioe){
					MyLogger.getLogger().severe("Errore nel ftp dei file: "+ioe.getMessage());
				}
			}
		}
		
		File statisticheAnnuali = new File(FOLDER_PATH, "statisticheAnnuali.html");
		statistichePublisher(reportTournaments, statisticheAnnuali, "StatisticheAnnuali2_Tab.vm", null);
		
		File statisticheTab = new File(FOLDER_PATH, "statisticheTab.html");
		statistichePublisher(reportTournaments, statisticheTab, "StatisticheTab.vm", "2025");
		
		if (withUpload){
			try{
				uploadFiles(statisticheAnnuali);
			}catch(IOException ioe){
				MyLogger.getLogger().severe("Errore nel ftp dei file: "+ioe.getMessage());
			}
		}
	}

	private static void uploadFiles(File file) throws IOException{
		AlterVistaUtil.uploadInRoot(Collections.singletonList(file));
	}
	
	private static void getStatistics(TorneoPubblicato torneoPubblicato) {
		getStatisticsByYearAndClub(torneoPubblicato);
		getStatisticsByYear(torneoPubblicato);		
	}
	
	private static void getStatisticsByYearAndClub(TorneoPubblicato torneoPubblicato) {
		List<MatchByYearAndClubValue> values;
		MatchByYearAndClubKey matchByYearAndClubKey = new MatchByYearAndClubKey(torneoPubblicato.getTorneoRow().getOrganizzatore());
		if (reportTournaments.containsStatisticsByYearAndClub(matchByYearAndClubKey)) {
			values = reportTournaments.getStatistics(matchByYearAndClubKey);
		}else {
			values = new ArrayList<MatchByYearAndClubValue>();
		}
		torneoPubblicato.getPartite().stream().forEach(partita -> manageMatch(partita, torneoPubblicato.getTorneoRow().getTipoTorneo(), values));
		reportTournaments.putStatistics(matchByYearAndClubKey, values);
	}
	
	private static void getStatisticsByYear(TorneoPubblicato torneoPubblicato) {
		Object[] obj = getAnnualStatisticsByYearTournament(torneoPubblicato);
		if (!ArrayUtils.isEmpty(obj)) {
			Integer yearOfTournament = (Integer) obj[0];
			AnnualStatistics annualStatisticsByYearTournament = (AnnualStatistics) obj[1];
			annualStatisticsByYearTournament.addNumberOfEvents(1);
			annualStatisticsByYearTournament.addPlayer(torneoPubblicato.getIdPartecipanti(), torneoPubblicato.getIdTorneo());
			reportTournaments.putAnnualStatistics(yearOfTournament, annualStatisticsByYearTournament);
		}
		torneoPubblicato.getPartite().stream().forEach(partita -> manageAnnualStatistics(torneoPubblicato, partita));
	}

	private static Object[] getAnnualStatisticsByYearTournament(TorneoPubblicato torneoPubblicato) {
		Integer yearOfTournament = null;
		AnnualStatistics annualStatisticsByYearTournament = null;
		TorneiRow torneiRow = torneoPubblicato.getTorneoRow();
		String endDate = torneiRow.getEndDate();
		if (StringUtils.isNotBlank(endDate)) {
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
			LocalDate localDate = LocalDate.parse(endDate, inputFormatter);
			yearOfTournament = localDate.getYear();
			
			if (reportTournaments.containsStatisticsByYear(yearOfTournament)) {
				annualStatisticsByYearTournament = reportTournaments.getAnnualStatistics(yearOfTournament);
			}else {
				annualStatisticsByYearTournament = new AnnualStatistics();
			}
		}		
		return new Object[] {yearOfTournament, annualStatisticsByYearTournament};
	}
	
	private static void manageAnnualStatistics(TorneoPubblicato torneoPubblicato, PartitaRow partita) {
		String organizzatore = torneoPubblicato.getTorneoRow().getOrganizzatore();
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
		LocalDate localDate = LocalDate.parse( partita.getDataTurno(), inputFormatter);
		Integer yearOfMatch = localDate.getYear();
		
		AnnualStatistics annualStatistics;
		if (reportTournaments.containsStatisticsByYear(yearOfMatch)) {
			annualStatistics = reportTournaments.getAnnualStatistics(yearOfMatch);
		}else {
			annualStatistics = new AnnualStatistics();
		}
		annualStatistics.addNumberOfMatches(1);
		annualStatistics.addClub(organizzatore);
		annualStatistics.addDate(organizzatore, partita.getDataTurno());
		annualStatistics.addPlayer(partita.getIdGiocatore1());
		annualStatistics.addPlayer(partita.getIdGiocatore2());
		annualStatistics.addPlayer(partita.getIdGiocatore3());
		if (Objects.nonNull(partita.getIdGiocatore4())) {
			annualStatistics.addPlayer(partita.getIdGiocatore4());
		}
		if (Objects.nonNull(partita.getIdGiocatore5())) {
			annualStatistics.addPlayer(partita.getIdGiocatore5());
		}
		reportTournaments.putAnnualStatistics(yearOfMatch, annualStatistics);
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
	
	public static void statistichePublisher(ReportTournaments reportTournaments, File statisticheFile, String templateName, String year){
		
		MyLogger.getLogger().info("Inizio scrittura statistiche per l'anno "+year);
		
	    Properties p = new Properties();
	    p.setProperty("input.encoding", "UTF-8");
	    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
	    p.setProperty("runtime.log.logsystem.log4j.logger","ReportPublisher");
	    
	    Velocity.init( p );

		VelocityContext context = new VelocityContext();
		Template template = null;

		try{
		  template = Velocity.getTemplate(templateName, "UTF-8");
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
		context.put( "data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(statisticheFile);
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
