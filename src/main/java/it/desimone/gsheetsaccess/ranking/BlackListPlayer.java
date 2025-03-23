package it.desimone.gsheetsaccess.ranking;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

public class BlackListPlayer {

	private int idAnagrafica;
	private List<String> forbiddenYears;
	private Date startExclusion;
	private Date endExclusion;
	private String exclusionFromRankingMatter;
	public int getIdAnagrafica() {
		return idAnagrafica;
	}
	public void setIdAnagrafica(int idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}
	public List<String> getForbiddenYears() {
		return forbiddenYears;
	}
	public void setForbiddenYears(List<String> forbiddenYears) {
		this.forbiddenYears = forbiddenYears;
	}
	public Date getStartExclusion() {
		return startExclusion;
	}
	public void setStartExclusion(Date startExclusion) {
		this.startExclusion = startExclusion;
	}
	public Date getEndExclusion() {
		return endExclusion;
	}
	public void setEndExclusion(Date endExclusion) {
		this.endExclusion = endExclusion;
	}

	public String getExclusionFromRankingMatter() {
		return exclusionFromRankingMatter;
	}
	public void setExclusionFromRankingMatter(String exclusionFromRankingMatter) {
		this.exclusionFromRankingMatter = exclusionFromRankingMatter;
	}
	public boolean isForbiddenYear(String year){
		return (CollectionUtils.isNotEmpty(forbiddenYears) && forbiddenYears.contains(year)) || (CollectionUtils.isEmpty(forbiddenYears) && startExclusion == null);
	}

	public boolean isExcludedPeriod(Date start, Date end){
		if (startExclusion == null || start == null) return false;
		return (!start.before(startExclusion) && (end == null || endExclusion == null || !endExclusion.before(end)))
				|| (start.before(startExclusion) && (end == null || !end.before(startExclusion)));
	}
	@Override
	public String toString() {
		return "BlackListPlayer [idAnagrafica=" + idAnagrafica + ", forbiddenYears=" + forbiddenYears
				+ ", startExclusion=" + startExclusion + ", endExclusion=" + endExclusion
				+ ", exclusionFromRankingMatter=" + exclusionFromRankingMatter + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(idAnagrafica);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlackListPlayer other = (BlackListPlayer) obj;
		return idAnagrafica == other.idAnagrafica;
	}

	
	
}
