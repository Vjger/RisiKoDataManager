package it.desimone.gsheetsaccess.htmlpublisher;

import it.desimone.ftputils.AlterVistaUtil;
import it.desimone.gsheetsaccess.analyzer.ClubAnalysis;
import it.desimone.gsheetsaccess.analyzer.ClubAnalysis.ClubPlayerData;
import it.desimone.gsheetsaccess.analyzer.TournamentsAnalyzer;
import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.gsheetsaccess.dto.ScorePlayer;
import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRidottaRow;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRow;
import it.desimone.gsheetsaccess.gsheets.dto.ClassificheRow;
import it.desimone.gsheetsaccess.gsheets.dto.LastUpdateRow;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.facade.ExcelGSheetsBridge;
import it.desimone.gsheetsaccess.ranking.RankingCalculator;
import it.desimone.gsheetsaccess.ranking.RankingData;
import it.desimone.gsheetsaccess.statistiche.StatsPublisher;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;
import it.desimone.utils.Capitalize;
import it.desimone.utils.MyLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class HtmlPublisher {
	
	public static final String ROOT_PATH = ResourceWorking.htmlPagesPath();
	public static final DateFormat lastUpdateTimeFormat = new SimpleDateFormat("dd/MM/yyyyHHmmss");

	public static class FilesToPublish{
		
		private String year;
		private File ranking;
		private File listaTornei;
		private List<File> tornei;
		private List<File> tabelliniClub;
		private List<ScorePlayer> tabellini;
		
		public FilesToPublish(String year){
			this.year = year;
		}
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public File getRanking() {
			return ranking;
		}
		public void setRanking(File ranking) {
			this.ranking = ranking;
		}
		public File getListaTornei() {
			return listaTornei;
		}
		public void setListaTornei(File listaTornei) {
			this.listaTornei = listaTornei;
		}
		public List<File> getTornei() {
			return tornei;
		}
		public void setTornei(List<File> tornei) {
			this.tornei = tornei;
		}
		public List<ScorePlayer> getTabellini() {
			return tabellini;
		}
		public void setTabellini(List<ScorePlayer> tabellini) {
			this.tabellini = tabellini;
		}
		public List<File> getTabelliniClub() {
			return tabelliniClub;
		}
		public void setTabelliniClub(List<File> tabelliniClub) {
			this.tabelliniClub = tabelliniClub;
		}
	}
	
	public static class TournamentsToPublish{
		private List<TorneoPubblicato> torneiPubblicati;
		private List<TorneoPubblicato> torneiDaMettereOnline;
		private Date maxDate;
		private LastUpdateRow lastUpdateRow;
		
		public TournamentsToPublish(List<TorneoPubblicato> torneiPubblicati,
				List<TorneoPubblicato> torneiDaMettereOnline, Date maxDate) {
			super();
			this.torneiPubblicati = torneiPubblicati;
			this.torneiDaMettereOnline = torneiDaMettereOnline;
			this.maxDate = maxDate;
		}
		public TournamentsToPublish(List<TorneoPubblicato> torneiPubblicati,
				List<TorneoPubblicato> torneiDaMettereOnline, Date maxDate, LastUpdateRow lastUpdateRow) {
			this(torneiPubblicati, torneiDaMettereOnline, maxDate);
			this.lastUpdateRow = lastUpdateRow;
		}
		public List<TorneoPubblicato> getTorneiPubblicati() {
			return torneiPubblicati;
		}
		public void setTorneiPubblicati(List<TorneoPubblicato> torneiPubblicati) {
			this.torneiPubblicati = torneiPubblicati;
		}
		public List<TorneoPubblicato> getTorneiDaMettereOnline() {
			return torneiDaMettereOnline;
		}
		public void setTorneiDaMettereOnline(
				List<TorneoPubblicato> torneiDaMettereOnline) {
			this.torneiDaMettereOnline = torneiDaMettereOnline;
		}
		public Date getMaxDate() {
			return maxDate;
		}
		public void setMaxDate(Date maxDate) {
			this.maxDate = maxDate;
		}
		public LastUpdateRow getLastUpdateRow() {
			return lastUpdateRow;
		}
		public void setLastUpdateRow(LastUpdateRow lastUpdateRow) {
			this.lastUpdateRow = lastUpdateRow;
		}
		public boolean isEmpty(){
			return torneiDaMettereOnline == null || torneiDaMettereOnline.isEmpty();
		}
	}
	
	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.INFO);
		Configurator.loadConfiguration(Configurator.Environment.PRODUCTION);
