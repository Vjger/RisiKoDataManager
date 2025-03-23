package it.desimone.gsheetsaccess.dto;

import it.desimone.gsheetsaccess.gsheets.dto.ClassificheRow;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TorneoPubblicato {

	private TorneiRow torneoRow;
	private List<PartitaRow> partite = new ArrayList<PartitaRow>();
	private List<ClassificheRow> classifica = new ArrayList<ClassificheRow>();

	public TorneoPubblicato(TorneiRow torneoRow) {
		super();
		this.torneoRow = torneoRow;
	}

	public void add(PartitaRow partitaRow){
		partite.add(partitaRow);
	}
	
	public void add(ClassificheRow classificheRow){
		classifica.add(classificheRow);
	}
	
	public String getIdTorneo(){
		return torneoRow.getIdTorneo();
	}
	
	public Set<Integer> getIdPartecipanti(){
		Set<Integer> partecipanti = new HashSet<Integer>();
		if (partite != null){
			for (PartitaRow partita: partite){
				if (partita.getIdGiocatore1() != null) partecipanti.add(partita.getIdGiocatore1());
				if (partita.getIdGiocatore2() != null) partecipanti.add(partita.getIdGiocatore2());
				if (partita.getIdGiocatore3() != null) partecipanti.add(partita.getIdGiocatore3());
				if (partita.getIdGiocatore4() != null) partecipanti.add(partita.getIdGiocatore4());
				if (partita.getIdGiocatore5() != null) partecipanti.add(partita.getIdGiocatore5());
			}
		}
		return partecipanti;
	}

	public TorneiRow getTorneoRow() {
		return torneoRow;
	}

	public void setTorneoRow(TorneiRow torneoRow) {
		this.torneoRow = torneoRow;
	}

	public List<PartitaRow> getPartite() {
		return partite;
	}

	public void setPartite(List<PartitaRow> partite) {
		this.partite = partite;
	}

	public List<ClassificheRow> getClassifica() {
		return classifica;
	}

	public void setClassifica(List<ClassificheRow> classifica) {
		this.classifica = classifica;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((torneoRow == null) ? 0 : torneoRow.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TorneoPubblicato other = (TorneoPubblicato) obj;
		if (torneoRow == null) {
			if (other.torneoRow != null)
				return false;
		} else if (!torneoRow.equals(other.torneoRow))
			return false;
		return true;
	}

	public boolean isConcluso() {
		//return classifica != null && !classifica.isEmpty();
		boolean concluso = false;
		
		int numeroTurni = torneoRow.getNumeroTurni();
		
		if (partite != null){
			for (PartitaRow partita: partite){
				if (partita != null && partita.getNumeroTurno() == numeroTurni){
					concluso = true;
					break;
				}
			}
		}
		
		return concluso;
	}

	@Override
	public String toString() {
		return "TorneoPubblicato [" + torneoRow.getIdTorneo() + "]";
	}
	
	
}
