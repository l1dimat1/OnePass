<%@ page import="com.infinite.share.net.http.auth.InviteServlet" %>
<%@ page import="com.infinite.share.net.http.auth.AuthPages" %>

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
               <%@include file="/include/message_handler.jspf"%>
            </div>
         </div>
      </div>
      <%@include file="/include/pagefooter.jspf"%>
   </div>
</body>
</html>