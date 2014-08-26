<%@ page import="com.infinite.share.net.http.auth.SignInServlet" %>
<%@ page import="com.infinite.share.net.http.auth.AuthPages" %>
<%@ page import="com.infinite.share.auth.UserSession" %>
<%@ page import="com.infinite.share.security.hash.HashFunctions" %>

<%@include file="/include/signout.jspf"%>

<%
   String userId = request.getParameter(AuthPages.INPUT_USERID);
   if (userId == null)
      userId = "";
%>

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
               <div class="title">Sign In</div>
               <div class="content">
                  <form action="/auth/signin" method="post">
	                  <table>
	                     <tr>
	                        <td>User Id</td>
	                        <td><input type="text" name="<%=AuthPages.INPUT_USERID%>" value="<%=userId%>" size=20 <%=(userId.isEmpty())?("autofocus"):("")%>/></td>
	                        <td></td>
	                     </tr>
	                     <tr>
	                        <td>Password</td>
	                        <td><input type="password" name="<%=AuthPages.INPUT_PASSWORD%>" size=20 <%=(!userId.isEmpty())?("autofocus"):("")%>/></td>
	                        <td><input type="submit" value="Sign In"/></td>
	                     </tr>
	                  </table>
                  </form>
               </div>
               <div class="inner_box">If you do not have an Infinity account yet, click <a href="<%=AuthPages.signUp()%>">here</a> to register.</div>
            </div>
         </div>
      </div>
      <%@include file="/include/pagefooter.jspf"%>
   </div>
</body>
</html>