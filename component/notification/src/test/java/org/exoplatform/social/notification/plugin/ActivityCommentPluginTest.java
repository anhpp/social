/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.notification.plugin;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.MessageInfo;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.model.NotificationKey;
import org.exoplatform.commons.api.notification.plugin.AbstractNotificationPlugin;
import org.exoplatform.commons.notification.impl.NotificationContextImpl;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.notification.AbstractPluginTest;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 20, 2013  
 */
public class ActivityCommentPluginTest extends AbstractPluginTest {
  private final static String ACTIVITY_TITLE = "my activity's title post today.";
  private final static String COMMENT_TITLE = "my comment's title add today.";
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  @Override
  public AbstractNotificationPlugin getPlugin() {
    return pluginService.getPlugin(NotificationKey.key(ActivityCommentPlugin.ID));
  }
  
  /**
   * Just test for simple case when you post the simple activity
   * @throws Exception
   */
  public void testSimpleCase() throws Exception {
    //STEP 1 post activity
    ExoSocialActivity activity = makeActivity(maryIdentity, ACTIVITY_TITLE);
    assertMadeNotifications(1);
    notificationService.clearAll();
    //STEP 2 add comment
    makeComment(activity, demoIdentity, COMMENT_TITLE);
    //assert equals = 2 because root is stream owner, and mary is activity's poster
    //then when add commnent need to notify to root and mary
    List<NotificationInfo> list = assertMadeNotifications(2);
    NotificationInfo commentNotification = list.get(0);
    //STEP 3 assert Message info
    
    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    ctx.setNotificationInfo(commentNotification.setTo(demoIdentity.getRemoteId()));
    MessageInfo info = getPlugin().buildMessage(ctx);
    
    assertSubject(info, demoIdentity.getProfile().getFullName() + " commented one of your activities");
    assertBody(info, "New comment on your activity");
  }
  
  /**
   * Just test for simple case when you post the simple activity
   * @throws Exception
   */
  public void testSimpleCaseTwoComments() throws Exception {
    //STEP 1 post activity
    ExoSocialActivity activity = makeActivity(maryIdentity, ACTIVITY_TITLE);
    assertMadeNotifications(1);
    notificationService.clearAll();
    //STEP 2 add comment
    makeComment(activity, maryIdentity, COMMENT_TITLE);
    //assert equals = 2 because root is stream owner, and mary is activity's poster
    //then when add commnent need to notify to root and mary
    List<NotificationInfo> list = assertMadeNotifications(1);
    NotificationInfo commentNotification = list.get(0);
    //STEP 3 assert Message info
    
    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    ctx.setNotificationInfo(commentNotification.setTo(maryIdentity.getRemoteId()));
    MessageInfo info = getPlugin().buildMessage(ctx);
    
    assertSubject(info, maryIdentity.getProfile().getFullName() + " commented one of your activities");
    assertBody(info, "New comment on your activity");
  }
  
  
  public void testPluginOFF() throws Exception {
    //STEP 1 post activity
    ExoSocialActivity activity = makeActivity(maryIdentity, ACTIVITY_TITLE);
    assertMadeNotifications(1);
    notificationService.clearAll();
    //STEP 2 add comment
    makeComment(activity, demoIdentity, COMMENT_TITLE);
    //assert equals = 2 because root is stream owner, and mary is activity's poster
    //then when add commnent need to notify to root and mary
    List<NotificationInfo> list = assertMadeNotifications(2);
    NotificationInfo commentNotification = list.get(0);
    //STEP 3 assert Message info
    
    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    ctx.setNotificationInfo(commentNotification.setTo(demoIdentity.getRemoteId()));
    MessageInfo info = getPlugin().buildMessage(ctx);
    
    assertSubject(info, demoIdentity.getProfile().getFullName() + " commented one of your activities");
    assertBody(info, "New comment on your activity");
    
    notificationService.clearAll();
    
    //OFF Plugin
    turnOFF(getPlugin());
    
    makeComment(activity, demoIdentity, COMMENT_TITLE);
    assertMadeNotifications(0);
    
    turnON(getPlugin());
  }
  
