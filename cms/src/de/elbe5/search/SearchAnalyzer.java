/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import org.apache.lucene.analysis.Analyzer;

import java.util.HashSet;
import java.util.Arrays;

public class SearchAnalyzer extends Analyzer {

    protected HashSet<String> stopSet = new HashSet<String>();
    protected HashSet<String> exclusionSet = new HashSet<String>();

    public void addStopWord(String sw) {
        stopSet.add(sw);
    }

    public void addStopWords(String[] sw) {
        if (sw == null)
            return;
        stopSet.addAll(Arrays.asList(sw));
    }

    public void addExclusionWord(String ew) {
        exclusionSet.add(ew);
    }

    public void addExclusionWords(String[] ew) {
        if (ew == null)
            return;
        exclusionSet.addAll(Arrays.asList(ew));
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        //todo
        return null;
    }

    //todo
  /*public TokenStream tokenStream(String fieldName, Reader reader) {
    TokenStream result = new StandardTokenizer(Version.LUCENE_6_2_1, reader);
    //result = new StandardFilter(result);
    //result = new LowerCaseFilter(result);
    result = new StopFilter(Version.LUCENE_6_2_1, result, stopSet);
    return result;
  }*/

}

