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
import java.util.Set;
import com.google.common.collect.Sets;
import dex.server.access.ServerAccess;
import dex.server.access.ServerAccessException;
import extendedsocnet.xsocnet.*;
import extendedsocnet.xsocnet.persistence.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class PersistenceTest {

  @BeforeClass
  public static void setUpPersistence() {
    // commented out because the dev-server is down
    /*
    XSocNetServerAccess sv = new XSocNetServerAccess();
    ServerAccess.initialize(sv);
    try {
      ServerAccess.instance().registerUser("x", "x");
      ServerAccess.instance().login("x", "x");
    }
    catch (ServerAccessException e) {
      e.printStackTrace();
    }

    Bla2Manager.init(Bla2PersistenceStorage.get());
    Blubber2Manager.init(Blubber2PersistenceStorage.get());
    Bla1Manager.init(Bla1PersistenceStorage.get());
    Blubber1Manager.init(Blubber1PersistenceStorage.get()); */
    Bla2Manager.init(Bla2StorageMock.get());
    Blubber2Manager.init(Blubber2StorageMock.get());
    Bla1Manager.init(Bla1StorageMock.get());
    Blubber1Manager.init(Blubber1StorageMock.get());
  }

  @Test
  public void testOneToOptional() {
    Bla2 b = Bla2Builder.get().setBlubb("asd").build();
    Blubber2 bl2 = Blubber2Builder.get().setBl(1).setOptional2(b).build();

    // check if everything was set correctly
    assertTrue(b.getOptional2().isPresent());
    assertTrue(b.getOptional2() != null);

    // Try to delete bl2 from b (should throw an exception
    assertTrue(!Bla2Builder.over(b).setOptional2(Optional.empty()).isValid());

    // Another Bla2 that will steal Blubber2
    Bla2 b2 = Bla2Builder.get().setBlubb("qwe").build();
    assertTrue(Bla2Builder.over(b2).setOptional2(Optional.ofNullable(bl2)).isValid());

    // Another Blubber2 tries to steal Bla2 from existing Blubber2
    assertTrue(!Blubber2Builder.get().setBl(2).setOptional2(b).isValid());

    // Check the situation for 4 elements
    Bla2 b3 = Bla2Builder.get().setBlubb("asd").build();
    Blubber2 bl3 = Blubber2Builder.get().setBl(5).setOptional2(b3).build();

    // What does the builder say, when setting new Blubber?
    assertTrue(!Bla2Builder.over(b3).setOptional2(Optional.ofNullable(bl2)).isValid());

    // What does the builder say when setting new Bla?
    assertTrue(!Blubber2Builder.over(bl3).setOptional2(b).isValid());
  }

  @Test
  public void testMultipleToOne() {
    Blubber1 b = Blubber1Builder.get().setBl(12).build();

    // Check if new Bla1 object referencing Blubber1 can be build
    assertTrue(Bla1Builder.get().setBlubb("asd2").setOne1(b).setOneToMultiple1(Sets.newHashSet(b)).isValid());

    Bla1 a = Bla1Builder.get().setBlubb("asd2").setOne1(b).setOneToMultiple1(Sets.newHashSet(b)).build();

    // check if the associations has been set correctly
    assertTrue(b.sizeOne1s() == 1);
    assertTrue(b.sizeOneToMultiple1s() == 1);
    assertTrue(b.sizeOptional1s() == 0);
    assertTrue(b.sizeMultiple1s() == 0);

    // checking second case for object creation
    Blubber1 b_n = Blubber1Builder.get().setBl(13).build();
    Bla1 a_n = Bla1Builder.get().setBlubb("qq3").setOne1(b_n).setOneToMultiple1(Sets.newHashSet(b_n)).build();

    //(2) Check if new Bla1 object referencing Blubber1 can be build
    assertTrue(Bla1Builder.get().setBlubb("ww3").setOne1(b_n).setOneToMultiple1(Sets.newHashSet(b_n)).isValid());

    // test with 4 elements
    // checking what will happen if another b steals an a
    Blubber1 b1 = Blubber1Builder.get().setBl(14).build();
    Bla1 a1 = Bla1Builder.get().setBlubb("asd").setOne1(b1).setOneToMultiple1(Sets.newHashSet(b1)).build();
    Blubber1 b2 = Blubber1Builder.get().setBl(54).build();
    Bla1 a2 = Bla1Builder.get().setBlubb("3qwe4").setOne1(b2).setOneToMultiple1(Sets.newHashSet(b2)).build();

    //Check if builder allows the changes
    assertTrue(Blubber1Builder.over(b1).setOne1(Sets.newHashSet(a1, a2)).isValid());
    Blubber1Builder.over(b1).setOne1(Sets.newHashSet(a1,a2)).build();

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
    Set<Bla1> bbb = Sets.newHashSet(b_m.iteratorOne1s());
    bbb.add(a_n);
    Blubber1Builder.over(b_m).setOne1(bbb).build();
    assertTrue(b_n.sizeOne1s() == 0);
    assertTrue(b_m.sizeOne1s() == 1);

    //Checking if objects can be removed:
    assertTrue(!Blubber1Builder.over(b).setOne1(Sets.newHashSet()).isValid());
  }

  //@Test
  public void testMultipleToOptional() {
    Blubber1 b = Blubber1Builder.get().setBl(11).build();
    Bla1 a = Bla1Builder.get().setBlubb("asd_1").setOne1(b).setOneToMultiple1(Sets.newHashSet(b)).build();

    // Check if new blubber is allowed
    assertTrue(Bla1Builder.over(a).setOptional1(Optional.ofNullable(b)).isValid() == true);

    // Checking if new bla is allowed
    assertTrue(Blubber1Builder.over(b).setOptional1(Sets.newHashSet(a)).isValid() == true);

    b.addOptional1(a);
    //Check if optional has been set
    assertTrue(b.sizeOptional1s() == 1);
    //Check if optional has been set (opposite class)
    assertTrue(a.getOptional1().isPresent());

    //Check if stealing blubber is successfull
    assertTrue(Blubber1Builder.get().setBl(41).setOptional1(Sets.newHashSet(a)).isValid() == true);

    //Check if stealing bla is allowed "
    assertTrue(Bla1Builder.get().setBlubb("eqw1").setOne1(b).setOneToMultiple1(Sets.newHashSet(b))
        .setOptional1(Optional.ofNullable(b)).isValid() == true);

    // checking second case for object creation
    Blubber1 b_n = Blubber1Builder.get().setBl(91).build();
    Bla1 a_n = Bla1Builder.get().setBlubb("q213weq1").setOne1(b_n).setOneToMultiple1(Sets.newHashSet(b_n)).build();

    //Check stealing blubber
    assertTrue(Bla1Builder.over(a_n).setOptional1(Optional.ofNullable(b)).isValid() == true);

    //Checking it the other way around
    assertTrue(Blubber1Builder.over(b_n).setOptional1(Sets.newHashSet(a)).isValid() == true);

    //Checking removal of bla
    assertTrue(Blubber1Builder.over(b_n).setOptional1(Sets.newHashSet()).isValid() == true);

    //Check removal blubber
    assertTrue(Bla1Builder.over(a_n).setOptional1(Optional.empty()).isValid() == true);
  }
}
