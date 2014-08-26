<%@ page import="com.infinite.share.net.http.admin.AdminPages" %>
<%@include file="/include/verify_signin.jspf"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-tdansitional.dtr">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <%@include file="/include/htmlheader.jspf"%>
</head>
<body>
   <div id="page">
      <%@include file="/include/pageheader.jspf"%>
      <div id="content_wrapper">
         <div id="content">
            <div class="box">
               <div class="title">Administration Console</div>
               <div class="inner_box">
                  <p><img src="/img/logo/appengine_12.png"/>&nbsp;<a href="<%=AdminPages.extHostAdminConsole()%>" target="_blank">Open AppEngine Console</a></p>
                  <p><img src="/img/logo/appengine_12.png"/>&nbsp;<a href="<%=AdminPages.extDatastoreViewer()%>" target="_blank">Open Datastore Viewer</a></p>
                  <p><img src="/img/ico/user_12.png"/>&nbsp;<a href="<%=AdminPages.extHostViewExpiredSessions()%>" target="_blank">View expired sessions</a></p>
                  <p><img src="/img/action/delete_12.png"/>&nbsp;<a href="<%=AdminPages.extHostClearUpExpiredSessions()%>" target="_blank">Clean up expired sessions</a></p>
                  <p><img src="/img/action/delete_12.png"/>&nbsp;<a href="/admin/invcleanup">Clean up expired invitations</a></p>
               </div>
            </div>
         </div>
      </div>
      <%@include file="/include/pagefooter.jspf"%>
   </div>
</body>
</html>