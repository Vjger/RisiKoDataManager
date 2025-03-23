package it.desimone.gsheetsaccess.ranking;

import it.desimone.gsheetsaccess.dto.ScorePlayer;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;

import java.util.List;
import java.util.Map;

//Definire l'oggetto: deve contenere i tabellini le soglie ed i contatori
public class RankingData {
	
	private List<ScorePlayer> tabellini;
	private Map<TipoTorneo, Integer> mappaSoglieTipoTorneo;
	private Map<TipoTorneo, Integer> mappaConteggiTipoTorneo;
	public List<ScorePlayer> getTabellini() {
		return tabellini;
	}
	public void setTabellini(List<ScorePlayer> tabellini) {
		this.tabellini = tabellini;
	}
	public Map<TipoTorneo, Integer> getMappaSoglieTipoTorneo() {
		return mappaSoglieTipoTorneo;
	}
	public void setMappaSoglieTipoTorneo(
			Map<TipoTorneo, Integer> mappaSoglieTipoTorneo) {
		this.mappaSoglieTipoTorneo = mappaSoglieTipoTorneo;
	}
	public Map<TipoTorneo, Integer> getMappaConteggiTipoTorneo() {
		return mappaConteggiTipoTorneo;
	}
	public void setMappaConteggiTipoTorneo(
			Map<TipoTorneo, Integer> mappaConteggiTipoTorneo) {
		this.mappaConteggiTipoTorneo = mappaConteggiTipoTorneo;
	}
	
	

}
