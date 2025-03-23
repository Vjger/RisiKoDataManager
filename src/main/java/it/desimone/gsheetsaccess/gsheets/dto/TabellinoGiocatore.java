package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TabellinoGiocatore {

	private AnagraficaGiocatoreRidottaRow anagraficaRidottaGiocatoreRowFrom;
	
	private List<PartitaRow> partiteGiocate;
	
	private Set<TorneiRow> torneiGiocati;
	
	public TabellinoGiocatore(AnagraficaGiocatoreRidottaRow anagraficaRidottaGiocatoreRowFrom, List<SheetRow> partiteGiocate, Set<TorneiRow> torneiGiocati) {
		super();
		this.anagraficaRidottaGiocatoreRowFrom = anagraficaRidottaGiocatoreRowFrom;
		if (partiteGiocate != null && !partiteGiocate.isEmpty()){
			this.partiteGiocate = new ArrayList<PartitaRow>();
			for (SheetRow row: partiteGiocate){
				this.partiteGiocate.add((PartitaRow)row);
			}
		}
		this.torneiGiocati = torneiGiocati;
	}

	public AnagraficaGiocatoreRidottaRow getAnagraficaRidottaGiocatoreRowFrom() {
		return anagraficaRidottaGiocatoreRowFrom;
	}

	public void setAnagraficaRidottaGiocatoreRowFrom(
			AnagraficaGiocatoreRidottaRow anagraficaRidottaGiocatoreRowFrom) {
		this.anagraficaRidottaGiocatoreRowFrom = anagraficaRidottaGiocatoreRowFrom;
	}

	public List<PartitaRow> getPartiteGiocate() {
		return partiteGiocate;
	}

	public void setPartiteGiocate(List<PartitaRow> partiteGiocate) {
		this.partiteGiocate = partiteGiocate;
	}

	public Set<TorneiRow> getTorneiGiocati() {
		return torneiGiocati;
	}

	public void setTorneiGiocati(Set<TorneiRow> torneiGiocati) {
		this.torneiGiocati = torneiGiocati;
	}
	
	
}
