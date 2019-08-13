/* (c) https://github.com/MontiCore/monticore */

package integrationtest;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;

import integrationtest.socnet.*;
import dex.data.DataConsistencyViolationException;
import dex.data.NotImplementedException;

public class SocNetTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  private Relationship createRelation(Date d) {
    return RelationshipBuilder.get().setPending(true)
        .setRequested(d).setAccepted(new Date(d.getTime() + 10000)).setRelationType(RelationType.FOLLOWER)
        .setInvited(createPerson(2)).setInitiated(createPerson(9))
        .build();
  }
  
  private InstantMessage createIM(String content) {
    return ((InstantMessageBuilder)InstantMessageBuilder.get().setContent(content)
        .setTimestamp(new Date())
        .setSent(createPerson(39337)))
        .build();
  }
  
  private Group createGroup(String purpose) {
    return GroupBuilder.get().setOpen(true).setPurpose(purpose).setCreated(new Date())
        .setProfileName("foo")
        .build();
  }
  private PhotoMessage createPhotoMessage(String content) {
    return ((PhotoMessageBuilder)PhotoMessageBuilder.get().setTimestamp(new
        Date()).setContent(content)
        .setPicture(Sets.newHashSet(createPhoto(130)))
        .setSent(createPerson(39337)))
        .build();// TODO (mit replyTo?)
  }
  private PhotoMessage createPhotoMessage(String content, Photo p1) {
    return ((PhotoMessageBuilder)PhotoMessageBuilder.get().setTimestamp(new Date()).setContent(content)
        .setPicture(Sets.newHashSet(p1))
        .setSent(createPerson(39337)))
        .build();
  }
  
  private Tag createTag(boolean confirmed) {
    Photo p1 = createPhoto(180);
    Person student = createPerson(52074);
    return TagBuilder.get().setConfirmed(confirmed).setTagged(student).setPhoto(p1).build();
  }
  
  private Person createPerson(int zip) {
    return PersonBuilder.get().setCity("Aachen").setCountry("Germany")
        .setProfileName("testStudent").setDateOfBirth(new Date()).setFirstName("Max").setSecondName("Winter")
        .setLastVisit(new Date()).setZip(zip).build();
  }
  
  private Photo createPhoto(int width) {
    return PhotoBuilder.get().setWidth(width).setHeight(120.).build();
  }
  
  @Test
  public void testInvited1() {
    Date d1 = new Date();
    Relationship r1 = createRelation(d1);
    Person p1 = createPerson(512);
    Group g1 = createGroup("ffff");
    r1.setInvited(p1);
    assertTrue(r1.getInvited().equals(p1));
    assertTrue(p1.containsInvited(r1));
    r1.setInvited(g1);
  }
  
  @Test
  public void testInvited2() {
    Date d1 = new Date();
    Relationship r1 = createRelation(d1);
    Person p1 = createPerson(512);
    p1.addInvited(r1);
    assertTrue(r1.getInvited().equals(p1));
    assertTrue(p1.containsInvited(r1));
  }
  
  @Test
  public void testInitiated1() {
    Date d1 = new Date();
    Relationship r1 = createRelation(d1);
    Person p1 = createPerson(512);
    r1.setInitiated(p1);
    assertTrue(r1.getInitiated().equals(p1));
    assertTrue(p1.containsInitiated(r1));
  }
  
  @Test
  public void testInitiated2() {
    Date d1 = new Date();
    Relationship r1 = createRelation(d1);
    Person p1 = createPerson(512);
    p1.addInitiated(r1);
    assertTrue(r1.getInitiated().equals(p1));
    assertTrue(p1.containsInitiated(r1));
  }
  
  @Test
  public void testGroupSetters1() {
    Group group1 = createGroup("none");
    group1.setProfileName("test");
    assertTrue(group1.getProfileName().equals("test"));
  }
  
  @Test
  public void testGroupSetters2() {
    Group group1 = createGroup("none");
    try {
      group1.setProfileName(null);
      Assert.fail();
    }
    catch (DataConsistencyViolationException e) {
    }
    
  }
  
  @Test
  public void testGroupSetters3() {
    Group group1 = createGroup("none");
    try {
      group1.getNumOfPosts();
      Assert.fail();
    }
    catch (NotImplementedException e) {
    }
  }
  
  @Test
  public void testPersonSetters1() {
    Person p1 = createPerson(42);
    Date d1 = new Date();
    p1.setLastVisit(d1);
    assertTrue(p1.getLastVisit() == d1);
  }
  
  @Test
  public void testPersonSetters2() {
    Person p1 = createPerson(42);
    try {
      p1.setLastVisit(null);
      Assert.fail();
    }
    catch (DataConsistencyViolationException e) {
    }
  }
  
  @Test
  public void testIM1() {
    InstantMessage instantMessage = createIM("a");
    instantMessage.setReplyTo(Optional.ofNullable(instantMessage));
    assertTrue(instantMessage.getReplyTo().isPresent());
    assertTrue(instantMessage.getReplyTo().get().equals(instantMessage));
    assertTrue(instantMessage.containsInstantMessage(instantMessage));
    assertTrue(instantMessage.sizeInstantMessages() == 1);
  }
  
  @Test
  public void testIM2() {
    InstantMessage instantMessage1 = createIM("a");
    InstantMessage instantMessage2 = createIM("b");
    instantMessage1.setReplyTo(Optional.ofNullable(instantMessage2));
    assertTrue(instantMessage1.getReplyTo().isPresent());
    assertTrue(instantMessage1.getReplyTo().get().equals(instantMessage2));
    assertTrue(instantMessage2.containsInstantMessage(instantMessage1));
  }
  
  @Test
  public void testIM3() {
    InstantMessage instantMessage1 = createIM("a");
    InstantMessage instantMessage2 = createIM("b");
    instantMessage2.addInstantMessage(instantMessage1);
    assertTrue(instantMessage1.getReplyTo().isPresent());
    assertTrue(instantMessage1.getReplyTo().get().equals(instantMessage2));
    assertTrue(instantMessage2.containsInstantMessage(instantMessage1));
  }
  
  @Test
  public void testIM4() {
    InstantMessage instantMessage = createIM("a");
    assertTrue(!instantMessage.getReplyTo().isPresent());
    assertTrue(instantMessage.sizeInstantMessages() == 0);
    assertTrue(!instantMessage.iteratorInstantMessages().hasNext());
  }
  
  @Test
  public void testIM5() {
    InstantMessage instantMessage = createIM("a");
    instantMessage.setReplyTo(Optional.ofNullable(instantMessage));
    instantMessage.setReplyTo(Optional.empty());
    assertTrue(!instantMessage.containsInstantMessage(instantMessage));
  }
  
  @Test
  public void testRelation1() {
    Date requestedTime = new Date();
    Relationship myFirstRelation = createRelation(requestedTime);
    myFirstRelation.setRelationType(RelationType.FOLLOWER);
    myFirstRelation.setRelationType(RelationType.FAMILY);
    assertTrue("Setting the RelationType does not work",
        myFirstRelation.getRelationType().equals(RelationType.FAMILY));
  }
  
  @Test
  public void testRelation2() {
    Date requestedTime = new Date();
    Relationship myFirstRelation = createRelation(requestedTime);
    myFirstRelation.setRelationType(RelationType.OTHER);
    assertTrue("Setting the rawRelationType does not work", myFirstRelation.getRelationType()
        .equals(RelationType.OTHER));
  }
  
  @Test
  public void testRelation3() {
    Date requestedTime = new Date();
    Relationship myFirstRelation = createRelation(requestedTime);
    myFirstRelation.setRelationType(RelationType.FOLLOWER);
    myFirstRelation.setRelationType(RelationType.OTHER);
    assertTrue("Setting the RelationType does not work",
        !myFirstRelation.getRelationType().equals(RelationType.FOLLOWER));
  }
  
  @Test
  public void testPersonOrganizer1() {
    Person student = createPerson(52074);
    Group f = createGroup("none");
    f.addOrganizer("org", student);
    assertTrue("Group organizer does not match",
        f.containsKeyOrganizer("org"));
  }
  
  @Test
  public void testPersonOrganizer3() {
    Person student = createPerson(52074);
    Group f = createGroup("none");
    f.addOrganizer("org", student);
    f.addOrganizer("at", student);
    assertTrue("Group organizer does not match",
        f.containsKeyOrganizer("org"));
    assertTrue("Group organizer does not match",
        f.containsKeyOrganizer("at"));
  }
  
  @Test
  public void testPersonOrganizer4() {
    Person student = createPerson(52074);
    Person student2 = createPerson(13289);
    Group f = createGroup("none");
    f.addOrganizer("org", student);
    f.addOrganizer("org", student2);
    assertTrue("Group organizer does not change",
        f.containsKeyOrganizer("org"));
  }
  
  @Test
  public void testPersonOrganizer5() {
    Group f = createGroup("none");
    try {
      f.addOrganizer("org", null);
      Assert.fail("added a null value");
    }
    catch (DataConsistencyViolationException e) {
    }
    assertTrue(!f.containsKeyOrganizer("org"));
  }
  
  @Test
  public void testPhotoTag2() {
    Photo p1 = PhotoBuilder.get().setWidth(18).setHeight(120.).build();
    Tag tag = TagBuilder.get().setConfirmed(true).setTagged(createPerson(52074)).setPhoto(p1)
        .build();
    p1.addTag(tag);
    p1.addTag(createTag(false));
    assertTrue(tag.getPhoto().equals(p1));
  }
  
  
  
  @Test
  public void testPhotoTag3() {
    Photo p1 = PhotoBuilder.get().setWidth(18).setHeight(120.).build();
    Tag tag = TagBuilder.get().setConfirmed(true).setTagged(createPerson(52074)).setPhoto(p1)
        .build();
    tag.setPhoto(p1);
    assertTrue(tag.getPhoto().equals(p1));
    assertTrue(p1.sizeTags() == 1);
    assertTrue(!p1.isEmptyTags());
    p1.addTag(tag);
    assertTrue(tag.getPhoto().equals(p1));
    assertTrue(p1.sizeTags() == 1);
    assertTrue(!p1.isEmptyTags());
  }
  
  @Test
  public void testPhotoTag5() {
    Photo p1 = PhotoBuilder.get().setWidth(100).setHeight(100).build();
    p1.addTag(createTag(false));
    p1.addTag(createTag(false));
    p1.addTag(createTag(true));
    assertTrue(p1.sizeTags() == 2);
    try {
      p1.clearTags();
      Assert.fail();
    }
    catch (DataConsistencyViolationException e) {
    }
    assertTrue(p1.sizeTags() == 2);
  }
  
  @Test
  public void testPhotoTag6() {
    Photo p1 = PhotoBuilder.get().setWidth(110).setHeight(110).build();
    Tag tag = createTag(true);
    ArrayList<Tag> list = new ArrayList<>();
    list.add(tag);
    p1.addAllTags(list);
    assertTrue(p1.containsTag(tag));
    assertTrue(p1.sizeTags() == 1);
  }
  
  @Test
  public void testPhotoPhotoMessages1() {
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    PhotoMessage message = createPhotoMessage("fixed it", p1);
    message.addPicture(p1);
    assertTrue(message.iteratorPictures().next().equals(p1));
    assertTrue(message.containsPicture(p1));
    assertTrue(message.sizePictures() == 1);
    ArrayList<Photo> photoList = new ArrayList<>();
    photoList.add(p1);
    assertTrue(message.containsAllPictures(photoList));
    photoList.add(PhotoBuilder.get().setWidth(230).setHeight(280.).build());
    assertTrue(!message.containsAllPictures(photoList));
  }
  
  @Test
  public void testPhotoPhotoMessages2() {
    
    Photo p = createPhoto(110);
    PhotoMessage message = createPhotoMessage("fixed it", p);
    ArrayList<Photo> Photos = new ArrayList<>();
    Photos.add(createPhoto(130));
    Photo p1 = createPhoto(120);
    Photos.add(p1);
    message.addAllPictures(Photos);
    message.addAllPictures(Photos);
    assertTrue("adding multiple Photos does not work", message.sizePictures()
        == 3);
  }
  
  @Test
  public void testPhotoPhotoMessages4() {
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    PhotoMessage message = ((PhotoMessageBuilder)PhotoMessageBuilder.get().setTimestamp(new
        Date()).setContent("fixed it")
        .setPicture(Sets.newHashSet(p1))
        .setSent(createPerson(39337)))
        .build();
    try {
      message.removePicture(p1);
      Assert.fail("removePictures does not fail");
    }
    catch (DataConsistencyViolationException e) {
    }
    message.addPicture(p1);
    assertTrue("number of pictures does not match", message.sizePictures() ==
        1);
    message.addPicture(p1);
    assertTrue("number of pictures does not match", message.sizePictures() ==
        1);
    try {
      message.removePicture(p1);
      Assert.fail("removePictures does not fail");
    }
    catch (DataConsistencyViolationException e) {
    }
  }
  
  @Test
  public void testPhotoPhotoMessages5() {
    Photo p1 = PhotoBuilder.get().setWidth(180).setHeight(120.).build();
    PhotoMessage message = ((PhotoMessageBuilder)PhotoMessageBuilder.get().setTimestamp(new
        Date()).setContent("fixed it")
        .setPicture(Sets.newHashSet(p1))
        .setSent(createPerson(39337)))
        .build();
    p1.addPhotoMessage(message);
    assertTrue("number of pictures does not match", message.sizePictures() ==
        1);
    assertTrue("number of pictures does not match", p1.sizePhotoMessages() ==
        1);
    // message.addPictures(p1);//TODO??
    try {
      p1.clearPhotoMessages();
      Assert.fail("removePictures does not fail");
    }
    catch (DataConsistencyViolationException e) {
    }
    assertTrue(p1.sizePhotoMessages() == 1);
  }
  
  @Test
  public void testPersonTag1() {
    Person student = createPerson(0);
    Tag t1 = createTag(true);
    assertTrue("Number of tagged Friends does not match", student.sizeTags() ==
        0);
    assertTrue(!student.containsTag(t1));
    student.addTag(t1);
    assertTrue("Tagged person does not match", t1.getTagged().equals(student));
    assertTrue("Number of tagged Friends does not match", student.sizeTags() ==
        1);
    assertTrue(student.containsTag(t1));
  }
  
  @Test
  public void testPersonTag2() {
    Person student = createPerson(0);
    Tag t1 = createTag(true);
    assertTrue("Number of tagged Friends does not match", student.sizeTags() ==
        0);
    assertTrue(!student.containsTag(t1));
    t1.setTagged(student);
    assertTrue("Tagged person does not match", t1.getTagged().equals(student));
    assertTrue("Number of tagged Friends does not match", student.sizeTags() ==
        1);
    assertTrue(student.containsTag(t1));
  }
  
  @Test
  public void testPersonTag3() {
    Person student = createPerson(0);
    Tag t1 = createTag(true);
    student.addTag(t1);
    assertTrue("IteratorTag does not return the right tag",
        student.iteratorTags().next()
            .equals(t1));
  }
  
  @Test
  public void testPersonTag4() {
    Person student = createPerson(0);
    Tag t1 = createTag(true);
    student.addTag(t1);
    student.addTag(createTag(true));
    try {
      student.clearTags();
      Assert.fail("number of pictures does not match");
    }
    catch (DataConsistencyViolationException e) {
    }
    try {
      student.removeTag(t1);
      Assert.fail("number of pictures does not match");
    }
    catch (DataConsistencyViolationException e) {
    }
  }
  
  @Test
  public void testMember1() {
    Group group = createGroup("none");
    Person p1 = createPerson(120);
    assertTrue(p1.sizeMembers() == 0);
    assertTrue(p1.isEmptyMembers());
    p1.addMember(group);
    assertTrue(p1.containsMember(group));
    assertTrue(p1.sizeMembers() == 1);
  }
  
  @Test
  public void testMember2() {
    Group group1 = createGroup("none");
    Group group2 = createGroup("one");
    Person p1 = createPerson(120);
    Person p2 = createPerson(121);
    ArrayList<Group> listGroup = new ArrayList<>();
    ArrayList<Person> listPerson = new ArrayList<>();
    listPerson.add(p1);
    listPerson.add(p2);
    listGroup.add(group1);
    listGroup.add(group2);
    p1.addAllMembers(listGroup);
    p2.addAllMembers(listGroup);
    assertTrue(group1.containsAllMembers(listPerson));
    assertTrue(group2.containsAllMembers(listPerson));
  }
  
  @Test
  public void testMember3() {
    Group group1 = createGroup("none");
    Person p1 = createPerson(120);
    p1.addMember(group1);
    p1.addMember(group1);
    assertTrue("Added a value twice", p1.sizeMembers() == 1);
    try {
      p1.addMember(null);
      Assert.fail("Added a null value");
    }
    catch (DataConsistencyViolationException e) {
    }
    assertTrue("Added a null value", p1.sizeMembers() == 1);
    p1.clearMembers();
    assertTrue(p1.sizeMembers() == 0);
    
  }
  
  @Test
  public void testMember4() {
    Group group1 = createGroup("none");
    Group group2 = createGroup("one");
    Person p1 = createPerson(120);
    assertTrue(p1.isEmptyMembers());
    p1.addMember(group1);
    p1.addMember(group2);
    assertTrue(p1.sizeMembers() == 2);
    assertTrue(group1.containsMember(p1));
    p1.removeMember(group1);
    assertTrue(!group1.containsMember(p1));
    assertTrue(!p1.containsMember(group1));
    group2.removeMember(p1);
    assertTrue(!p1.containsMember(group2));
    assertTrue(!group2.containsMember(p1));
  }
  
  @Test
  public void testMember5() {
    Group group1 = createGroup("none");
    Person p1 = createPerson(120);
    p1.addMember(group1);
    p1.removeMember(group1);
    group1.removeMember(p1);
  }
  
  @Test
  public void testSent1() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    group1.addSent(o);
    assertTrue(group1.indexOfSent(o) == 0);
    assertTrue(group1.lastIndexOfSent(o) == 0);
    group1.getSent(0).equals(o);
    group1.containsSent(o);
    assertTrue(group1.sizeSents() == 1);
    assertTrue(o.getSent().equals(group1));
    o.setSent(group1);
  }
  
  @Test
  public void testSent2() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    group1.addAllSents(Lists.newArrayList(o));
    group1.getSent(0).equals(o);
    assertTrue(group1.sizeSents() == 1);
    assertTrue(o.getSent().equals(group1));
  }
  
  @Test
  public void testSent3() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.setSent(group1);
    group1.getSent(0).equals(o);
    assertTrue(group1.sizeSents() == 1);
    assertTrue(o.getSent().equals(group1));
  }
  
  @Test
  public void testSent4() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.setSent(group1);
    try {
      group1.clearSents();
      Assert.fail();
    }
    catch (DataConsistencyViolationException e) {
    }
  }
  
  @Test
  public void testSent5() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.setSent(group1);
    o.setSent(group1);
    assertTrue(group1.sizeSents() == 1);
    group1.iteratorSents().next().equals(o);
    createIM("test").setSent(group1);
    assertTrue(((InstantMessage) group1.getSent(1)).getContent().equals("test"));
  }
  
  @Test
  public void testSent6() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.setSent(group1);
    o.setSent(group1);
    assertTrue(group1.sizeSents() == 1);
    group1.iteratorSents().next().equals(o);
    createIM("test").setSent(group1);
    assertTrue(((InstantMessage) group1.getSent(1)).getContent().equals("test"));
  }
  
  @Test
  public void testSent7() {
    Group group1 = createGroup("none");
    Post post1 = createIM("one");
    Post post2 = createIM("two");
    group1.addAllSents(Lists.newArrayList(post1, post2));
    group1.containsAllSents(Lists.newArrayList(post1, post2));
    assertTrue(group1.sizeSents() == 2);
    assertTrue(post1.getSent().equals(group1));
    assertTrue(post2.getSent().equals(group1));
  }
  
  @Test
  public void testSent8() {
    Group group1 = createGroup("Facebooker");
    Group group2 = createGroup("Snapchatter");
    Post post1 = createIM("one");
    Post post2 = createIM("two");
    group1.addAllSents(Lists.newArrayList(post1, post2));
    group2.addAllSents(Lists.newArrayList(post1, post2));
    assertTrue(group1.sizeSents() == 0);
    assertTrue(post1.getSent().equals(group2));
    assertTrue(post2.getSent().equals(group2));
    Post post3 = createIM("three");
    post3.setSent(group2);
    Iterator<Post> i = group2.iteratorSents();
    assertTrue(i.next().equals(post1));//RIGHT?
    assertTrue(i.next().equals(post2));
    assertTrue(i.next().equals(post3));
  }
  
  @Test
  public void testSent9() {
    Group group1 = createGroup("Facebooker");
    Group group2 = createGroup("Snapchatter");
    Post post1 = createIM("one");
    Post post2 = createIM("two");
    group1.addAllSents(Lists.newArrayList(post1, post2));
    group2.addAllSents(Lists.newArrayList(post1, post2));
    try {
      group2.removeAllSents(Lists.newArrayList(post1, post2));
      Assert.fail();
    }
    catch (DataConsistencyViolationException e) {
    }
  }
  
  @Test
  public void testReceived1() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    group1.addReceived(o);
    assertTrue(group1.indexOfReceived(o) == 0);
    assertTrue(group1.lastIndexOfReceived(o) == 0);
    group1.getReceived(0).equals(o);
    group1.containsReceived(o);
    assertTrue(group1.sizeReceiveds() == 1);
    assertTrue(o.containsReceived(group1));
    group1.addReceived(o, 0);
  }
  
  @Test
  public void testReceived2() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    group1.addAllReceiveds(Lists.newArrayList(o));
    group1.getReceived(0).equals(o);
    assertTrue(group1.sizeReceiveds() == 1);
    assertTrue(o.containsReceived(group1));
  }
  
  @Test
  public void testReceived3() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.addReceived(group1);
    group1.getReceived(0).equals(o);
    assertTrue(group1.sizeReceiveds() == 1);
    assertTrue(o.containsReceived(group1));
  }
  
  @Test
  public void testReceived4() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.addReceived(group1);
    group1.clearReceiveds();
    assertTrue(o.sizeReceiveds() == 0);
    assertTrue(group1.sizeReceiveds() == 0);
  }
  
  @Test
  public void testReceived5() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.addReceived(group1);
    o.addReceived(group1);
    // assertTrue(group1.sizeReceiveds() == 1);
    o.removeReceived(group1);
    assertTrue((o.containsReceived(group1) == group1.containsReceived(o)));
  }
  
  @Test
  public void testReceived6() {
    Group group1 = createGroup("none");
    Post o = createIM("o");
    o.addReceived(group1);
    o.addReceived(group1);
    group1.iteratorReceiveds().next().equals(o);
    group1.addReceived(createIM("test"), 0);
    assertTrue(((InstantMessage) group1.getReceived(0)).getContent().equals("test"));
  }
  
  @Test
  public void testReceived7() {
    Group group1 = createGroup("none");
    Post post1 = createIM("one");
    Post post2 = createIM("two");
    group1.addAllReceiveds(Lists.newArrayList(post1, post2));
    group1.containsAllReceiveds(Lists.newArrayList(post1, post2));
    assertTrue(group1.sizeReceiveds() == 2);
    assertTrue(post1.containsReceived(group1));
    assertTrue(post2.containsReceived(group1));
  }
  
  @Test
  public void testReceived8() {
    Group group1 = createGroup("Facebooker");
    Group group2 = createGroup("Snapchatter");
    Post post1 = createIM("one");
    Post post2 = createIM("two");
    group1.addAllReceiveds(Lists.newArrayList(post1, post2));
    group2.addAllReceiveds(Lists.newArrayList(post1, post2));
    assertTrue(group1.sizeReceiveds() == 2);
    assertTrue(post1.containsReceived(group2));
    assertTrue(post2.containsReceived(group2));
    Post post3 = createIM("three");
    group2.addReceived(post3, 0);
    Iterator<Post> i = group2.iteratorReceiveds();
    assertTrue(i.next().equals(post3));//RIGHT?
    assertTrue(i.next().equals(post1));
    assertTrue(i.next().equals(post2));
  }
  
  @Test
  public void testReceived9() {
    Group group1 = createGroup("Facebooker");
    Group group2 = createGroup("Snapchatter");
    Post post1 = createIM("one");
    Post post2 = createIM("two");
    group1.addAllReceiveds(Lists.newArrayList(post1, post2));
    group2.addAllReceiveds(Lists.newArrayList(post1, post2));
    group2.removeAllReceiveds(Lists.newArrayList(post1, post2));
    assertTrue(post1.sizeReceiveds() == 1);
  }
  
  @Test
  public void testReceived10() {
    Group group1 = createGroup("Facebooker");
    Group group2 = createGroup("Snapchatter");
    Post post1 = createIM("one");
    post1.addAllReceiveds(Lists.newArrayList(group1, group2));
    post1.iteratorReceiveds().next().equals(group1);
    post1.addReceived(createGroup("test"));
    assertTrue(post1.containsAllReceiveds(Lists.newArrayList(group1, group2)));
    assertTrue(post1.containsReceived(group1));
    assertTrue(group1.containsReceived(post1));
  }
}
