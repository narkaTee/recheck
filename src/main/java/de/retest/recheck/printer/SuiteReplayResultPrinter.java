package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.report.SuiteReplayResult;

public class SuiteReplayResultPrinter implements Printer<SuiteReplayResult> {

	private final TestReplayResultPrinter delegate;

	public SuiteReplayResultPrinter( final DefaultValueFinderProvider provider, final ShouldIgnore ignore ) {
		delegate = new TestReplayResultPrinter( provider, ignore );
	}

	@Override
	public String toString( final SuiteReplayResult difference, final String indent ) {
		return difference.getTestReplayResults().stream() //
				.map( d -> delegate.toString( d, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}
}
