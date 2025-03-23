package it.desimone.gsheetsaccess.ranking;

import java.util.HashMap;
import java.util.Map;

import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;

public class ConteggiTornei {
	
	private Map<TipoTorneo, DatiTornei> mappaDatiTornei = new HashMap<TipoTorneo, DatiTornei>();
	
	class DatiTornei{
		private Integer numeroTorneiDisputati = 0;
		private Integer numeroTurniDisputati = 0;
		
		public Integer getNumeroTorneiDisputati() {
			return numeroTorneiDisputati;
		}
		public void setNumeroTorneiDisputati(Integer numeroTorneiDisputati) {
			this.numeroTorneiDisputati = numeroTorneiDisputati;
		}
		public Integer getNumeroTurniDisputati() {
			return numeroTurniDisputati;
		}
		public void setNumeroTurniDisputati(Integer numeroTurniDisputati) {
			this.numeroTurniDisputati = numeroTurniDisputati;
		}
		public void addOneToTorneiDisputati(){
			numeroTorneiDisputati++;
		}
		public void addToTurniDisputati(Integer numeroTurni){
			numeroTurniDisputati+= numeroTurni;
		}
		@Override
		public String toString() {
			return "DatiTornei [numeroTorneiDisputati=" + numeroTorneiDisputati
					+ ", numeroTurniDisputati=" + numeroTurniDisputati + "]";
		}
		
		
	}
	
	public void addOneToTorneiDisputati(TipoTorneo tipoTorneo){
		DatiTornei datiTornei = mappaDatiTornei.get(tipoTorneo);
		if (datiTornei == null) datiTornei = new DatiTornei();
		
		datiTornei.addOneToTorneiDisputati();
		mappaDatiTornei.put(tipoTorneo, datiTornei);
	}
	public void addToTurniDisputati(TipoTorneo tipoTorneo, Integer numeroTurni){
		DatiTornei datiTornei = mappaDatiTornei.get(tipoTorneo);
		if (datiTornei == null) datiTornei = new DatiTornei();
		datiTornei.addToTurniDisputati(numeroTurni);
		mappaDatiTornei.put(tipoTorneo, datiTornei);
	}
	public Integer getNumeroTorneiDisputati(TipoTorneo tipoTorneo) {
		Integer result = 0;
		DatiTornei datiTornei = mappaDatiTornei.get(tipoTorneo);
		if (datiTornei != null){
			result = datiTornei.getNumeroTorneiDisputati();
		}		
		return result;
	}
	public Integer getNumeroTurniDisputati(TipoTorneo tipoTorneo) {
		Integer result = 0;
		DatiTornei datiTornei = mappaDatiTornei.get(tipoTorneo);
		if (datiTornei != null){
			result = datiTornei.getNumeroTurniDisputati();
		}		
		return result;
	}
	@Override
	public String toString() {
		return "ConteggiTornei [mappaDatiTornei=" + mappaDatiTornei + "]";
	}

	
	

}
