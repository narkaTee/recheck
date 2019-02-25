package de.retest.recheck.review.ignore;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import de.retest.recheck.ignore.ShouldIgnore;
import de.retest.recheck.review.ignore.io.RegexLoader;
import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class AttributeShouldIgnore implements ShouldIgnore {

	private final String attribute;

	public AttributeShouldIgnore( final String attribute ) {
		this.attribute = attribute;
	}

	@Override
	public boolean shouldIgnoreElement( final Element element ) {
		return false;
	}

	@Override
	public boolean shouldIgnoreAttributeDifference( final Element element,
			final AttributeDifference attributeDifference ) {
		return attributeDifference.getKey().equals( attribute );
	}

	public static class AttributeShouldIgnoreLoader extends RegexLoader<AttributeShouldIgnore> {

		private static final String MATCHER = "matcher: ";
		private static final String KEY = "key: ";
		private static final String FORMAT = MATCHER + KEY + "%s";
		private static final Pattern REGEX = Pattern.compile( MATCHER + KEY + "(.+)" );

		public AttributeShouldIgnoreLoader() {
			super( REGEX );
		}

		@Override
		protected AttributeShouldIgnore load( final MatchResult regex ) {
			final String attribute = regex.group( 1 );
			return new AttributeShouldIgnore( attribute );
		}

		@Override
		public String save( final AttributeShouldIgnore ignore ) {
			return String.format( FORMAT, ignore.attribute );
		}
	}
}
