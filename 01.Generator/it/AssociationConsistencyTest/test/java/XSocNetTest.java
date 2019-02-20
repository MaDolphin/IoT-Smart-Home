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

package extendedsocnet;

import static org.junit.Assert.*;

import java.util.Optional;

import com.google.common.collect.Sets;
import extendedsocnet.xsocnet.*;
import extendedsocnet.xsocnet.persistence.*;
import org.junit.Test;
import org.junit.BeforeClass;

public class XSocNetTest {

  @BeforeClass
  public static void setUpPersistence() {
    Bla2Manager.init(Bla2StorageMock.get());
    Blubber2Manager.init(Blubber2StorageMock.get());
    Bla1Manager.init(Bla1StorageMock.get());
    Blubber1Manager.init(Blubber1StorageMock.get());
  }

  @Test
  public void testOneToOptional() {
    Bla2 b = Bla2Builder.get().setBlubb("12").build();
    Blubber2 bl2 = Blubber2Builder.get().setBl(1).setOptional2(b).build();

    // check if everything was set correctly
    assertTrue(b.getOptional2().isPresent());
    assertTrue(b.getOptional2() != null);

    // Try to delete bl2 from b (should throw an exception
    assertTrue(!Bla2Builder.over(b).setOptional2(Optional.empty()).isValid());

    // Another Bla2 that will steal Blubber2
    Bla2 b2 = Bla2Builder.get().setBlubb("13").build();
    assertTrue(Bla2Builder.over(b2).setOptional2(Optional.ofNullable(bl2)).isValid()); // TODO this should be true!!! FIX IT!!

    // Another Blubber2 tries to steal Bla2 from existing Blubber2
    assertTrue(!Blubber2Builder.get().setBl(2).setOptional2(b).isValid());

    // Check the situation for 4 elements
    Bla2 b3 = Bla2Builder.get().setBlubb("14").build();
    Blubber2 bl3 = Blubber2Builder.get().setBl(5).setOptional2(b3).build();

    // What does the builder say, when setting new Blubber?
    assertTrue(!Bla2Builder.over(b3).setOptional2(Optional.ofNullable(bl2)).isValid());

    // What does the builder say when setting new Bla?
    assertTrue(!Blubber2Builder.over(bl3).setOptional2(b).isValid());
  }

  @Test
  public void testMultipleToOne() {
    Blubber1 b = Blubber1Builder.get().setBl(1).build();

    // Check if new Bla1 object referencing Blubber1 can be build
    assertTrue(Bla1Builder.get().setBlubb("15").setOne1(b).setOneToMultiple1(Sets.newHashSet(b)).isValid());

    Bla1 a = Bla1Builder.get().setBlubb("16").setOne1(b).setOneToMultiple1(Sets.newHashSet(b)).build();

    // check if the associations has been set correctly
    assertTrue(b.sizeOne1s() == 1);
    assertTrue(b.sizeOneToMultiple1s() == 1);
    assertTrue(b.sizeOptional1s() == 0);
    assertTrue(b.sizeMultiple1s() == 0);

    // checking second case for object creation
    Blubber1 b_n = Blubber1Builder.get().setBl(1).build();
    Bla1 a_n = Bla1Builder.get().setBlubb("17").setOne1(b_n).setOneToMultiple1(Sets.newHashSet(b_n)).build();

    //(2) Check if new Bla1 object referencing Blubber1 can be build
    assertTrue(Bla1Builder.get().setBlubb("18").setOne1(b_n).setOneToMultiple1(Sets.newHashSet(b_n)).isValid());

    // test with 4 elements
    // checking what will happen if another b steals an a
    Blubber1 b1 = Blubber1Builder.get().setBl(1).build();
    Bla1 a1 = Bla1Builder.get().setBlubb("19").setOne1(b1).setOneToMultiple1(Sets.newHashSet(b1)).build();
    Blubber1 b2 = Blubber1Builder.get().setBl(5).build();
    Bla1 a2 = Bla1Builder.get().setBlubb("20").setOne1(b2).setOneToMultiple1(Sets.newHashSet(b2)).build();

    //Check if builder allows the changes
    assertTrue(Blubber1Builder.over(b1).setOne1(Sets.newHashSet(a1, a2)).isValid());
    b1.addOne1(a2);

    assertTrue(a1.getOne1() != null);
    assertTrue(a2.getOne1().equals(b1));
    assertTrue(b2.sizeOne1s() == 0);

    //Check if Blubber can steal from another one
    assertTrue(Blubber1Builder.over(b).setOne1(Sets.newHashSet(a)).isValid());

    //Check if Blubber can steal from another one
    assertTrue(Bla1Builder.over(a).setOne1(b_n).isValid());

    Blubber1 b_m = Blubber1Builder.get().setBl(7).build();

    //checking another interesting fact
    assertTrue(Blubber1Builder.over(b_m).setOne1(Sets.newHashSet(a_n)).isValid());

    b_m.addOne1(a_n);
    assertTrue(b_n.sizeOne1s() == 0);
    assertTrue(b_m.sizeOne1s() == 1);

    //Checking if objects can be removed:
    assertTrue(!Blubber1Builder.over(b).setOne1(Sets.newHashSet()).isValid());
  }
}
