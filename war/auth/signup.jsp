<%@ page import="com.infinite.share.net.http.auth.SignUpServlet" %>
<%@ page import="com.infinite.share.net.http.auth.AuthPages" %>
<%@ page import="com.infinite.share.auth.UserSession" %>

<%@include file="/include/signout.jspf"%>

<%
   String inviterId = request.getParameter(AuthPages.INPUT_INVITER_ID);
   if (inviterId == null)
      inviterId = "";

   String userName = request.getParameter(AuthPages.INPUT_USERID);
   if (userName == null)
      userName = "";

   String displayName = request.getParameter(AuthPages.INPUT_DISPLAY_NAME);
   if (displayName == null)
      displayName = "";
   
   String email = request.getParameter(AuthPages.INPUT_EMAIL);
   if (email == null)
      email = "";
   
   String verificationCode = request.getParameter(AuthPages.INPUT_INVITATION_KEY);
   if (verificationCode == null)
      verificationCode = "";
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
               <div class="title">Register</div>
               <form  action="/auth/signup" method="post" autocomplete="off">
                  <div class="content">
                     <table width=100%>
                        <tr>
                           <td width=120px>Verification Code</td>
                           <td colspan=2><input type="text" name="<%=AuthPages.INPUT_INVITATION_KEY%>" value="<%=verificationCode%>" size=96 readonly/></td>
                        </tr>
                        <tr>
                           <td>Inviter's Id</td>
                           <td width=160px><input type="text" name="<%=AuthPages.INPUT_INVITER_ID%>" value="<%=inviterId%>" size=20 readonly/></td>
                           <td><i>User id of the person who sent you the invitation.</i></td>
                        </tr>
                     </table>
                  </div>
                  <br/>
                  <div class="content">
                     <table width=100%>
                        <tr>
                           <td width=120px>User Id</td>
                           <td width=160px><input type="text" name="<%=AuthPages.INPUT_USERID%>" value="<%=userName%>" size=20 autofocus/></td>
                           <td><i>User name must be at least 4 characters.</i></td>
                        </tr>
                        <tr>
                           <td>Password</td>
                           <td><input type="password" name="<%=AuthPages.INPUT_PASSWORD1%>" size=20/></td>
                           <td><i>Password must be at least 8 characters.</i></td>
                        </tr>
                        <tr>
                           <td>Verify Password</td>
                           <td><input type="password" name="<%=AuthPages.INPUT_PASSWORD2%>" size=20/></td>
                           <td><i>Confirm password.</i></td>
                        </tr>
                        <tr>
                           <td>Display Name</td>
                           <td><input type="text" name="<%=AuthPages.INPUT_DISPLAY_NAME%>" value="<%=displayName%>" size=20/></td>
                           <td><i>Display Name.</i></td>
                        </tr>
                        <tr>
                           <td>Email Address</td>
                           <td><input type="text" name="<%=AuthPages.INPUT_EMAIL%>" value="<%=email%>" size=20/></td>
                           <td><i>Email address.</i></td>
                        </tr>
                     </table>
                  </div>
                  <div class="inner_box" style="text-align: center">
                     <input type="submit" value="Register"/>
                  </div>
               </form>
            </div>
         </div>
      </div>
      <%@include file="/include/pagefooter.jspf"%>
   </div>
</body>
</html>