<%@ page import="com.infinite.share.auth.UserSession" %>
<%@ page import="com.infinite.share.net.http.auth.ChangePasswordServlet" %>
<%@ page import="com.infinite.share.net.http.auth.AuthPages" %>

<%@include file="/include/verify_signin.jspf"%>

<%
   User user = UserSession.getUser(session);
  String userId = (user != null) ? (user.getImmutableUserId()) : ("");
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
               <div class="title">Change Password</div>
               <div class="content">
                  <form  action="/auth/changepassword" method="post" autocomplete="off">
                     <input type="hidden" name="<%=AuthPages.INPUT_USERID%>" value="<%=userId%>"/>
                     <table>
                        <tr>
                           <td>Old Password</td>
                           <td><input type="password" name="<%=AuthPages.INPUT_PASSWORD%>" size=20 autofocus/></td>
                           <td></td>
                        </tr>
                        <tr>
                           <td>New Password</td>
                           <td><input type="password" name="<%=AuthPages.INPUT_PASSWORD1%>" size=20/></td>
                           <td><i>Password must be at least 8 characters.</i></td>
                        </tr>
                        <tr>
                           <td>Verify New Password</td>
                           <td><input type="password" name="<%=AuthPages.INPUT_PASSWORD2%>" size=20/></td>
                           <td><input type="submit" value="Change Password"/></td>
                        </tr>
                     </table>
                  </form>
               </div>
            </div>
         </div>
      </div>
      <%@include file="/include/pagefooter.jspf"%>
   </div>
</body>
</html>