package com.infinite.share.net.http.admin;

/********************************************************************************************************************************
 * Helper class for software management pages.
 ********************************************************************************************************************************/
public final class SourceManagementPages
{
   private static final String PAGE_ADMIN       = "https://github.com";
   private static       String EXT_GIT_USERNAME = "username";
   private static       String EXT_GIT_PROJECT  = "project";

   /**
    * Ensure that this class will never be instantiated / extended. 
    */
   private SourceManagementPages()
   {
   }

   /**
    * Set the name of the GitHub user owning the project.
    * @param url The name of the GitHub user owning the project.
    */
   public static void setGitHubUserName(final String userName)
   {
      EXT_GIT_USERNAME = userName;
   }
   
   /**
    * Set the GitHub project name;
    * @param project
    */
   public static void setGitHubProject(final String project)
   {
      EXT_GIT_PROJECT = project;
   }

   /**
    * Return the URL to the GitHub's project page.
    * @return The URL to the GitHub's project page.
    */
   public static String homePage()
   {
      return PAGE_ADMIN + "/" + EXT_GIT_USERNAME + "/" + EXT_GIT_PROJECT;
   }
}
