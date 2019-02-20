/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package generation.association;

import static org.junit.Assert.assertTrue;
import generation.GeneratorTestBase;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import dex.junit.categories.AssociationTests;
/**
 * BaseClass for Association Tests.
 * 
 * @author (last commit) $Author$
 */
@Category(AssociationTests.class)
public abstract class AssociationTestBase extends GeneratorTestBase {  
  
  private static String DIR_PATH = "testmodels/DiagramsWithAssociations/correct";
  
  private static List<String> filenameList;
  
  @BeforeClass
  public static void init() {
    List<String> filenames = new ArrayList<String>();
    File dir = new File(DIR_PATH);
    for (File f : dir.listFiles()) {
      if (!f.isDirectory()) {
        filenames.add(f.getName());
      }
    }
    
    filenameList = filenames.stream().sorted().collect(Collectors.toList());
  }
  
  /**
   * The number of processed files within a single VM should be limited because of memory problems
   * 
   * @param from start index 
   * @param to   end index
   */
  protected void test(int from, int to) {
    // TODO: currently commented out because the input model has changed
   /* for (int i = from; i <= to; i++) {
      assertTrue(filenameList.size() > i);
      String filename = filenameList.get(i);
      System.out.println(ManagementFactory.getRuntimeMXBean().getName() + ":" + filename);
      testCorrect("testmodels/DiagramsWithAssociations/correct/" + filename);
    }*/
  }
}
