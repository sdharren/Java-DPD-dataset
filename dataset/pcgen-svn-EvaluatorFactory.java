/**
 * pcgen.core.term.EvaluatorFactory.java
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 03-Oct-2008 17:46:37
 *
 * Current Ver: $Revision:$
 * Last Editor: $Author:$
 * Last Edited: $Date:$
 *
 */

package pcgen.core.term;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.util.Logging;

public class EvaluatorFactory {

	Pattern internalVarPattern;
	Map<String, TermEvaluatorBuilder> BuilderStore;
	
	private Map<String, TermEvaluator> SrcNeutralEvaluatorStore = 
			new HashMap<String, TermEvaluator>();
	private Map<String, Map<String, TermEvaluator>> SrcDependantEvaluatorStore = 
			new HashMap<String, Map<String, TermEvaluator>>();


	public static final EvaluatorFactory PC =
			new EvaluatorFactory(true, TermEvaluatorBuilderPCVar.values());
	
	public static final EvaluatorFactory EQ =
			new EvaluatorFactory(false, TermEvaluatorBuilderEQVar.values());

	private EvaluatorFactory (
			boolean addStats,
			final TermEvaluatorBuilder[] termEvaluatorBuilders)
	{
		TermEvaluatorBuilder[] evals = (addStats) ?
				addStatBuilder(termEvaluatorBuilders) :
				termEvaluatorBuilders;

		BuilderStore     = new TreeMap<String, TermEvaluatorBuilder>();
		StringBuilder sb = new StringBuilder("^(");

		boolean add = false;

		for (TermEvaluatorBuilder e : evals) {
			if (add) {
				sb.append("|");
			} else {
				add = true;
			}
			sb.append(e.getTermConstructorPattern());
			
			String[] keys = e.getTermConstructorKeys();
			for (String k : keys) {
				BuilderStore.put(k, e);
			}
		}

		sb.append(")");
		internalVarPattern = Pattern.compile(sb.toString());	
	}
	
	private static TermEvaluatorBuilder[] addStatBuilder(
			TermEvaluatorBuilder[] builderArray)
	{
		int end = builderArray.length;

		TermEvaluatorBuilder[] tempArray = new TermEvaluatorBuilder[end + 1];

		System.arraycopy(builderArray, 0, tempArray, 0, end);

		tempArray[end] = makeStatBuilder();

		return tempArray;
	}

	private static TermEvaluatorBuilder makeStatBuilder()
	{
		Collection<PCStat> stats = Globals.getContext().ref.getConstructedCDOMObjects(PCStat.class);
		List<String> s = new LinkedList<String>();
		StringBuilder pSt = new StringBuilder(stats.size() * 4 + 6);

		pSt.append("(?:");
		boolean add1 = false;
		for (PCStat stat : stats)
		{
			if (add1) {
				pSt.append("|");
			} else {
				add1 = true;
			}
			pSt.append(stat.getAbb());
			s.add(stat.getAbb());
		}
		pSt.append(")");

		return new TermEvaluatorBuilderPCStat(pSt.toString(), s.toArray(new String[s.size()]), false);
	}

	private TermEvaluator makeTermEvaluator(
			String term,
			String source) {
		
		Matcher mat = internalVarPattern.matcher(term);

		if (mat.find()) {
			String matchedPortion = mat.group(1);
			TermEvaluatorBuilder f = BuilderStore.get(matchedPortion);

			try
			{
				if (f.isEntireTerm() &&
					(term.length() != matchedPortion.length()))
				{
					return null;
				}
				else
				{
					return f.getTermEvaluator(term, source, matchedPortion);
				}
			}
			catch (TermEvaulatorException e)
			{
				Logging.log(Logging.DEBUG, e.toString());
			}
		}
		
		return null;
	}

	public TermEvaluator getTermEvaluator (
			String term,
			String source) {

		Map<String, TermEvaluator> inner = SrcDependantEvaluatorStore.get(term);

		if (inner == null)
		{
			TermEvaluator evaluator = SrcNeutralEvaluatorStore.get(term);
			if (evaluator != null) {
				return evaluator; 
			}
		}
		else
		{
			TermEvaluator evaluator = inner.get(source);
			if (evaluator != null) {
				return evaluator; 
			}
		}

		TermEvaluator evaluator = makeTermEvaluator(term, source);

		if (evaluator == null)
		{
			return null;
		}
		
		if (evaluator.isSourceDependant())
		{
			Map<String, TermEvaluator> i = SrcDependantEvaluatorStore.get(term);
			Map<String, TermEvaluator> j = (i == null) ? new HashMap<String, TermEvaluator>() : i;
			j.put(source, evaluator);
			SrcDependantEvaluatorStore.put(term, j);	
		}
		else
		{
			SrcNeutralEvaluatorStore.put(term, evaluator);
		}

		return evaluator;
	}
}
