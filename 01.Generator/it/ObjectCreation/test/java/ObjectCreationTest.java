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

package objectcreationtest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import dex.data.DataConsistencyViolationException;
import dex.data.NotImplementedException;
import objectcreation.objectcreation.*;

import static org.junit.Assert.*;

public class ObjectCreationTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  public void test1() {
    Aa a = AaBuilder.get().setName("a").build();
    Cc c = CcBuilder.get().setName("c").build();
    Bb b = BbBuilder.get().setName("b").setAa(a).setCc(Optional.ofNullable(c)).build();
    
    assertTrue(a.getBb().get().equals(b));
    assertTrue(b.getAa().equals(a));
    assertTrue(b.getCc().get().equals(c));
    assertTrue(c.getBb().get().equals(b));
    
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    Bb b2 = BbBuilder.get().setName("b2").setAa(a).setCc(Optional.ofNullable(c))
        .build();
    
    assertTrue(a.getBb().get().equals(b));
    assertTrue(b.getAa().equals(a));
    assertTrue(b.getCc().get().equals(c));
    assertTrue(c.getBb().get().equals(b));
  }
  
  @Test
  public void test2() {
    Aa1 a = Aa1Builder.get().setName("a").build();
    Bb1 b = Bb1Builder.get().setName("b").setAa1(a).build();
    Cc1 c = Cc1Builder.get().setName("c").setA1(b).build();
    
    assertTrue(a.getBb1().get().equals(b));
    assertTrue(b.getAa1().equals(a));
    assertTrue(b.getA1().get().equals(c));
    assertTrue(c.getA1().equals(b));
    
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    Bb1 b2 = Bb1Builder.get().setName("b2").setAa1(a).setA1(Optional.ofNullable(c))
        .build();
    
    assertTrue(a.getBb1().get().equals(b));
    assertTrue(b.getAa1().equals(a));
    assertTrue(b.getA1().get().equals(c));
    assertTrue(c.getA1().equals(b));
  }
  
}
