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
               <div class="title">Invite a Friend</div>
               <div class="content">
                  <form  action="/auth/invite" method="post" autocomplete="off">
                     <table>
                        <tr>
                           <td>Confirm password</td>
                           <td><input type="password" name="<%=AuthPages.INPUT_PASSWORD%>" size=20 autofocus/></td>
                           <td>Type in your own password for confirmation.</td>
                        </tr>
                        <tr>
                           <td>Friend's Email Address</td>
                           <td><input name="<%=AuthPages.INPUT_EMAIL%>" size=20/></td>
                           <td><input type="submit" value="Send Invitation Email"/></td>
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