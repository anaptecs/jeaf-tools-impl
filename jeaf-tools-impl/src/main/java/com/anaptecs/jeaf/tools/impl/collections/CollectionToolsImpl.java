/*
 * anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 * 
 * Copyright 2004 - 2013 All rights reserved.
 */
package com.anaptecs.jeaf.tools.impl.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.anaptecs.jeaf.tools.annotations.ToolsImplementation;
import com.anaptecs.jeaf.tools.api.collections.CollectionTools;
import com.anaptecs.jeaf.xfun.api.checks.Check;

/**
 * This class implements the collection tools interface for all full featured java environments such as JSE and JEE.
 * 
 * @author JEAF Development Team
 * 
 */
@ToolsImplementation(toolsInterface = CollectionTools.class)
public class CollectionToolsImpl implements CollectionTools {
  /**
   * Method creates a unmodifiable clone of the passed collection.
   * 
   * @param <T>
   * @param pCollection List of which an unmodifiable clone should be created. The parameter must not be null.
   * @return {@link Collection} Unmodifiable clone of the passed collection. The method never returns null.
   */
  public <T> Collection<T> unmodifiableClone( Collection<? extends T> pCollection ) {
    // Check parameter
    Check.checkInvalidParameterNull(pCollection, "pCollection");

    return Collections.unmodifiableCollection(new ArrayList<T>(pCollection));
  }

  /**
   * Method creates a unmodifiable clone of the passed list.
   * 
   * @param <T>
   * @param pList List of which an unmodifiable clone should be created. The parameter must not be null.
   * @return {@link List} Unmodifiable clone of the passed list. The method never returns null.
   */
  public <T> List<T> unmodifiableClone( List<? extends T> pList ) {
    // Check parameter
    Check.checkInvalidParameterNull(pList, "pList");

    return Collections.unmodifiableList(new ArrayList<T>(pList));
  }

  /**
   * Method creates a unmodifiable clone of the passed map.
   * 
   * @param <K>
   * @param <V>
   * @param pMap Map of which an unmodifiable clone should be created. The parameter must not be null.
   * @return {@link Map} Unmodifiable clone of the passed Map. The method never returns null.
   */
  public <K, V> Map<K, V> unmodifiableClone( Map<? extends K, ? extends V> pMap ) {
    // Check parameter
    Check.checkInvalidParameterNull(pMap, "pMap");

    return Collections.unmodifiableMap(new HashMap<K, V>(pMap));
  }

  /**
   * Method creates a unmodifiable clone of the passed set.
   * 
   * @param <T>
   * @param pSet Set of which an unmodifiable clone should be created. The parameter must not be null.
   * @return {@link Set} Unmodifiable clone of the passed set. The method never returns null.
   */
  public <T> Set<T> unmodifiableClone( Set<? extends T> pSet ) {
    // Check parameter
    Check.checkInvalidParameterNull(pSet, "pSet");

    return Collections.unmodifiableSet(new HashSet<T>(pSet));
  }

  /**
   * Method creates a unmodifiable clone of the passed sorted set.
   * 
   * @param <T>
   * @param pSortedSet Sorted set of which an unmodifiable clone should be created. The parameter must not be null.
   * @return {@link SortedSet} Unmodifiable clone of the passed sorted set. The method never returns null.
   */
  public <T> SortedSet<T> unmodifiableClone( SortedSet<? extends T> pSortedSet ) {
    // Check parameter
    Check.checkInvalidParameterNull(pSortedSet, "pSortedSet");

    return Collections.unmodifiableSortedSet(new TreeSet<T>(pSortedSet));
  }

  /**
   * Methods filters the passed collection for all objects that have the passed type.
   * 
   * @param <T>
   * @param pCollection Collection that should be filtered for all objects of the passed type. The parameter must not be
   * null.
   * @param pClass Type for which the passed collection should be filtered. The parameter must not be null.
   * @return {@link List} List with all objects that are instance of the passed class. The method never returns null.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T, S extends T> List<S> filter( Collection<? extends T> pCollection, Class<S> pClass ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pCollection, "pCollection");
    Check.checkInvalidParameterNull(pClass, "pClass");

    // Check for all objects of the passed collection if they have the passed type.
    List<S> lFilteredList = new LinkedList<>();
    for (T lNextObject : pCollection) {
      if (pClass.isInstance(lNextObject) == true) {
        lFilteredList.add((S) lNextObject);
      }
    }
    // Return list with all filtered objects.
    return lFilteredList;
  }

  /**
   * Methods filters the passed map for entries that have the passed type.
   * 
   * @param <T>
   * @param pMap Map that should be filtered for all objects of the passed type. The parameter must not be null.
   * @param pClass Type for which the passed map should be filtered. The parameter must not be null.
   * @return {@link List} List with all objects that are instance of the passed class. The method never returns null.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T, S extends T> List<S> filter( Map<?, ? extends T> pMap, Class<S> pClass ) {
    // Check parameters.
    Check.checkInvalidParameterNull(pMap, "pMap");
    Check.checkInvalidParameterNull(pClass, "pClass");

    List<S> lFilteredList = new LinkedList<>();
    for (Entry<?, ? extends T> lEntry : pMap.entrySet()) {
      T lValue = lEntry.getValue();
      if (pClass.isInstance(lValue) == true) {
        lFilteredList.add((S) lValue);
      }
    }
    return lFilteredList;
  }

  /**
   * Method converts the passed collection into a string using {@link Object#toString()} for each object plus the passed
   * separator.
   * 
   * @param pCollection Collection that should be converted into a string. The parameter must not be null.
   * @param pSeparator Separator string that should be used. The parameter must not be null.
   * @return {@link String} String that was created from the collection. The method never returns null.
   */
  @Override
  public String toString( Collection<?> pCollection, String pSeparator ) {
    // Check parameters
    Check.checkInvalidParameterNull(pCollection, "pCollection");
    Check.checkInvalidParameterNull(pSeparator, "pSeparator");

    StringBuilder lBuilder = new StringBuilder();
    Iterator<?> lIterator = pCollection.iterator();
    while (lIterator.hasNext()) {
      Object lNextObject = lIterator.next();
      if (lNextObject != null) {
        lBuilder.append(lNextObject.toString());
        if (lIterator.hasNext() == true) {
          lBuilder.append(pSeparator);
        }
      }
    }

    // Return created string.
    return lBuilder.toString();
  }

  /**
   * Method add all the elements of the passed array to the passed collection.
   * 
   * @param pCollection Collection to which the elements should be added. The parameter must not be null.
   * @param pElements Array with all elements that should be added. The parameter may be null.
   */
  @Override
  public <T> boolean addAll( Collection<T> pCollection, @SuppressWarnings("unchecked") T... pElements ) {
    // Check parameter
    Check.checkInvalidParameterNull(pCollection, "pCollection");

    boolean lElementsAdded;
    if (pElements != null && pElements.length > 0) {
      Collections.addAll(pCollection, pElements);
      lElementsAdded = true;
    }
    else {
      lElementsAdded = false;
    }
    return lElementsAdded;
  }
}
