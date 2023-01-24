package com.amica.help;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amica.HasKeys;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.amica.help.Ticket.Priority;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link HelpDesk} class.
 * 
 * @author Will Provost
 */
public class HelpDeskTest {

	public static final String TECH1 = "TECH1";
	public static final String TECH2 = "TECH2";
	public static final String TECH3 = "TECH3";

	public static final int TICKET1_ID = 1;
	public static final String TICKET1_ORIGINATOR = "TICKET1_ORIGINATOR";
	public static final String TICKET1_DESCRIPTION = "TICKET1_DESCRIPTION";
	public static final Priority TICKET1_PRIORITY = Priority.LOW;
	public static final int TICKET2_ID = 2;
	public static final String TICKET2_ORIGINATOR = "TICKET2_ORIGINATOR";
	public static final String TICKET2_DESCRIPTION = "TICKET2_DESCRIPTION";
	public static final Priority TICKET2_PRIORITY = Priority.HIGH;
	
	public static final String TAG1 = "TAG1";
	public static final String TAG2 = "TAG2";
	public static final String TAG3 = "TAG3";
	
	private HelpDesk helpDesk = new HelpDesk();
	private Technician tech1;
	private Technician tech2;
	private Technician tech3;

	@BeforeEach
	public void setUp() {
		helpDesk.addTechnician(TECH1, TECH1, 1);
		helpDesk.addTechnician(TECH2, TECH2, 2);
		helpDesk.addTechnician(TECH3, TECH3, 3);

		Iterator<Technician> iterator = helpDesk.getTechnicians().iterator();
		tech1 = iterator.next();
		tech2 = iterator.next();
		tech3 = iterator.next();

		Clock.setTime(100);
	}

	/**
	 * Custom matcher that checks the contents of a stream of tickets
	 * against expected IDs, in exact order;
	 */
	public static class HasIDs extends TypeSafeMatcher<Stream<? extends Ticket>> {

		private String expected;
		private String was;
		
		public HasIDs(int... IDs) {
			int[] expectedIDs = IDs;
			expected = Arrays.stream(expectedIDs)
					.mapToObj(Integer::toString)
					.collect(Collectors.joining(", ", "[ ", " ]"));		
		}
		
		public void describeTo(Description description) {
			
			description.appendText("tickets with IDs ");
			description.appendText(expected);
		}
		
		@Override
		public void describeMismatchSafely
				(Stream<? extends Ticket> tickets, Description description) {
			description.appendText("was: tickets with IDs ");
			description.appendText(was);
		}

		protected boolean matchesSafely(Stream<? extends Ticket> tickets) {
			was = tickets.mapToInt(Ticket::getID)
					.mapToObj(Integer::toString)
					.collect(Collectors.joining(", ", "[ ", " ]"));
			return expected.equals(was);
		}
		
	}

	public void createTicket1() {
		helpDesk.createTicket(TICKET1_ORIGINATOR, TICKET1_DESCRIPTION, TICKET1_PRIORITY);
	}

	public void createTicket2() {
		helpDesk.createTicket(TICKET2_ORIGINATOR, TICKET2_DESCRIPTION, TICKET2_PRIORITY);
	}

	@Test
	public void testGetTicketsBeforeAnyTicketsExist() {
		helpDesk = new HelpDesk();
		assertThat(helpDesk.getTicketByID(TICKET1_ID), is(nullValue()));
	}

	@Test
	public void testCreateTicketAndCheckDescriptions() {
		createTicket1();
		createTicket2();
		assertThat(helpDesk.getTicketByID(TICKET2_ID), hasProperty("description", is(TICKET2_DESCRIPTION)));
	}

	@Test
	public void testThatTechniciansNeedToBeCreated() {
		HelpDesk helpDeskForTest = new HelpDesk();
		assertThrows(IllegalStateException.class, () -> helpDeskForTest.createTicket(TICKET1_ORIGINATOR, TICKET1_DESCRIPTION, TICKET1_PRIORITY));
	}

	@Test
	public void testThatTechnicianIsAssigned() {
		createTicket1();
		assertEquals(tech1, helpDesk.getTicketByID(TICKET1_ID).getTechnician());
		assertEquals(1, tech1.getActiveTickets().count());
	}

	@Test
	public void testThatTechnicianAssignmentRotates() {
		createTicket1();
		tech2.assignTicket(helpDesk.getTicketByID(TICKET1_ID));
		// this is a bit vulnerable – what if the technician checked the ticket and maybe threw an exception?

		createTicket2();
		assertEquals(tech3, helpDesk.getTicketByID(TICKET2_ID).getTechnician());
	}

	@Test
	public void testGetTicketsByStatus() {
		createTicket1();
		createTicket2();
		helpDesk.getTicketByID(TICKET2_ID).resolve("Resolved");
		assertEquals(1, helpDesk.getTicketsByStatus(Ticket.Status.ASSIGNED).count());
		assertEquals(1, helpDesk.getTicketsByStatus(Ticket.Status.RESOLVED).count());
	}

	@Test
	public void testGetTicketByNotStatus() {
		createTicket1();
		createTicket2();
		assertThat(helpDesk.getTicketsByNotStatus(Ticket.Status.WAITING), hasIDs(2,1));
	}

	@Test
	public void testFindByTag() {
		createTicket1();
		createTicket2();
		helpDesk.getTicketByID(TICKET1_ID).addTag(new Tag(TAG1));
		helpDesk.getTicketByID(TICKET2_ID).addTag(new Tag(TAG2));

		assertEquals(2, helpDesk.getTicketsWithAnyTag(TAG1, TAG2).count());
		assertEquals(1, helpDesk.getTicketsWithAnyTag(TAG1).count());
	}

	@Test
	public void testFindByTechnician() {
		createTicket1();
		assertEquals(1, helpDesk.getTicketsByTechnician(TECH1).count());
	}

	@Test
	public void testFindByText() {
		createTicket1();
		assertEquals(1, helpDesk.getTicketsByText(TICKET1_DESCRIPTION).count());
	}

	// TODO if time permits, go back to add more tests for other behaviors

	public static Matcher<Stream<? extends Ticket>> hasIDs(int... IDs) {
		return new HasIDs(IDs);
	}
// Step5 uses a generic stream matcher:
//	public static Matcher<Stream<? extends Ticket>> hasIDs(Integer... IDs) {
//		return HasKeys.hasKeys(Ticket::getID, IDs);
//	}
}

