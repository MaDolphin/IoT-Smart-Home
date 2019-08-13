/* (c) https://github.com/MontiCore/monticore */

package extendedsocnet;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;
import org.junit.Assert;

import java.util.Optional;

import dex.data.DataConsistencyViolationException;
import dex.data.NotImplementedException;
import extendedsocnet.xsocnet.*;
import static org.junit.Assert.*;

public class XSocNetTest {
  @Test
  public void testFriend1() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    assertTrue(!tag1.getBoyfriend().isPresent());
    assertTrue(!tag1.getGirlfriend().isPresent());
    tag1.setBoyfriend(Optional.ofNullable(tag2));
    assertTrue(tag1.getBoyfriend().isPresent());
    assertTrue(tag2.getGirlfriend().isPresent());
    assertTrue(tag1.getBoyfriend().get().equals(tag2));
    assertTrue(tag2.getGirlfriend().get().equals(tag1));
    tag2.setGirlfriend(Optional.ofNullable(tag1));
    assertTrue(tag1.getBoyfriend().get().equals(tag2));
    assertTrue(tag2.getGirlfriend().get().equals(tag1));
  }
  
  @Test
  public void testFriend2() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    tag1.setBoyfriend(Optional.ofNullable(tag2));
    assertTrue(tag2.getGirlfriend().isPresent());
    tag1.setBoyfriend(Optional.empty());
    assertTrue(!tag2.getGirlfriend().isPresent());
    assertTrue(!tag1.getBoyfriend().isPresent());
  }
  
  @Test
  public void testFriend3() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    Tag tag3 = TagBuilder.get().setConfirmed(true).build();
    tag1.setBoyfriend(Optional.ofNullable(tag2));
    tag1.setBoyfriend(Optional.ofNullable(tag3));
    assertTrue(tag1.getBoyfriend().get().equals(tag3));
    assertTrue(tag3.getGirlfriend().get().equals(tag1));
    assertTrue(!tag2.getGirlfriend().isPresent());
  }
  
  @Test
  public void testFriend4() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    tag1.setGirlfriend(Optional.ofNullable(tag1));
    tag2.setGirlfriend(Optional.ofNullable(tag2));
    tag1.setGirlfriend(Optional.ofNullable(tag2));
    assertTrue(tag1.getGirlfriend().isPresent());
    assertTrue(!tag2.getGirlfriend().isPresent());
    assertTrue(!tag1.getBoyfriend().isPresent());
    assertTrue(tag2.getBoyfriend().isPresent());
    
  }
  
  @Test
  public void testCar1() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    tag.setCar(Optional.ofNullable(p1));
    assertTrue(tag.getCar().isPresent());
    assertTrue(tag.getCar().get().equals(p1));
    assertTrue(p1.containsCar(tag));
  }
  
  @Test
  public void testCar2() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    p1.addCar(tag1);
    assertTrue(tag1.getCar().isPresent());
    assertTrue(tag1.getCar().get().equals(p1));
    assertTrue(p1.containsCar(tag1));
  }
  
  @Test
  public void testCar3() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    assertTrue(!tag.getCar().isPresent());
    assertTrue(p1.sizeCars() == 0);
    assertTrue(!p1.iteratorCars().hasNext());
    assertTrue(!p1.containsCar(tag));
  }
  @Test
  public void testHouseOccupants1() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    tag.setHouse(Optional.ofNullable(tag));
    assertTrue(tag.getHouse().isPresent());
    assertTrue(tag.getHouse().get().equals(tag));
    assertTrue(tag.containsOccupants(tag));
  }
  
  @Test
  public void testHouseOccupants2() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    tag1.setHouse(Optional.ofNullable(tag2));
    assertTrue(tag1.getHouse().isPresent());
    assertTrue(tag1.getHouse().get().equals(tag2));
    assertTrue(tag2.containsOccupants(tag1));
  }
  
  @Test
  public void testHouseOccupants3() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    tag2.addOccupants(tag1);
    assertTrue(tag1.getHouse().isPresent());
    assertTrue(tag1.getHouse().get().equals(tag2));
    assertTrue(tag2.containsOccupants(tag1));
  }
  
  @Test
  public void testHouseOccupants4() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    assertTrue(!tag.getHouse().isPresent());
    assertTrue(tag.sizeOccupantss() == 0);
    assertTrue(!tag.iteratorOccupantss().hasNext());
  }
  @Test
  public void testLunch1() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    assertTrue(!tag.getLunch().isPresent());
    tag.setLunch(Optional.ofNullable(p1));
    assertTrue(tag.getLunch().get().equals(p1));
    assertTrue(p1.getLunch().get().equals(tag));
  }
  
  @Test
  public void testLunch2() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    assertTrue(!p1.getLunch().isPresent());
    p1.setLunch(Optional.ofNullable(tag));
    assertTrue(p1.getLunch().get().equals(tag));
    assertTrue(tag.getLunch().get().equals(p1));
  }
  
  @Test
  public void testLunch3() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    p1.setLunch(Optional.ofNullable(tag1));
    p1.setLunch(Optional.ofNullable(tag2));
    assertTrue(p1.getLunch().get().equals(tag2));
    assertTrue(!tag1.getLunch().isPresent());
    assertTrue(tag2.getLunch().isPresent());
  }
  
  @Test
  public void testPaper1() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    assertTrue(p1.sizePapers() == 0);
    assertTrue(p1.isEmptyPapers());
    p1.addPaper(tag);
    assertTrue(p1.containsPaper(tag));
    assertTrue(p1.sizePapers() == 1);
  }
  
  @Test
  public void testPaper2() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    Photo p2 = PhotoBuilder.get().setWidth(190).setHeight(121.).build();
    ArrayList<Tag> listTag = new ArrayList<>();
    ArrayList<Photo> listPhoto = new ArrayList<>();
    listPhoto.add(p1);
    listPhoto.add(p2);
    listTag.add(tag1);
    listTag.add(tag2);
    p1.addAllPapers(listTag);
    p2.addAllPapers(listTag);
    assertTrue(tag1.containsAllPapers(listPhoto));
    assertTrue(tag2.containsAllPapers(listPhoto));
  }
  
  @Test
  public void testPaper3() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    p1.addPaper(tag1);
    p1.addPaper(tag1);
    assertTrue("Added a value twice", p1.sizePapers() == 1);
    try {
      p1.addPaper(null);
      Assert.fail();
    }
    catch (dex.data.DataConsistencyViolationException e) {
    }
    assertTrue("Added a null value", p1.sizePapers() == 1);
    
  }
  
  @Test
  public void testPaper4() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    assertTrue(p1.isEmptyPapers());
    p1.addPaper(tag1);
    p1.addPaper(tag2);
    assertTrue(p1.sizePapers() == 2);
    assertTrue(tag1.containsPaper(p1));
    p1.removePaper(tag1);
    assertTrue(!tag1.containsPaper(p1));
    assertTrue(!p1.containsPaper(tag1));
    tag2.removePaper(p1);
    assertTrue(!p1.containsPaper(tag2));
    assertTrue(!tag2.containsPaper(p1));
  }
  
  @Test
  public void testPaper5() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    p1.addPaper(tag1);
    p1.removePaper(tag1);
    tag1.removePaper(p1);
  }
  @Test
  public void testApple1() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    assertTrue(!tag1.getApple().isPresent());
    tag1.setApple(Optional.ofNullable(tag2));
    assertTrue(tag1.getApple().isPresent());
    assertTrue(tag1.getApple().get().equals(tag2));
    tag1.setApple(Optional.ofNullable(tag1));
    assertTrue(tag1.getApple().get().equals(tag1));
    assertTrue(!tag2.getApple().isPresent());
  }
  
  @Test
  public void testApple2() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    assertTrue(!tag.getApple().isPresent());
    tag.setApple(Optional.ofNullable(tag));
    assertTrue(tag.getApple().isPresent());
    assertTrue(tag.getApple().get().equals(tag));
  }
  
  @Test
  public void testApple3() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    tag1.setApple(Optional.ofNullable(tag1));
    tag1.setApple(Optional.ofNullable(tag2));
    assertTrue(!tag1.getApple().equals(tag1));
  }
  
  @Test
  public void testApple4() {
    Tag tag = TagBuilder.get().setConfirmed(true).build();
    tag.setApple(Optional.ofNullable(tag));
    tag.setApple(Optional.empty());
  }
  
  @Test
  public void testOrange1() {
    Tag tag1 = TagBuilder.get().setConfirmed(true).build();
    Tag tag2 = TagBuilder.get().setConfirmed(false).build();
    assertTrue(tag1.sizeOranges() == 0);
    tag1.addOrange(tag2);
    assertTrue(tag1.containsOrange(tag2));
    try {
      tag1.addOrange(null);
      Assert.fail();
    }
    catch (dex.data.DataConsistencyViolationException e) {
    }
    assertTrue(tag1.containsOrange(tag2));
    assertTrue(!tag1.isEmptyOranges());
    assertTrue(tag1.sizeOranges() == 1);
    tag1.removeOrange(tag2);
    assertTrue(tag2.sizeOranges() == 0);
    tag1.addOrange(tag2);
    tag1.clearOranges();
    assertTrue(tag2.sizeOranges() == 0);
    assertTrue(tag2.isEmptyOranges());
  }
}
