/* (c) https://github.com/MontiCore/monticore */

package onecardinalitytest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import java.util.Optional;
import dex.data.DataConsistencyViolationException;
import dex.data.NotImplementedException;
import onecardinalitytest.testcard.*;
import static org.junit.Assert.*;

public class TestCardTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testOneToMany() {
    // [1] Tag <-> Photo [*]

    Tag t = TagBuilder.get().build();
    Photo p = PhotoBuilder.get().setName("a").setTag(t).build();

    // try to add an existing object
    Tag t2 = TagBuilder.get().build();
    Photo p2 = PhotoBuilder.get().setName("b").setTag(t2).build();

    // This should work -> adapt the rawSetMethod in Photo
    t.addPhoto(p2);

    assertTrue(t.sizePhotos() == 2);
    assertTrue(t2.sizePhotos() == 0);
  }

  @Test
  public void testOneToManyFalse() {
    // [1] Tag <-> Photo [*]
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    Photo p = PhotoBuilder.get().build();
  }

  @Test
  public void testOneToManyFalse2() {
    // [1] Tag <-> Photo [*]
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    Photo p = PhotoBuilder.get().setName("asdf").build();
  }

  @Test
  public void testOneToManyCreate() {
    // [1] Tag <-> Photo [*]

    Tag t = TagBuilder.get().build();
    Photo p = PhotoBuilder.get().setName("a").setTag(t).build();
    Photo p2 = PhotoBuilder.get().setName("b").setTag(t).build();

    // This should work -> adapt the rawSetMethod in Photo
    t.addPhoto(p2);

    Tag t2 = TagBuilder.get().setPhoto(Sets.newHashSet(p)).build();

    assertTrue(t.sizePhotos() == 1);
    assertTrue(t2.sizePhotos() == 1);
  }

  @Test
  public void testOneToManyClear() {
    // [1] Tag <-> Photo [*]
    Tag t = TagBuilder.get().build();
    Photo p = PhotoBuilder.get().setName("a").setTag(t).build();

    thrown.expect(dex.data.DataConsistencyViolationException.class);
    t.clearPhotos();
  }

  @Test
  public void testOneToOptional() {
    // [1] Aa <-> Bb [0..1]"
    // test setup
    Aa a = AaBuilder.get().build();
    Bb b = BbBuilder.get().setAa(a).build();

    // remove an object from Tag
    // This should throw an exception
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    a.setBb(Optional.empty());
  }

  @Test
  public void testOneToOptionalFalse() {
    // [1] Aa <-> Bb [0..1]"
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    Bb b = BbBuilder.get().build();
  }

  @Test
  public void testOneToOptionalCreate() {
    // [1] Aa <-> Bb [0..1]"
    // test setup
    Aa a = AaBuilder.get().build();
    Bb b = BbBuilder.get().setAa(a).build();

    thrown.expect(dex.data.DataConsistencyViolationException.class);
    Bb b2 = BbBuilder.get().setAa(a).build();
  }

  @Test
  public void testOneToOptional2() {
    // [1] Tag <-> Photo [0..1]"
    // test setup
    Aa a = AaBuilder.get().build();
    Bb b = BbBuilder.get().setAa(a).build();

    Aa a2 = AaBuilder.get().build();
    Bb b2 = BbBuilder.get().setAa(a2).build();

    // this should throw an exception
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    a.setBb(Optional.ofNullable(b2));
  }

  @Test
  public void testOneToManyToMany() {
    // association [1..*] C <-> D [*];
    C c = CBuilder.get().setBla("a").build();
    D d = DBuilder.get().setC(Sets.newHashSet(c)).build();

    // c.clearDs(); // TODO: possible but need to check if a D has at least one
    // C

    C c1 = CBuilder.get().setBla("a").build();
    C c2 = CBuilder.get().setBla("b").build();
    D d2 = DBuilder.get().setC(Sets.newHashSet(c1, c2)).build();

    assertTrue(d2.sizeCs() == 2);

    c1.clearDs(); // possible but need to check if a D has at least one C

    assertTrue(d2.sizeCs() == 1);

    // Now an exception has to be thrown
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    c2.clearDs();
  }

  @Test
  public void testOneToManyToManyCreate() {
    // association [1..*] C <-> D [*];
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    D d = DBuilder.get().build();
  }

  @Test
  public void testOneToManyToOptional() {
    // association [1..*] E <-> F [0..1];
    E e = EBuilder.get().setBla("e").build();
    F f = FBuilder.get().setE(Sets.newHashSet(e)).build();

    // this should throw an exception
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    e.setF(Optional.empty());

  }

  @Test
  public void testOneToManyToOptional2() {
    // association [1..*] E <-> F [0..1];
    E e = EBuilder.get().setBla("e").build();
    F f = FBuilder.get().setE(Sets.newHashSet(e)).build();

    // e.setF(Optional.empty()); // this should throw an exception

    E e1 = EBuilder.get().setBla("e").build();
    E e2 = EBuilder.get().setBla("e2").build();
    F f2 = FBuilder.get().setE(Sets.newHashSet(e1, e2)).build();

    e1.setF(Optional.empty()); // this is okay

    // this should throw an exception
    thrown.expect(dex.data.DataConsistencyViolationException.class);
    e2.setF(Optional.empty());
  }

  @Test
  public void testOptionalToOptional() {
    // association [0..1] G <-> H [0..1];
    G g = GBuilder.get().setBlubb("a").build();
    H h = HBuilder.get().build();
    H h2 = HBuilder.get().build();

    g.setH(Optional.ofNullable(h));
    g.setH(Optional.ofNullable(h2));
  }
  @Test
  public void testClearString1() {
  thrown.expect(dex.data.DataConsistencyViolationException.class);
  G g = GBuilder.get().setBlubb("").build();
  }
}
