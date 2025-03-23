package it.desimone;

import it.desimone.gsheetsaccess.ranking.BlackListPlayer;
import it.desimone.utils.DateUtils;
import junit.framework.TestCase;

public class BlackListPlayerTest extends TestCase {

	private BlackListPlayer pl1, pl2, pl3;
	
	protected void setUp() throws Exception {
		super.setUp();
		pl1 = new BlackListPlayer();
		pl1.setIdAnagrafica(53);
		pl1.setStartExclusion(DateUtils.parseItalianDate("01/01/2024"));
		pl1.setEndExclusion(DateUtils.parseItalianDate("07/01/2024"));
		pl2 = new BlackListPlayer();
		pl2.setIdAnagrafica(53);
		pl2.setStartExclusion(DateUtils.parseItalianDate("15/01/2024"));
		//pl2.setEndExclusion(DateUtils.parseItalianDate("16/01/2024"));
		pl3 = new BlackListPlayer();
		pl3.setIdAnagrafica(53);
		pl3.setStartExclusion(DateUtils.parseItalianDate("19/01/2024"));
		//pl3.setEndExclusion(DateUtils.parseItalianDate("07/01/2024"));
	}
	
	public void testExclusion1() {
		assertTrue(pl1.isExcludedPeriod(DateUtils.parseItalianDate("17/10/2023"), DateUtils.parseItalianDate("09/01/2024")));
	}
	
	public void testExclusion2() {
		assertTrue(pl1.isExcludedPeriod(DateUtils.parseItalianDate("09/11/2023"), DateUtils.parseItalianDate("18/01/2024")));
	}
	
	public void testExclusion3() {
		assertTrue(pl1.isExcludedPeriod(DateUtils.parseItalianDate("07/01/2024"), DateUtils.parseItalianDate("07/01/2024")));
	}
	
	public void testExclusion4() {
		assertFalse(pl1.isExcludedPeriod(DateUtils.parseItalianDate("14/01/2024"), DateUtils.parseItalianDate("14/01/2024")));
	}
	public void testExclusion5() {
		assertFalse(pl2.isExcludedPeriod(DateUtils.parseItalianDate("17/10/2023"), DateUtils.parseItalianDate("09/01/2024")));
	}
	
	public void testExclusion6() {
		assertTrue(pl2.isExcludedPeriod(DateUtils.parseItalianDate("09/11/2023"), DateUtils.parseItalianDate("18/01/2024")));
	}
	
	public void testExclusion7() {
		assertFalse(pl2.isExcludedPeriod(DateUtils.parseItalianDate("07/01/2024"), DateUtils.parseItalianDate("07/01/2024")));
	}
	
	public void testExclusion8() {
		assertFalse(pl2.isExcludedPeriod(DateUtils.parseItalianDate("14/01/2024"), DateUtils.parseItalianDate("14/01/2024")));
	}
	public void testExclusion9() {
		assertFalse(pl3.isExcludedPeriod(DateUtils.parseItalianDate("17/10/2023"), DateUtils.parseItalianDate("09/01/2024")));
	}
	
	public void testExclusion10() {
		assertFalse(pl3.isExcludedPeriod(DateUtils.parseItalianDate("09/11/2023"), DateUtils.parseItalianDate("18/01/2024")));
	}
	
	public void testExclusion11() {
		assertFalse(pl3.isExcludedPeriod(DateUtils.parseItalianDate("07/01/2024"), DateUtils.parseItalianDate("07/01/2024")));
	}
	
	public void testExclusion12() {
		assertFalse(pl3.isExcludedPeriod(DateUtils.parseItalianDate("14/01/2024"), DateUtils.parseItalianDate("14/01/2024")));
	}
}
