/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.util.HashSet;
import java.util.Arrays;
import java.io.Reader;

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

  public TokenStream tokenStream(String fieldName, Reader reader) {
    TokenStream result = new StandardTokenizer(Version.LUCENE_31, reader);
    //result = new StandardFilter(result);
    //result = new LowerCaseFilter(result);
    result = new StopFilter(Version.LUCENE_31, result, stopSet);
    return result;
  }

}