//		File folderTabelliniClub = new File(FOLDER_PATH+File.separator+"TABELLINI_CLUB");
//		String year = "2019";
//		List<TorneoPubblicato> torneiPubblicati = TorneiUtils.caricamentoTornei(year);
//		ClubAnalysis clubAnalysis = TournamentsAnalyzer.elaboraPartecipazioniTornei(year, torneiPubblicati);
//		tabelliniClubPublisher(clubAnalysis, year, folderTabelliniClub);
		publish(false);
	}

	public static void publish(boolean withUpload) {
		MyLogger.getLogger().info("INIZIO elaborazione");
		
		TorneiUtils.resetCacheTornei();
		Map<Integer, TournamentsToPublish> newToPublish = publishingAnalyzer();
		Set<Integer> years = newToPublish.keySet();
		
		List<FilesToPublish> fileDaPubblicare = new ArrayList<FilesToPublish>();
		for (Integer year: years){
			TournamentsToPublish tournamentsToPublishByYear = newToPublish.get(year);
			if (!tournamentsToPublishByYear.isEmpty()){
				MyLogger.getLogger().info("INIZIO Elaborazione Tornei del "+year);
				
				FilesToPublish filesToPublish = publishPerYear(year.toString(), tournamentsToPublishByYear);
				fileDaPubblicare.add(filesToPublish);
				
				//ResourceWorking.setLastTournamentDate(year.toString(), lastUpdateTimeFormat.format(tournamentsToPublishByYear.getMaxDate()));
				LastUpdateRow lastUpdateRow = tournamentsToPublishByYear.getLastUpdateRow();
				if (lastUpdateRow == null) {
					lastUpdateRow = new LastUpdateRow();
					lastUpdateRow.setAnnoRiferimento(year);
				}
				lastUpdateRow.setLastElaboration(ExcelGSheetsBridge.dfUpdateTime.format(tournamentsToPublishByYear.getMaxDate()));
				TorneiUtils.insertOrUpdateLastUpdateRow(lastUpdateRow);
				
				MyLogger.getLogger().info("FINE Elaborazione Tornei del "+year);
			}
		}
		
		File doppioniSospetti = new File(ROOT_PATH, SitePages.DOPPIONI);
		List<ScorePlayer> allTabellini = new ArrayList<ScorePlayer>();
		for (FilesToPublish fileToPublish: fileDaPubblicare){
			
			allTabellini.addAll(fileToPublish.getTabellini());
		
			if (withUpload){
				try{
					uploadFiles(fileToPublish.getRanking(), fileToPublish.getListaTornei(), fileToPublish.getTornei(), fileToPublish.getTabelliniClub());
				}catch(IOException ioe){
					MyLogger.getLogger().severe("Errore nel ftp dei file: "+ioe.getMessage());
				}
			}
		}
		//doppioniSospetti(allTabellini, doppioniSospetti);
		//doppioniSospetti(overallTabellini(), doppioniSospetti);
		doppioniSospetti(TorneiUtils.getOverallTabellini(), doppioniSospetti);
		if (withUpload){
			try {
				AlterVistaUtil.uploadInRoot(Collections.singletonList(doppioniSospetti));
			} catch (IOException ioe) {
				MyLogger.getLogger().severe("Errore nel ftp dei file: "+ioe.getMessage());
			}
		}
		
		StatsPublisher.publish(withUpload);
		MyLogger.getLogger().info("Fine elaborazione");
	}
	
	public static FilesToPublish publishPerYear(String year, TournamentsToPublish tournamentsToPublishByYear) {
		MyLogger.getLogger().info("Inizio elaborazione");
		
		MyLogger.getLogger().info("Inizio estrazione tornei pubblicati");
		List<TorneoPubblicato> torneiPubblicati = tournamentsToPublishByYear.getTorneiPubblicati();
	
		Collections.sort(torneiPubblicati, new Comparator<TorneoPubblicato>() {

			@Override
			public int compare(TorneoPubblicato o1, TorneoPubblicato o2) {
				int result = 0;
				try{
					Date endDate1 = ExcelGSheetsBridge.dfDateTorneo.parse(o1.getTorneoRow().getEndDate());
					Date endDate2 = ExcelGSheetsBridge.dfDateTorneo.parse(o2.getTorneoRow().getEndDate());
					result = endDate2.compareTo(endDate1);
				}catch(ParseException pe){
					MyLogger.getLogger().severe(pe.getMessage());
				}
				//return o2.getIdTorneo().compareTo(o1.getIdTorneo());
				return result;
			}
		});
		File listaTornei = new File(ResourceWorking.listTournamentsPath(), "listaTornei"+year+".html");
		listaTorneiPublisher(torneiPubblicati, listaTornei, year);
		
		MyLogger.getLogger().info("Inizio elaborazione tabellini");
//		List<ScorePlayer> tabellini = RankingCalculator.elaboraTabellini(year, torneiPubblicati, null);
		RankingData rankingData = RankingCalculator.elaboraRanking(year, torneiPubblicati, null);
		List<ScorePlayer> tabellini = rankingData.getTabellini();
		
		assegnaNominativiAPartita(torneiPubblicati, year);
		
		File ranking = new File(ResourceWorking.listRankingsPath(),"ranking"+year+".html");
		rankingPublisher(tabellini, ranking, year, rankingData);
		File folderTornei = new File(ROOT_PATH+File.separator+"TORNEI");
		List<File> torneiHtml = torneiPublisher(year, tournamentsToPublishByYear.getTorneiDaMettereOnline(), folderTornei);
		
		File folderTabelliniClub = new File(ROOT_PATH+File.separator+"TABELLINI_CLUB");
		//ClubAnalysis clubAnalysis = TournamentsAnalyzer.elaboraPartecipazioniTornei(year, torneiPubblicati, tournamentsToPublishByYear.getTorneiDaMettereOnline());
		ClubAnalysis clubAnalysis = TournamentsAnalyzer.elaboraPartecipazioniTornei(year, torneiPubblicati, tournamentsToPublishByYear.getTorneiDaMettereOnline(), tabellini);
		List<File> tabelliniClub = tabelliniClubPublisher(clubAnalysis, year, folderTabelliniClub);
		
		MyLogger.getLogger().info("FINE elaborazione");
		
		FilesToPublish filesToPublish = new FilesToPublish(year);
		filesToPublish.setListaTornei(listaTornei);
		filesToPublish.setRanking(ranking);
		filesToPublish.setTornei(torneiHtml);
		filesToPublish.setTabellini(tabellini);
		filesToPublish.setTabelliniClub(tabelliniClub);
		
		return filesToPublish;
	}

	public static Map<Integer, TournamentsToPublish> publishingAnalyzer(){
		
		Map<Integer, TournamentsToPublish> result = new HashMap<Integer, HtmlPublisher.TournamentsToPublish>();
		
		List<Integer> years = Configurator.getTorneiYears();
		
		//LastUpdateData lastUpdateData = LastUpdateData.getInstance(); 
		LastUpdateData lastUpdateData = new LastUpdateData(); 
		for (Integer year: years){
			MyLogger.getLogger().info("Inizio estrazione tornei pubblicati per l'anno "+year);
			List<TorneoPubblicato> torneiPubblicati = TorneiUtils.getTorneiPubblicati(year.toString());
			
//			String lastDateString = ResourceWorking.getLastTournamentDate(year.toString());
//			Date lastDateOLD = null;
//			if (lastDateString != null && !lastDateString.trim().isEmpty()){
//				try {
//					lastDateOLD = lastUpdateTimeFormat.parse(lastDateString);
//				} catch (ParseException e) {
//					MyLogger.getLogger().severe("Errore nel parsing della data di ultima elaborazione per l'anno "+year +" sulla stringa "+lastDateString+": "+e.getMessage() );
//				}
//			}
			//Date lastDate = lastUpdateData.getLastTournamentDate(year);
			Date lastDate = lastUpdateData.getLastTournamentDateByUpdateRow(year);
			MyLogger.getLogger().info("Ultima data pubblicazione per l'anno "+year+":"+lastDate);
			Date maxDate = null;
			List<TorneoPubblicato> torneiDaMettereOnline = new ArrayList<TorneoPubblicato>();
			for (TorneoPubblicato torneo: torneiPubblicati){
				MyLogger.getLogger().fine("Inizio elaborazione torneo "+torneo.getIdTorneo());
				try {
					Date updateTime = ExcelGSheetsBridge.dfUpdateTime.parse(torneo.getTorneoRow().getUpdateTime());
					if (lastDate == null || updateTime.after(lastDate)){
						torneiDaMettereOnline.add(torneo);
						if (maxDate == null || maxDate.before(updateTime)){
							maxDate = updateTime;
						}
					}
				} catch (ParseException e) {
					MyLogger.getLogger().severe("Errore nel parsing della data di update per l'anno "+year +" del torneo con id "+torneo.getTorneoRow().getIdTorneo()+" sulla stringa "+torneo.getTorneoRow().getUpdateTime()+": "+e.getMessage() );
				}
			}
			//TournamentsToPublish tournamentsToPublish = new TournamentsToPublish(torneiPubblicati, torneiDaMettereOnline, maxDate);
			TournamentsToPublish tournamentsToPublish = new TournamentsToPublish(torneiPubblicati, torneiDaMettereOnline, maxDate, lastUpdateData.getLastUpdateRow(year));
			MyLogger.getLogger().info("Tornei da pubblicare per l'anno "+year+": "+tournamentsToPublish.torneiDaMettereOnline.size());
			result.put(year, tournamentsToPublish);
		}
		return result;
	}

	
	
	private static void assegnaNominativiAPartita(List<TorneoPubblicato> torneiPubblicati, String year){
		List<AnagraficaGiocatoreRow> anagraficheGiocatoriRow = TorneiUtils.getAllAnagraficheGiocatori(year);
		
		for (TorneoPubblicato torneo: torneiPubblicati){
			List<PartitaRow> partite = torneo.getPartite();
			for (PartitaRow partita: partite){
				AnagraficaGiocatoreRow vincitore = TorneiUtils.findAnagraficaById(anagraficheGiocatoriRow, partita.getIdGiocatoreVincitore());
				AnagraficaGiocatoreRow giocatore1 = TorneiUtils.findAnagraficaById(anagraficheGiocatoriRow, partita.getIdGiocatore1());
				AnagraficaGiocatoreRow giocatore2 = TorneiUtils.findAnagraficaById(anagraficheGiocatoriRow, partita.getIdGiocatore2());
				AnagraficaGiocatoreRow giocatore3 = TorneiUtils.findAnagraficaById(anagraficheGiocatoriRow, partita.getIdGiocatore3());
				AnagraficaGiocatoreRow giocatore4 = TorneiUtils.findAnagraficaById(anagraficheGiocatoriRow, partita.getIdGiocatore4());
				AnagraficaGiocatoreRow giocatore5 = TorneiUtils.findAnagraficaById(anagraficheGiocatoriRow, partita.getIdGiocatore5());
				if (vincitore != null){
					partita.setNominativoVincitore(Capitalize.capitalize(vincitore.getNome())+" "+Capitalize.capitalize(vincitore.getCognome()));
				}else{
					partita.setNominativoVincitore("");
				}
				if (giocatore1 != null){
					partita.setNominativoGiocatore1(Capitalize.capitalize(giocatore1.getNome())+" "+Capitalize.capitalize(giocatore1.getCognome()));
				}else{
					partita.setNominativoGiocatore1("");
				}
				if (giocatore2 != null){
					partita.setNominativoGiocatore2(Capitalize.capitalize(giocatore2.getNome())+" "+Capitalize.capitalize(giocatore2.getCognome()));
				}else{
					partita.setNominativoGiocatore2("");
				}
				if (giocatore3 != null){
					partita.setNominativoGiocatore3(Capitalize.capitalize(giocatore3.getNome())+" "+Capitalize.capitalize(giocatore3.getCognome()));
				}else{
					partita.setNominativoGiocatore3("");
				}
				if (giocatore4 != null){
					partita.setNominativoGiocatore4(Capitalize.capitalize(giocatore4.getNome())+" "+Capitalize.capitalize(giocatore4.getCognome()));
				}else{
					partita.setNominativoGiocatore4("");
				}
				if (giocatore5 != null){
					partita.setNominativoGiocatore5(Capitalize.capitalize(giocatore5.getNome())+" "+Capitalize.capitalize(giocatore5.getCognome()));
				}else{
					partita.setNominativoGiocatore5("");
				}
			}
			
			List<ClassificheRow> classifica = torneo.getClassifica();
			if (classifica != null){
				for (ClassificheRow rigaClassifica: classifica){
					AnagraficaGiocatoreRow giocatore = TorneiUtils.findAnagraficaById(anagraficheGiocatoriRow, rigaClassifica.getIdGiocatore());
					rigaClassifica.setNominativoGiocatore(Capitalize.capitalize(giocatore.getNome())+" "+Capitalize.capitalize(giocatore.getCognome()));
				}
			}
		}		
	}
	
	public static void rankingPublisher(List<ScorePlayer> tabellini, File ranking, String year, RankingData rankingData){

		MyLogger.getLogger().info("Inizio scrittura file");
	    Properties p = new Properties();
	    p.setProperty("input.encoding", "UTF-8");
	    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
	    Velocity.init( p );

		VelocityContext context = new VelocityContext();

		context.put( "year", year );
		context.put( "years", Configurator.getTorneiYears() );
		context.put( "mappaSoglieTipoTorneo", rankingData.getMappaSoglieTipoTorneo() );
		context.put( "mappaConteggiTipoTorneo", rankingData.getMappaConteggiTipoTorneo() );
		context.put( "TipoTorneo", TipoTorneo.class );
		context.put( "Enum", Enum.class);
		context.put( "scorePlayers", tabellini );
		context.put( "styleGenerator", StyleGenerator.class);
		context.put( "Capitalize", Capitalize.class);
		context.put( "htmlPublisher", HtmlPublisher.class);
		context.put( "data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

		Template template = null;

		try{
		  template = Velocity.getTemplate("Ranking.vm", "UTF-8");
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
			writer = new FileWriter(ranking);
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
	
	public static List<File> tabelliniClubPublisher(ClubAnalysis clubAnalysis, String year, File folderTabelliniClub){

		MyLogger.getLogger().info("Inizio scrittura file");
	    Properties p = new Properties();
	    p.setProperty("input.encoding", "UTF-8");
	    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
	    Velocity.init( p );

		VelocityContext context = new VelocityContext();

		context.put( "year", year );
		context.put( "styleGenerator", StyleGenerator.class);
		context.put( "Capitalize", Capitalize.class);
		context.put( "htmlPublisher", HtmlPublisher.class);

		Template template = null;

		try{
		  template = Velocity.getTemplate("TabellinoClub2.vm", "UTF-8");
		}catch( ResourceNotFoundException rnfe ){
			MyLogger.getLogger().severe(rnfe.getMessage());
		}catch( ParseErrorException pee ){
			MyLogger.getLogger().severe(pee.getMessage());
		}catch( MethodInvocationException mie ){
			MyLogger.getLogger().severe(mie.getMessage());
		}catch( Exception e ){
			MyLogger.getLogger().severe(e.getMessage());
		}

		List<File> result = new ArrayList<File>();
		for (String club: clubAnalysis.getClubs()){
			List<ClubPlayerData> clubPlayersData = clubAnalysis.getPlayerDataByClub(club);
			int maxSize = 0;
			if (CollectionUtils.isNotEmpty(clubPlayersData)) {
				try{
					maxSize = clubPlayersData.get(0).getTorneiDisputati().size();
				} catch (IndexOutOfBoundsException e) {
					MyLogger.getLogger().severe(e.getMessage()+"anno: "+year+ "club: "+club);
					throw e;
				}
			}
			int width = 200+(Math.min(1600, maxSize*160));
			context.put( "clubPlayersData", clubPlayersData);
			context.put( "club", club);
			context.put( "width", width);
			File tabellinoClubHtml = new File(folderTabelliniClub, "Tabellino"+getTorneoPage(club)+"_"+year+".html");
			result.add(tabellinoClubHtml);
			FileWriter writer = null;
			try {
				writer = new FileWriter(tabellinoClubHtml);
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
		return result;
	}
	
	public static List<File> torneiPublisher(String year, List<TorneoPubblicato> torneiPubblicati, File folderTornei){
		
//        BasicConfigurator.configure();
//        Logger log = Logger.getLogger( "HtmlPublisher" );
//        log.info("Log4jLoggerExample: ready to start velocity");
		
		MyLogger.getLogger().info("Inizio scrittura file");
	    Properties p = new Properties();
	    p.setProperty("input.encoding", "UTF-8");
	    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
        p.setProperty("runtime.log.logsystem.log4j.logger","HtmlPublisher");
	    
	    Velocity.init( p );

		VelocityContext context = new VelocityContext();
		Template template = null;

		try{
		  template = Velocity.getTemplate("Torneo.vm", "UTF-8");
		}catch( ResourceNotFoundException rnfe ){
			MyLogger.getLogger().severe(rnfe.getMessage());
		}catch( ParseErrorException pee ){
			MyLogger.getLogger().severe(pee.getMessage());
		}catch( MethodInvocationException mie ){
			MyLogger.getLogger().severe(mie.getMessage());
		}catch( Exception e ){
			MyLogger.getLogger().severe(e.getMessage());
		}


		List<File> result = new ArrayList<File>();
		for (TorneoPubblicato torneo: torneiPubblicati){
			MyLogger.getLogger().info("Inizio elaborazione torneo "+torneo.getIdTorneo());
					context.put( "torneo", torneo );
					context.put( "styleGenerator", StyleGenerator.class);
				
					File torneoHtml = new File(folderTornei, getTorneoPage(torneo.getIdTorneo())+".html");
					FileWriter writer = null;
					try {
						writer = new FileWriter(torneoHtml);
						template.merge( context, writer );
						result.add(torneoHtml);
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
		return result;
	}

	public static void listaTorneiPublisher(List<TorneoPubblicato> torneiPubblicati, File listaTornei, String year){
		
		MyLogger.getLogger().info("Inizio scrittura file. Primo Torneo: "+torneiPubblicati.get(0).getIdTorneo());
		MyLogger.getLogger().info("Inizio scrittura file. Ultimo Torneo: "+torneiPubblicati.get(torneiPubblicati.size()-1).getIdTorneo());
		
		Set<String> clubs = new TreeSet<String>();
		for (TorneoPubblicato torneo: torneiPubblicati){
			clubs.add(torneo.getTorneoRow().getOrganizzatore());
		}
		
	    Properties p = new Properties();
	    p.setProperty("input.encoding", "UTF-8");
	    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
	    p.setProperty("runtime.log.logsystem.log4j.logger","HtmlPublisher");
	    
	    Velocity.init( p );

		VelocityContext context = new VelocityContext();
		Template template = null;

		try{
		  template = Velocity.getTemplate("ListaTornei.vm", "UTF-8");
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
		context.put( "tornei", torneiPubblicati );
		context.put( "clubs", clubs );
		context.put( "styleGenerator", StyleGenerator.class);
		context.put( "htmlPublisher", HtmlPublisher.class);
		context.put( "data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(listaTornei);
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
	
	public static void doppioniSospetti(List<ScorePlayer> tabellini, File anagraficheDaVerificare){
		MyLogger.getLogger().info("Verifica doppioni sospetti");
		Set<AnagraficaGiocatoreRidottaRow> sospettiCloni = TorneiUtils.findClone(); 
		List<ScorePlayer> tabelliniSospetti = null;
		if (sospettiCloni != null){
			tabelliniSospetti = new ArrayList<ScorePlayer>();
			for (AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRow: sospettiCloni){
				AnagraficaGiocatoreRow anagraficaSonda = new AnagraficaGiocatoreRow(anagraficaGiocatoreRidottaRow.getId());
				ScorePlayer scorePlayerSonda = new ScorePlayer(anagraficaSonda);
				int index = tabellini.indexOf(scorePlayerSonda);
				if (index >= 0){
					ScorePlayer found = tabellini.get(index);
					tabelliniSospetti.add(found);
					MyLogger.getLogger().finest("Cercato: "+anagraficaGiocatoreRidottaRow+" Trovato: "+found);
				}
			}
			
			Collections.sort(tabelliniSospetti, new Comparator<ScorePlayer>() {
				@Override
				public int compare(ScorePlayer s1, ScorePlayer s2) {
					return s1.getAnagraficaGiocatore().getCognome().toLowerCase().compareTo(s2.getAnagraficaGiocatore().getCognome().toLowerCase());
				}
			});
				
		    Properties p = new Properties();
		    p.setProperty("input.encoding", "UTF-8");
		    p.setProperty("resource.loader.file.path", ResourceWorking.velocityTemplatePath());
		    Velocity.init( p );

			VelocityContext context = new VelocityContext();

			context.put( "scorePlayers", tabelliniSospetti );
			context.put( "styleGenerator", StyleGenerator.class);
			context.put( "Capitalize", Capitalize.class);
			context.put( "htmlPublisher", HtmlPublisher.class);

			Template template = null;

			try{
			  //template = Velocity.getTemplate("TabellinoGiocatore.vm", "UTF-8");
			  template = Velocity.getTemplate("PresuntiDoppioni.vm", "UTF-8");
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
				writer = new FileWriter(anagraficheDaVerificare);
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
	

	private static void uploadFiles(File ranking, File listaTornei, List<File> torneiHtml,  List<File> tabelliniPerClub) throws IOException{
		AlterVistaUtil.uploadInTornei(torneiHtml);
		AlterVistaUtil.uploadInTabelliniPerClub(tabelliniPerClub);
		AlterVistaUtil.uploadInListaTornei(Collections.singletonList(listaTornei));
		AlterVistaUtil.uploadInRankings(Collections.singletonList(ranking));
	}
	
	public static String getTorneoPage(String idTorneo){
		return idTorneo.replaceAll("\\s+", "").replaceAll("\\[", "_").replaceAll("\\]", "_").replaceAll("!", "").replaceAll("'", "").replaceAll("é", "e").replaceAll("è", "e");
	}
	
}
