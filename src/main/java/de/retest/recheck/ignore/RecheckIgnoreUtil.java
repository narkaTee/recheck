package de.retest.recheck.ignore;

import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.configuration.ProjectConfiguration;
import de.retest.recheck.configuration.ProjectConfigurationUtil;
import de.retest.recheck.review.GlobalIgnoreApplier;
import de.retest.recheck.review.counter.NopCounter;
import de.retest.recheck.review.workers.LoadShouldIgnoreWorker;

public class RecheckIgnoreUtil {

	private static final Logger logger = LoggerFactory.getLogger( RecheckIgnoreUtil.class );

	private RecheckIgnoreUtil() {}

	public static Optional<Path> getIgnoreFile() {
		return getRetestFile( ProjectConfiguration.RECHECK_IGNORE );
	}

	public static Optional<Path> getIgnoreRuleFile() {
		return getRetestFile( ProjectConfiguration.RECHECK_IGNORE_JSRULES );
	}

	private static Optional<Path> getRetestFile( final String filename ) {
		final Optional<Path> projectConfigurationFolder = ProjectConfigurationUtil.findProjectConfigurationFolder();
		return projectConfigurationFolder.map( p -> p.resolve( filename ) );
	}

	public static GlobalIgnoreApplier loadRecheckIgnore() {
		try {
			final LoadShouldIgnoreWorker loadShouldIgnoreWorker =
					new LoadShouldIgnoreWorker( NopCounter.getInstance() );
			return loadShouldIgnoreWorker.load();
		} catch ( final Exception e ) {
			logger.error( "Could not load recheck ignore file.", e );
			return GlobalIgnoreApplier.create( NopCounter.getInstance() );
		}
	}
}
