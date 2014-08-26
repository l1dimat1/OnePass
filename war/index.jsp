<%@page import="com.infinite.share.net.http.HomePages" %>
<%@include file="/include/verify_signin.jspf"%>

<% if (UserSession.isSignedIn(session))
   {
%>    <jsp:forward page="<%=HomePages.welcome()%>" />
<% }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-tdansitional.dtr">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <%@include file="/include/htmlheader.jspf"%>
</head>
<body>
</body>
</html>