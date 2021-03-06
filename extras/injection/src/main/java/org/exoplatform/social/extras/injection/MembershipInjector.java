package org.exoplatform.social.extras.injection;

import java.util.HashMap;

import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.model.Space;

public class MembershipInjector extends AbstractSocialInjector {

  /** . */
  private static final String MEMBERSHIP_TYPE = "type";
  
  /** . */
  private static final String FROM_USER = "fromUser";

  /** . */
  private static final String TO_USER = "toUser";
  
  /** . */
  private static final String USER_PREFIX = "userPrefix";

  /** . */
  private static final String FROM_SPACE = "fromSpace";

  /** . */
  private static final String TO_SPACE = "toSpace";
  
  /** . */
  private static final String SPACE_PREFIX = "spacePrefix";

  /** . */
  private static final String PATTERN = "pattern";
  
  /** . */
  private String identitiesInfo = "";

  
  @Override
  public void inject(HashMap<String, String> params) throws Exception {
    
    //
    String type = params.get(MEMBERSHIP_TYPE);
    
    //
    int fromUser = param(params, FROM_USER);
    int toUser = param(params, TO_USER);
    String userPrefix = params.get(USER_PREFIX);
    //
    int fromSpace = param(params, FROM_SPACE);
    int toSpace = param(params, TO_SPACE);
    String spacePrefix = params.get(SPACE_PREFIX);
    String pattern = params.get(PATTERN);
    
    if (!"manager".equals(type) && !"member".equals(type)) {
      getLog().info("'" + type + "' is a wrong value for membership type parameter. Please set it to 'member' or 'manager'. Aborting injection ..." );
      return;
    }
    
    init(userPrefix, spacePrefix); // create userBase and spaceBase
   
    getLog().info("About to inject Space Members :");
    getLog().info("" + (toSpace - fromSpace + 1) + " space(s) has prefix '" + spacePrefix + " ' with " + (toUser - fromUser + 1) + " member(s) has prefix '" + userPrefix + "'");
      
    int floor = fromSpace;
    for (int i = floor; i <= toSpace; ++i) {
      generate(i, type, fromUser, toUser, pattern);
    }
    
    getLog().info("completed to inject Space Members");
  }
  
   
  
  private void generate(int spaceIdx, String type, int from, int to, String pattern) {
    
    String spacePrettyBaseName = spaceBase.replace(".", "");
    String userPrettyBase = userBase.replace(".", "");
    if (pattern == null) {
      
      String spaceName = spacePrettyBaseName + spaceIdx;
      Space space = spaceService.getSpaceByPrettyName(spaceName);
      
      if (space == null) {
        getLog().info("space with display name: " + spaceName + "is not existing");
        return;
      }
    
      getLog().info("added users " + identitiesInfo + " to " + type + " of '" + spaceName + "' space.");
    
      Identity identity = null;
    
      for(int i = from; i <= to; ++i) {

        String username = userPrettyBase + i;
        identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username, false);
        
        if ("member".endsWith(type)) {
          spaceService.addMember(space, identity.getRemoteId());
        } else if ("manager".endsWith(type)) {
          spaceService.setManager(space, identity.getRemoteId(), true);
        }
      
      }

    }
    else {
      //create space name with new pattern
      String spaceAppend = new StringBuilder().append(pattern).append(spaceIdx).toString();
      String spaceName = spacePrettyBase + spaceAppend.substring(spaceAppend.length() - pattern.length());
      //String spaceName = spaceName(pat,spaceIdx);
      Space space = spaceService.getSpaceByPrettyName(spaceName);
      
      if (space == null) {
        getLog().info("space with display name: " + spaceName + "is not existing");
        return;
      }
    
      getLog().info("added users " + identitiesInfo + " to " + type + " of '" + spaceName + "' space.");
    
      Identity identity = null;
    
      for(int i = from; i <= to; ++i) {

        //String username = userName(pat,i);
        //create user name with new pattern
        String nameAppend = new StringBuilder().append(pattern).append(i).toString();
        String username = userPrettyBase + nameAppend.substring(nameAppend.length() - pattern.length());
        
        identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username, false);
        
        if ("member".endsWith(type)) {
          spaceService.addMember(space, identity.getRemoteId());
        } else if ("manager".endsWith(type)) {
          spaceService.setManager(space, identity.getRemoteId(), true);
        }
      
      }
    }
  }
  
  
}
