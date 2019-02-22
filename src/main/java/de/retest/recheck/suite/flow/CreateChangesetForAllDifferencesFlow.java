package de.retest.recheck.suite.flow;

import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.ReplayResult;
import de.retest.recheck.report.SuiteReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.diff.ElementDifference;
import de.retest.recheck.ui.diff.IdentifyingAttributesDifference;
import de.retest.recheck.ui.diff.InsertedDeletedElementDifference;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.ReviewResult;
import de.retest.recheck.ui.review.SuiteChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;

public class CreateChangesetForAllDifferencesFlow {

	private final ReplayResult replayResult;
	private final ReviewResult reviewResult;

	private CreateChangesetForAllDifferencesFlow( final ReplayResult replayResult ) {
		this.replayResult = replayResult;
		reviewResult = new ReviewResult();
	}

	public static ReviewResult create( final ReplayResult replayResult ) {
		final CreateChangesetForAllDifferencesFlow flow = new CreateChangesetForAllDifferencesFlow( replayResult );
		flow.create();
		return flow.reviewResult;
	}

	private void create() {
		for ( final SuiteReplayResult suite : replayResult.getSuiteReplayResults() ) {
			final SuiteChangeSet suiteChangeSet =
					reviewResult.createSuiteChangeSet( suite.getSuiteName(), suite.getSuiteUuid() );
			for ( final TestReplayResult test : suite.getTestReplayResults() ) {
				final TestChangeSet testChangeSet = suiteChangeSet.createTestChangeSet();
				boolean first = true;
				for ( final ActionReplayResult actionReplayResult : test.getActionReplayResults() ) {
					final String description = actionReplayResult.getDescription();
					final String stateFilePath = actionReplayResult.getStateFilePath();
					if ( first ) {
						if ( testChangeSet.getInitialStateChangeSet().isEmpty() ) {
							addAllElementDifferences( actionReplayResult,
									testChangeSet.createInitialActionChangeSet( description, stateFilePath ) );
						} else {
							addAllElementDifferences( actionReplayResult, testChangeSet.getInitialStateChangeSet() );
						}
						first = false;
					} else {
						final ActionChangeSet actionChangeSet = testChangeSet.createActionChangeSet();
						if ( actionReplayResult.getStateDifference() != null ) {
							addAllElementDifferences( actionReplayResult, actionChangeSet );
						}
					}
				}
			}
		}
	}

	private void addAllElementDifferences( final ActionReplayResult actionReplayResult,
			final ActionChangeSet actionChangeSet ) {
		for ( final ElementDifference elementDifference : actionReplayResult.getAllElementDifferences() ) {
			if ( elementDifference.isInsertion() ) {
				actionChangeSet.addInsertChange(
						((InsertedDeletedElementDifference) elementDifference.getIdentifyingAttributesDifference())
								.getActual() );
			} else if ( elementDifference.isDeletion() ) {
				actionChangeSet.addDeletedChange(
						((InsertedDeletedElementDifference) elementDifference.getIdentifyingAttributesDifference())
								.getExpected().getIdentifyingAttributes() );
			} else {
				if ( elementDifference.hasIdentAttributesDifferences() ) {
					actionChangeSet.getIdentAttributeChanges().addAll( elementDifference.getIdentifyingAttributes(),
							((IdentifyingAttributesDifference) elementDifference.getIdentifyingAttributesDifference())
									.getAttributeDifferences() );
				}
				if ( elementDifference.hasAttributesDifferences() ) {
					actionChangeSet.getAttributesChanges().addAll( elementDifference.getIdentifyingAttributes(),
							elementDifference.getAttributesDifference().getDifferences() );
				}
			}
		}
	}
}