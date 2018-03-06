/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0 

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.auphi.ktrl.schedule.dependency;
import java.util.ArrayList;
import java.util.Collections;
 
public class TSortUtils {
	private static <T> void swap(ArrayList<T> data, int i, int j) {
		T t = data.get(i);
		data.set(i, data.get(j));
		data.set(j, t);
	}
 
	public static <T extends Comparable<? super T>> boolean nextPerm(ArrayList<T> data) {
		// find the swaps
		int c = -1, d = data.size();
		for (int i = d - 2; i >= 0; i--)
			if (data.get(i).compareTo(data.get(i + 1)) < 0) {
				c = i;
				break;
			}
 
		if (c < 0)
			return false;
 
		int s = c + 1;
		for (int j = c + 2; j < d; j++)
			if (data.get(j).compareTo(data.get(s)) < 0 && //
					data.get(j).compareTo(data.get(c)) > 0)
				s = j;
 
		// do the swaps
		swap(data, c, s);
		while (--d > ++c)
			swap(data, c, d);
 
		return true;
	}
 
	public static <T extends Comparable<? super T>> ArrayList<ArrayList<T>> Permutations(ArrayList<T> d) {
		ArrayList<ArrayList<T>> result = new ArrayList<ArrayList<T>>();
		Collections.sort(d);
		do {
			result.add(new ArrayList<T>(d));
		} while (nextPerm(d));
		return result;
	}
 
	/** <p>
	 * <b>Topological sort</b> solves a problem of - finding a linear ordering
	 * of the vertices of <i>V</i> such that for each edge <i>(i, j) ∈ E</i>,
	 * vertex <i>i</i> is to the left of vertex <i>j</i>. (Skiena 2008, p. 481)
	 * </p>
	 * 
	 * <p>
	 * Method is derived from of <a
	 * href="http://en.wikipedia.org/wiki/Topological_sort#Algorithms" > Kahn's
	 * pseudo code</a> and traverses over vertices as they are returned by input
	 * map. Leaf nodes can have null or empty values. This method assumes, that
	 * input is valid DAG, so if cyclic dependency is detected, error is thrown.
	 * tSortFix is a fix to remove self dependencies and add missing leaf nodes.
	 * </p>
	 * 
	 * <pre>
	 * // For input with elements:
	 * { F1=[F2, F3, F4], F10=[F7, F4], F11=[F4], F2=[F3, F8, F4], F3=[F6], 
	 *   F4=null, F5=[F6, F4], F6=[F7, F8, F4], F7=[F4], F8=[F4], F9=[F4]}
	 *   
	 * // Output based on input map type: 
	 * HashMap: [F4, F11, F8, F9, F7, F10, F6, F5, F3, F2, F1]
	 * TreeMap: [F4, F11, F7, F8, F9, F10, F6, F3, F5, F2, F1]
	 * </pre>
	 * 
	 * @param g
	 *            <a href="http://en.wikipedia.org/wiki/Directed_acyclic_graph"
	 *            > Directed Acyclic Graph</a>, where vertices are stored as
	 *            {@link java.util.HashMap HashMap} elements.
	 * 
	 * @return Linear ordering of input nodes.
	 * @throws Exception
	 *             Thrown when cyclic dependency is detected, error message also
	 *             contains elements in cycle.
	 * 
	 */
	public static <T> ArrayList<T> tSort(java.util.Map<T, ArrayList<T>> g)
	        throws Exception
	/**
	 * @param L
	 *            Answer.
	 * @param S
	 *            Not visited leaf vertices.
	 * @param V
	 *            Visited vertices.
	 * @param P
	 *            Defined vertices.
	 * @param n
	 *            Current element.
	 */
	{
	    java.util.ArrayList<T> L = new ArrayList<T>(g.size());
	    java.util.Queue<T> S = new java.util.concurrent.LinkedBlockingDeque<T>();
	    java.util.HashSet<T> V = new java.util.HashSet<T>(), 
	    P = new java.util.HashSet<T>();
	    P.addAll(g.keySet());
	    T n;
 
	    // Find leaf nodes.
	    for (T t : P)
	        if (g.get(t) == null || g.get(t).isEmpty())
	            S.add(t);
 
	    // Visit all leaf nodes. Build result from vertices, that are visited
	    // for the first time. Add vertices to not visited leaf vertices S, if
	    // it contains current element n an all of it's values are visited.
	    while (!S.isEmpty()) {
	        if (V.add(n = S.poll()))
	            L.add(n);
	        for (T t : g.keySet())
	            if (g.get(t) != null && !g.get(t).isEmpty() && !V.contains(t)
	                    && V.containsAll(g.get(t)))
	                S.add(t);
	    }
 
	    // Return result.
	    if (L.containsAll(P))
	        return L;
 
	    // Throw exception.
	    StringBuilder sb = new StringBuilder();
//	            "\nInvalid DAG: a cyclic dependency detected :\n");
	    for (T t : P)
	        if (!L.contains(t))
	            sb.append(t).append(" ");
	    throw new Exception(sb.append("\n").toString());
	}
 
	/**
	 * Method removes self dependencies and adds missing leaf nodes.
	 * 
	 * @param g
	 *            <a href="http://en.wikipedia.org/wiki/Directed_acyclic_graph"
	 *            > Directed Acyclic Graph</a>, where vertices are stored as
	 *            {@link java.util.HashMap HashMap} elements.
	 */
	public static <T> void tSortFix(java.util.Map<T, ArrayList<T>> g) {
	    java.util.ArrayList<T> tmp;
	    java.util.HashSet<T> P = new java.util.HashSet<T>();
	    P.addAll(g.keySet());
 
	    for (T t : P)
	        if (g.get(t) != null || !g.get(t).isEmpty()) {
	            (tmp = g.get(t)).remove(t);
	            for (T m : tmp)
	                if (!P.contains(m))
	                    g.put(m, new ArrayList<T>(0));
	        }
	}
 
	/**
	 * Creates a new {@code ArrayList} instance, containing input data.
	 * 
	 * @param data
	 *            List of mutable input elements.
	 * @return New {@link ArrayList} with input elements.
	 */
	public static <T> ArrayList<T> aList(T... data) {
		if (data == null)
			return new ArrayList<T>(0);
		int capacity = 8 + data.length + (data.length >> 3);
		ArrayList<T> list = new ArrayList<T>(capacity);
		Collections.addAll(list, data);
		return list;
	}
 
	/**
	 * Creates a new {@code ArrayList} instance, containing integer sequence
	 * between form and to. Sequence can be negative.
	 * 
	 * @param from
	 *            Integer with what sequence starts.
	 * @param to
	 *            Integer with what sequence ends.
	 * @return List of mutable integer sequence. {@code if (from == to)}, then
	 *         empty ArrayList is returned.
	 */
	public static ArrayList<Integer> mRange(int from, int to) {
		if (from == to)
			return new ArrayList<Integer>(0);
		if (from < to) {
			ArrayList<Integer> result = new ArrayList<Integer>(//
					Math.abs(from - to) + 1);
			for (int i = from; i <= to; i++)
				result.add(i);
			return result;
		}
		ArrayList<Integer> result = new ArrayList<Integer>(
				Math.abs(from - to) + 1);
		for (int i = from; i >= to; i--)
			result.add(i);
		return result;
	}
}