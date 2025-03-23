package it.desimone.gsheetsaccess.ranking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.googleaccess.GoogleSheetsAccess;
import it.desimone.gsheetsaccess.gsheets.dto.BlackListRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRowFactory.SheetRowType;
import it.desimone.gsheetsaccess.gsheets.facade.GSheetsInterface;
import it.desimone.utils.DateUtils;
import it.desimone.utils.MyLogger;

public class BlackListData {

	public static final String BLACKLIST_SHEET = "BLACKLIST";
	private List<BlackListPlayer> blackListPlayers;
	private static BlackListData instance;
	private BlackListData() {}
	
	public static BlackListData getInstance() {
		if (instance == null) {
			instance = new BlackListData();
			try {
				List<BlackListRow> blackList = GSheetsInterface.getAllRows(Configurator.getBlackListSheetId(), SheetRowType.BlackList);

				if (CollectionUtils.isNotEmpty(blackList)) {
					List<BlackListPlayer> blp = new ArrayList<BlackListPlayer>();
					for (BlackListRow row: blackList) {
						BlackListPlayer blackListPlayer = new BlackListPlayer();
						blackListPlayer.setIdAnagrafica(Integer.valueOf(row.getIdAnagrafica()));
						if (StringUtils.isNotEmpty(row.getAnniEsclusi())) {
							String[]years = row.getAnniEsclusi().split(",");
							blackListPlayer.setForbiddenYears(Arrays.asList(years));
						}
						if (StringUtils.isNotEmpty(row.getDataInizioEsclusione())) {
							blackListPlayer.setStartExclusion(DateUtils.parseItalianDate(row.getDataInizioEsclusione()));
						}
						if (StringUtils.isNotEmpty(row.getDataFineEsclusione())) {
							blackListPlayer.setEndExclusion(DateUtils.parseItalianDate(row.getDataFineEsclusione()));
						}
	
						blackListPlayer.setExclusionFromRankingMatter(row.getMotivazione());
						blp.add(blackListPlayer);
					}
					instance.setBlackListPlayers(blp);
				}
				MyLogger.getLogger().info("BLACKLIST: "+instance);
			} catch (IOException e) {
				MyLogger.getLogger().severe("Errore nell'acquisizione della blackList: "+e.getMessage());
			} 
		}
		return instance;
	}
	
	public static BlackListData getInstanceOld() {
		if (instance == null) {
			instance = new BlackListData();
			GoogleSheetsAccess googleSheetsAccess = new GoogleSheetsAccess();
			try {
				List<List<Object>> rows = googleSheetsAccess.leggiSheet(Configurator.getBlackListSheetId(), BLACKLIST_SHEET+"!A2:D");
				if (CollectionUtils.isNotEmpty(rows)) {
					List<BlackListPlayer> blp = new ArrayList<BlackListPlayer>();
					for (List<Object> row: rows) {
						Integer idAnagrafica 			=  Integer.parseInt((String)row.get(0));
						String anniEsclusi = null;
						if (row.size() >1) anniEsclusi 	= (String) row.get(1);
						String dataInizioEsclusione = null;
						if (row.size() >2) dataInizioEsclusione 	= (String) row.get(2);
						String dataFineEsclusione = null;
						if (row.size() >3) dataFineEsclusione 	= (String) row.get(3);
						BlackListPlayer blackListPlayer = new BlackListPlayer();
						blackListPlayer.setIdAnagrafica(idAnagrafica);
						if (StringUtils.isNotEmpty(anniEsclusi)) {
							String[]years = anniEsclusi.split(",");
							blackListPlayer.setForbiddenYears(Arrays.asList(years));
						}
						if (StringUtils.isNotEmpty(dataInizioEsclusione)) {
							blackListPlayer.setStartExclusion(DateUtils.parseItalianDate(dataInizioEsclusione));
						}
						if (StringUtils.isNotEmpty(dataFineEsclusione)) {
							blackListPlayer.setEndExclusion(DateUtils.parseItalianDate(dataFineEsclusione));
						}
						if (row.size() >4) {
							blackListPlayer.setExclusionFromRankingMatter((String) row.get(4));
						}
						blp.add(blackListPlayer);
					}
					instance.setBlackListPlayers(blp);
				}
				MyLogger.getLogger().info("BLACKLIST: "+instance);
			} catch (IOException e) {
				MyLogger.getLogger().severe("Errore nell'acquisizione della blacklist: "+e.getMessage());
			} catch (Exception e) {
				MyLogger.getLogger().severe("Errore nell'elaborazione della blacklist: "+e.getMessage());
			}
		}
		return instance;
	}
	
	public List<BlackListPlayer> getBlackListPlayers() {
		return blackListPlayers;
	}

	public void setBlackListPlayers(List<BlackListPlayer> blackListPlayers) {
		this.blackListPlayers = blackListPlayers;
	}

	public boolean isForbiddenPlayer(int idAnagrafica, String year) {
		boolean result = false;
		Optional<BlackListPlayer> blackListPlayerOpt = blackListPlayers.stream().filter(b -> b.getIdAnagrafica() == idAnagrafica).findFirst();
		if (blackListPlayerOpt.isPresent()) {
			result = blackListPlayerOpt.get().isForbiddenYear(year);
		}
		return result;
	}
	
	public boolean isDisqualifiedPlayer(int idAnagrafica, Date start, Date end) {
		boolean result = false;
		Optional<BlackListPlayer> blackListPlayerOpt = blackListPlayers.stream().filter(b -> b.getIdAnagrafica() == idAnagrafica).findFirst();
		if (blackListPlayerOpt.isPresent()) {
			result = blackListPlayerOpt.get().isExcludedPeriod(start, end);
		}
//		if (CollectionUtils.isNotEmpty(blackListPlayers)) {
//			for (BlackListPlayer blackListPlayer: blackListPlayers) {
//				if (idAnagrafica == blackListPlayer.getIdAnagrafica() && blackListPlayer.isExcludedPeriod(start, end)) {
//					result = true;
//					break;
//				}
//			}
//		}
		return result;
	}
	
	public String getMotivazioneSqualifica(int idAnagrafica) {
		
		String result = null;
		Optional<BlackListPlayer> blackListPlayerOpt = blackListPlayers.stream().filter(b -> b.getIdAnagrafica() == idAnagrafica).findFirst();
		if (blackListPlayerOpt.isPresent()) {
			result = blackListPlayerOpt.get().getExclusionFromRankingMatter();
		}

		return result;
	}

	@Override
	public String toString() {
		return "BlackListData [blackListPlayers=" + blackListPlayers + "]";
	}
	
}
