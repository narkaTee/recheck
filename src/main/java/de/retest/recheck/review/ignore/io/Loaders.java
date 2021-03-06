package de.retest.recheck.review.ignore.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import de.retest.recheck.review.ignore.AttributeShouldIgnore;
import de.retest.recheck.review.ignore.AttributeShouldIgnore.AttributeShouldIgnoreLoader;
import de.retest.recheck.ignore.JSShouldIgnoreImpl;
import de.retest.recheck.review.ignore.ElementAttributeShouldIgnore;
import de.retest.recheck.review.ignore.ElementAttributeShouldIgnore.ElementAttributeShouldIgnoreLoader;
import de.retest.recheck.review.ignore.ElementShouldIgnore;
import de.retest.recheck.review.ignore.ElementShouldIgnore.ElementShouldIgnoreLoader;
import de.retest.recheck.review.ignore.IgnoreCommentLoader;
import de.retest.recheck.review.ignore.IgnoreCommentLoader.ShouldIgnoreComment;
import de.retest.recheck.review.ignore.JSShouldIgnoreLoader;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher.ElementIdMatcherLoader;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher;
import de.retest.recheck.review.ignore.matcher.ElementRetestIdMatcher.ElementRetestIdMatcherLoader;
import de.retest.recheck.review.ignore.matcher.ElementXPathMatcher;
import de.retest.recheck.review.ignore.matcher.ElementXPathMatcher.ElementXpathMatcherLoader;

public class Loaders {

	private static final List<Pair<Class<?>, Loader<?>>> registeredLoaders = registerLoaders();

	private Loaders() {}

	private static List<Pair<Class<?>, Loader<?>>> registerLoaders() {
		final List<Pair<Class<?>, Loader<?>>> pairs = new ArrayList<>();
		pairs.add( Pair.of( ElementIdMatcher.class, new ElementIdMatcherLoader() ) );
		pairs.add( Pair.of( ElementRetestIdMatcher.class, new ElementRetestIdMatcherLoader() ) );
		pairs.add( Pair.of( ElementXPathMatcher.class, new ElementXpathMatcherLoader() ) );
		pairs.add( Pair.of( ElementAttributeShouldIgnore.class, new ElementAttributeShouldIgnoreLoader() ) );
		pairs.add( Pair.of( AttributeShouldIgnore.class, new AttributeShouldIgnoreLoader() ) );
		pairs.add( Pair.of( ElementShouldIgnore.class, new ElementShouldIgnoreLoader() ) );
		pairs.add( Pair.of( ShouldIgnoreComment.class, new IgnoreCommentLoader() ) );
		pairs.add( Pair.of( JSShouldIgnoreImpl.class, new JSShouldIgnoreLoader() ) );
		return pairs;
	}

	public static <T> Loader<T> get( final Class<? extends T> clazz ) {
		return (Loader<T>) registeredLoaders.stream() //
				.filter( pair -> clazz.isAssignableFrom( pair.getLeft() ) ) //
				.findFirst() //
				.map( Pair::getRight ) //
				.orElseThrow( () -> new UnsupportedOperationException( "No loader registered for " + clazz ) );
	}

	public static <T> Loader<T> get( final String line ) {
		return (Loader<T>) registeredLoaders.stream() //
				.filter( pair -> pair.getRight().canLoad( line ) ) //
				.findFirst() //
				.map( Pair::getRight ) //
				.orElseThrow( () -> new UnsupportedOperationException( "No loader registered for " + line ) );
	}

	public static Stream<String> save( final Stream<?> objects ) {
		return objects.map( Loaders::save );
	}

	private static <T> String save( final T object ) {
		final Loader<T> loader = (Loader<T>) get( object.getClass() );
		return loader.save( object );
	}

	public static Stream<?> load( final Stream<String> lines ) {
		return lines.map( String::trim ) //
				.filter( StringUtils::isNotEmpty ) //
				.map( Loaders::load );
	}

	private static <T> T load( final String line ) {
		return (T) registeredLoaders.stream() //
				.map( Pair::getRight ) //
				.filter( loader -> loader.canLoad( line ) ) //
				.findFirst() //
				.map( loader -> loader.load( line ) ) //
				.orElseThrow( () -> new IllegalArgumentException( "Line '" + line + "' has no loader." ) );
	}
}