  public void testPluginON() throws Exception {
    //STEP 1 OFF ActivityCommentPlugin
    turnOFF(getPlugin());
    ExoSocialActivity activity = makeActivity(maryIdentity, ACTIVITY_TITLE);
    assertMadeNotifications(1);
    notificationService.clearAll();
    
    //STEP 2 Add Comment
    makeComment(activity, demoIdentity, COMMENT_TITLE);
    assertMadeNotifications(0);

    //STEP 3 assert Message info
    turnON(getPlugin());
    makeComment(activity, demoIdentity, COMMENT_TITLE);
    //assert equals = 2 because root is stream owner, and mary is activity's poster
    //then when add commnent need to notify to root and mary
    List<NotificationInfo> list = assertMadeNotifications(2);
    NotificationInfo commentNotification = list.get(0);
    //STEP 3 assert Message info
    
    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    ctx.setNotificationInfo(commentNotification.setTo(demoIdentity.getRemoteId()));
    MessageInfo info = getPlugin().buildMessage(ctx);
    
    assertSubject(info, demoIdentity.getProfile().getFullName() + " commented one of your activities");
    assertBody(info, "New comment on your activity");
    
    notificationService.clearAll();
  }
  
  public void testFeatureONOFF() throws Exception {
    ExoSocialActivity maryActivity = makeActivity(maryIdentity, ACTIVITY_TITLE);
    //mary post activity on root stream ==> one notification
    assertMadeNotifications(1);
    notificationService.clearAll();
    makeComment(maryActivity, demoIdentity, COMMENT_TITLE);
    //assert equals = 2 because root is stream owner, and mary is activity's poster
    assertMadeNotifications(2);
    notificationService.clearAll();
    
    //turn off the feature ==> all plugins off
    turnFeatureOff();
    
    ExoSocialActivity newActivity = makeActivity(maryIdentity, ACTIVITY_TITLE);
    //postActivityPlugin off
    assertMadeNotifications(0);
    makeComment(newActivity, demoIdentity, COMMENT_TITLE);
    //commentActivityPlugin off
    assertMadeNotifications(0);
    
    //turn on the feature
    turnFeatureOn();
    
    ExoSocialActivity johnActivity = makeActivity(johnIdentity, ACTIVITY_TITLE);
    assertMadeNotifications(1);
    notificationService.clearAll();
    makeComment(johnActivity, demoIdentity, COMMENT_TITLE);
    assertMadeNotifications(2);
  }
  
  public void testDigest() throws Exception {
    //mary post activity on root stream ==> notify to root
    ExoSocialActivity maryActivity = makeActivity(maryIdentity, ACTIVITY_TITLE);
    assertMadeNotifications(1);
    notificationService.clearAll();
    
    List<NotificationInfo> toRoot = new ArrayList<NotificationInfo>();

    //demo add comment to maryActivity ==> notify to root and mary
    makeComment(maryActivity, demoIdentity, "demo add comment");
    List<NotificationInfo> list1 = assertMadeNotifications(2);
    toRoot.add(list1.get(1));
    notificationService.clearAll();
    
    //john add comment to maryActivity ==> notify to root, mary and demo
    makeComment(activityManager.getActivity(maryActivity.getId()), johnIdentity, "john add comment");
    List<NotificationInfo> list2 = assertMadeNotifications(3);
    toRoot.add(list2.get(2));
    notificationService.clearAll();
    
    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    toRoot.set(0, toRoot.get(0).setTo(rootIdentity.getRemoteId()));
    ctx.setNotificationInfos(toRoot);
    Writer writer = new StringWriter();
    getPlugin().buildDigest(ctx, writer);
    assertDigest(writer, "Demo gtn, John Anthony have commented on your activity: my activity's title post today.");
  }
}
