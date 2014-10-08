<%@ page import="com.infinite.onepass.net.http.sites.SitesPages" %>
<%@ page import="com.infinite.onepass.sites.Site" %>
<%@ page import="com.infinite.onepass.sites.SiteFilter" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.SortedSet" %>

<%@include file="/include/verify_signin.jspf"%>
<%
   User owner = ((request.getSession(false) == null) || (UserSession.getUser(session) == null)) ? (null) : (UserSession.getUser(session));
   SortedSet<Site> sites = Site.restoreAll(owner);

   Collection<Site> filteredSites = SiteFilter.filter(sites, request.getParameter(SitesPages.INPUT_FILTER), owner);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-tdansitional.dtr">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <%@include file="/include/htmlheader.jspf"%>
   <link rel="stylesheet" type="text/css" href="/css/onepass.css" />
</head>
<body>
   <div id="page">
      <%@include file="/include/pageheader.jspf"%>
      <div id="content_wrapper">
         <div id="content">
            <div class="box" style="height: 100%">
               <%@include file="/include/message_handler.jspf"%>
               <div class="title">
                  Sites
                  <div class="menu"><img src="/img/action/add_12.png"/>&nbsp;<a href="<%=SitesPages.createSite()%>">New Site</a></div>
               </div>
               <div class="content">
                  <div class="list_table">
                     <div class="title_row">
                        <div class="cell">Site</div>
                     </div>
<%                for (Site site: filteredSites)
                  {
%>                   <div class="row">
                        <div class="cell"><a href="<%=SitesPages.editSite(site)%>"><%=site.getName(owner)%></a></div>
                     </div>
<%                }
%>               </div>
               </div>
            </div>
         </div>
      </div>
      <%@include file="/include/pagefooter.jspf"%>
   </div>
</body>
</html>