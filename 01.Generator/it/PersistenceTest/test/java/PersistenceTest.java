/* (c) https://github.com/MontiCore/monticore */

package persistencetest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.common.collect.Lists;

import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import junit.framework.TestCase;
import dex.data.DataConsistencyViolationException;
import dex.data.NotImplementedException;
import persistencetest.socnet.*;
import static org.junit.Assert.*;

public class PersistenceTest  extends TestCase {
  private FrameFixture demo;
  
  @Before
  public void setUp() {
    /*Main.main(new String[0]);
    
    // log in to dex
    Robot robot = BasicRobot.robotWithCurrentAwtHierarchy();
    DialogFixture login = WindowFinder.findDialog("LoginDialog").using(robot);
    
    login.textBox("username").enterText("a");
    login.textBox("password").enterText("a");
    login.button("login").click();
    
    // find main window
    demo = WindowFinder.findFrame("MainWindow").using(robot);
    // getMainFrameByTitle("The SocNet System")).using(robot);*/
  }
  
  @Test
  public void testAddPhoto() {
    System.out.println("TODO");
    /*for (int i = 1; i <= 9; i++) {
      demo.tree().node(7).click();
      demo.tabbedPane().click();
      demo.tabbedPane().selectTab(1);
      demo.button(getButtonByName("add")).click();
      demo.textBox("height").enterText("" + i);
      demo.button(getButtonByName("Ok")).click();
      //demo.tree().node(i).click();
    }*/
  }
  
  @After
  public void tearDown() {
    /*demo.close();
    demo.cleanUp();*/
  }
  
  private static GenericTypeMatcher<javax.swing.JButton> getButtonByName(
      final String title) {
    GenericTypeMatcher<javax.swing.JButton> textMatcher = new GenericTypeMatcher<javax.swing.JButton>(
        javax.swing.JButton.class) {
      protected boolean isMatching(javax.swing.JButton frameName) {
        if (frameName.getText() != null) {
          return (title.replace(" ", "")).equals(frameName.getText()
              .replace(" ", ""));
        }
        return false;
      }
      
    };
    return textMatcher;
  }
}
