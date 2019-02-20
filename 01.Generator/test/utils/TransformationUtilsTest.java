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

package utils;

import org.junit.Test;

import static common.util.TransformationUtils.dotJoinIfNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransformationUtilsTest {

 @Test
 public void testDotJoinIfNotEmpty() {
   assertTrue(dotJoinIfNotEmpty("","","").isEmpty());
   String first = "first";
   String second = "second";
   String third = "third";
   String last = "last";
   assertEquals(dotJoinIfNotEmpty("",second,""), second);
   assertEquals(dotJoinIfNotEmpty(first,"",""), first);
   assertEquals(dotJoinIfNotEmpty("","",last), last);
   assertEquals(dotJoinIfNotEmpty(first,second,""), first + "." + second);
   assertEquals(dotJoinIfNotEmpty("",second,"", last), second + "." + last);
   assertEquals(dotJoinIfNotEmpty(first,second,third, last), first + "." + second + "." + third + "." + last);
 }

}
