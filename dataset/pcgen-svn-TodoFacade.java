/**
 * TodoFacade.java
 * Copyright 2010 James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 14/06/2010 5:12:24 PM
 *
 * $Id: TodoFacade.java 12156 2010-06-14 10:03:19Z jdempsey $
 */
package pcgen.core.facade;

import pcgen.system.LanguageBundle;

/**
 * The interface <code>TodoFacade</code> defines what methods must be provided 
 * to support a Todo entry for a character. 
 *
 * <br/>
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2010-06-14 03:03:19 -0700 (Mon, 14 Jun 2010) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 12156 $
 */
public interface TodoFacade extends Comparable<TodoFacade>
{
	/** Event constant to indicate a request to change tabs. */
	public static String SWITCH_TABS = "SwitchTabs";
	
	/**
	 * The possible types of tabs for a character.
	 */
	public enum CharacterTab
	{
		SummaryTab, RacesTab, TemplatesTab, ClassesTab, SkillsTab, FeatsAbilitiesTab, DomainsTab, SpellsTab;
		

		/**
		 * @return The display name of the tab.
		 */
		public String getTabTile()
		{
			switch (this)
			{
				case SummaryTab:
					return LanguageBundle.getString("in_summary");
				case RacesTab:
					return LanguageBundle.getString("in_races");
				case TemplatesTab:
					return LanguageBundle.getString("in_Templates");
				case ClassesTab:
					return LanguageBundle.getString("in_clClass");
				case SkillsTab:
					return LanguageBundle.getString("in_skills");
				case FeatsAbilitiesTab:
					return LanguageBundle.getString("in_featsAbilities");
				case DomainsTab:
					return LanguageBundle.getString("in_domains");
				case SpellsTab:
					return LanguageBundle.getString("in_spells");
				default:
					throw new InternalError();
			}
		}
	}
	
	/**
	 * @return The message to be displayed. Is normally a key to localised 
	 * message, starting with in_ but may also be plain text.   
	 */
	public String getMessageKey();
	
	/**
	 * @return The character tab on which the task can be completed.
	 */
	public CharacterTab getTab();
	
	/**
	 * @return The internal name of the field where the task can be completed.
	 */
	public String getFieldName();
	
	/**
	 * @return The internal name of the sub tab where the task can be completed.
	 */
	public String getSubTabName();
}
