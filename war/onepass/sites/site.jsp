<%@ page import="com.infinite.onepass.net.http.sites.SaveSiteServlet" %>
<%@ page import="com.infinite.onepass.net.http.sites.SitesPages" %>
<%@ page import="com.infinite.onepass.sites.Site" %>
<%@ page import="com.infinite.onepass.sites.TransientSite" %>
<%@ page import="com.infinite.share.auth.UserSession" %>

<%@include file="/include/verify_signin.jspf"%>

<%
   User owner = ((request.getSession(false) == null) || (UserSession.getUser(session) == null)) ? (null) : (UserSession.getUser(session));
   
   Object actionId = request.getParameter(SitesPages.INPUT_ACTION);
   boolean edit = false;
   if ((actionId != null) && (Integer.parseInt((String)actionId) == SitesPages.ACTION_EDIT))
      edit = true;
   
   Object siteIdO = request.getParameter(SitesPages.INPUT_SITE_ID);
   Site site = null;
   boolean newSite = false;
   if (siteIdO != null)
   {
      site = Site.restoreFromId(owner, (String)siteIdO);
   }
   else
   {
      site = new TransientSite(owner);
      newSite = true;
   }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-tdansitional.dtr">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <%@include file="/include/htmlheader.jspf"%>
   <link rel="stylesheet" type="text/css" href="/css/onepass.css" />
   <script language="javascript" type="text/javascript">
      function browseImage()
      {
         document.getElementById("imageFileBrowser").click();
      }
      
      function deleteImage()
      {
          var targetImg            = document.getElementById("siteImage");
          var targetImgStringInput = document.getElementById("<%=SitesPages.INPUT_IMAGE_STRING%>");

          targetImg.src = "<%=Site.defaultImage()%>";
          targetImgStringInput.value = targetImg.src;
      }

      function updateImageFromFile()
      {
         var filesSelected = document.getElementById("imageFileBrowser").files;
         if (filesSelected.length > 0)
         {
            var targetImg            = document.getElementById("siteImage");
            var targetImgStringInput = document.getElementById("<%=SitesPages.INPUT_IMAGE_STRING%>");
             
            var fileToLoad = filesSelected[0];
            var fileReader = new FileReader();
            fileReader.onload = function(fileLoadedEvent)
            {
               var imgBase64String = fileLoadedEvent.target.result; // <--- data: base64
               var imgSizeKB = Math.floor(0.5 + (imgBase64String.length - 814) / 1370); 
               if (imgSizeKB <= 105)
            	{
	               targetImg.src = imgBase64String;
	               targetImgStringInput.value = targetImg.src;
            	}
               else
            	{
            	   window.alert("The selected image is too large: " + imgSizeKB + "KB.\nMaximum allowed size: 100KB.");
            	}
            }
            fileReader.readAsDataURL(fileToLoad);
         }
      }

      function deleteSite()
      {
         if (getConfirmation("You are about to delete site '<%=(site != null) ? (site.getName(owner)) : ("Error")%>'. Please confirm if you want to continue."))
         {
            submitForm('deleteSiteForm');
         }
      }

      function getImageData(targetField, targetImage)
      {
         var xmlhttp = new XMLHttpRequest();
         xmlhttp.onreadystatechange =
            function()
            {
               if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
               {
                  document.getElementById(targetField).value = xmlhttp.responseText;
                  document.getElementById("get" + targetField).src = "/img/action/refresh_12.png";
                  document.getElementById(targetImage).src = document.getElementById(targetField).value;
                  document.getElementById(targetImage).style.visibility = 'visible';
                  document.getElementById(targetImage + "Wrapper").style.visibility = 'visible';
               }
            };
         var params = "<%=SitesPages.INPUT_SITE_ID%>=<%=site.getSiteId()%>&<%=SitesPages.INPUT_PROPERTY%>=" + targetField;
         xmlhttp.open("POST", "/onepass/sites/get", true);
         xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
         xmlhttp.send(params);
      }
      
      function getData(targetField)
      {
	      var xmlhttp = new XMLHttpRequest();
	      xmlhttp.onreadystatechange =
	    	   function()
	         {
	    	      if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
	            {
	    	    	   document.getElementById(targetField).style.visibility = 'visible';
	    	    	   document.getElementById(targetField).value = xmlhttp.responseText;
	    	    	   document.getElementById("get" + targetField).src = "/img/action/refresh_12.png";
	            }
	         };
	      var params = "<%=SitesPages.INPUT_SITE_ID%>=<%=site.getSiteId()%>&<%=SitesPages.INPUT_PROPERTY%>=" + targetField;
	      xmlhttp.open("POST", "/onepass/sites/get", true);
	      xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	      xmlhttp.send(params);
      }

      function getAllData()
      {
    	   getData('<%=SitesPages.INPUT_LOGIN%>');
         getData('<%=SitesPages.INPUT_PASSWORD%>');
         getData('<%=SitesPages.INPUT_KEY%>');
         getData('<%=SitesPages.INPUT_COMMENT%>');
         getImageData('<%=SitesPages.INPUT_IMAGE_STRING%>', 'siteImage');
         
         document.getElementById("getAll").src = "/img/action/refresh_12.png";
      }
   </script>
</head>
<% if (edit)
{%>
<body onload="getAllData()"><%
}
else
{%>
<body><%
}%>
   <div id="page">
      <%@include file="/include/pageheader.jspf"%>
      <div id="content_wrapper">
         <div id="content">
            <div class="box">
               <%@include file="/include/message_handler.jspf"%>
<%
               if (site == null)
               {
%>                <div class="error_message">
                     Site not found.
                  </div>
<%             }
               else
               {
%>                <div class="title">
                     Site
                     <div class="menu">
                        <img src="/img/action/download_12.png" id="getAll"/>&nbsp;
                           <a href="#" onclick="getAllData(); return false;">Refresh all</a>&nbsp;&nbsp;&nbsp;
<%                if (!edit)
                  {
%>                      <img src="/img/action/edit_12.png"/>&nbsp;
                           <a href="<%=SitesPages.editSite(site, true)%>">Edit Site</a>&nbsp;&nbsp;&nbsp;
                        <img src="/img/action/delete_12.png"/>&nbsp;
                           <a href="#" onclick="deleteSite(); return false;">Delete site</a>
                        <form id="deleteSiteForm" action="/onepass/sites/deletesite" method="post">
                           <input name="<%=SitesPages.INPUT_SITE_ID%>" type="hidden" value="<%=site.getSiteId()%>"/>
                        </form>
<%                }
%>                   </div>
                  </div>
                  <div class="content">
                     <form action="/onepass/sites/savesite" method="post" autocomplete="off">
                        <input name="<%=SitesPages.INPUT_SITE_ID%>" type="hidden" value="<%=site.getSiteId()%>"/>
                        <div class="list_table">
                           <div class="row">
                              <div class="cell" style="width: 60px;">Name</div>
                              <div class="cell" style="width: 12px;;"></div>
                              <div class="cell" style="width: 100%;">
                                 <input name="<%=SitesPages.INPUT_SITE_NAME%>" id="<%=SitesPages.INPUT_SITE_NAME%>" value="<%=site.getName(owner)%>" size="122"
                                          <%=(newSite)?("autofocus"):("readonly")%> class="site_property" onclick="selectInputText('<%=SitesPages.INPUT_SITE_NAME%>'); return false;"/>
                              </div>
                              <div class="cell" style="width: 12px;"></div>
                           </div>
                           <div class="row">
                              <div class="cell">Reference</div>
                              <div class="cell"></div>
                              <div class="cell">
                                 <input name="<%=SitesPages.INPUT_REFERENCE%>" id="<%=SitesPages.INPUT_REFERENCE%>" value="<%=site.getReference(owner)%>" size="122"
                                          <%=(newSite)?(""):("autofocus")%> <%=(edit)?(""):("readonly")%> class="site_property" onclick="selectInputText('<%=SitesPages.INPUT_REFERENCE%>'); return false;"/>
                              </div>
                              <div class="cell">
                                 <a href="<%=site.getReference(owner)%>" target="_blank"><img src="/img/action/go_12.png" id="goToReference"/></a>
                              </div>
                           </div>
                           <div class="row">
                              <div class="cell">Login</div>
                              <div class="cell">
                                 <a href="#" onclick="getData('<%=SitesPages.INPUT_LOGIN%>'); return false;">
                                    <img src="<%=(edit)?("/img/action/refresh_12.png"):("/img/action/download_12.png")%>" id="get<%=SitesPages.INPUT_LOGIN%>"/>
                                 </a>
                              </div>
                              <div class="cell">
                                 <input name="<%=SitesPages.INPUT_LOGIN%>" id="<%=SitesPages.INPUT_LOGIN%>" value="" size="122" style="visibility: hidden"
                                          <%=(edit)?(""):("readonly")%> class="<%=(edit)?("site_property"):("masked_site_property")%>" onclick="selectInputText('<%=SitesPages.INPUT_LOGIN%>'); return false;"/>
                              </div>
                              <div class="cell"></div>
                           </div>
                           <div class="row">
                              <div class="cell">Password</div>
                              <div class="cell">
                                 <a href="#" onclick="getData('<%=SitesPages.INPUT_PASSWORD%>'); return false;">
                                    <img src="<%=(edit)?("/img/action/refresh_12.png"):("/img/action/download_12.png")%>" id="get<%=SitesPages.INPUT_PASSWORD%>"/>
                                 </a>
                              </div>
                              <div class="cell">
                                 <input name="<%=SitesPages.INPUT_PASSWORD%>" id="<%=SitesPages.INPUT_PASSWORD%>" value="" size="122" style="visibility: hidden"
                                          <%=(edit)?(""):("readonly")%> class="<%=(edit)?("site_property"):("masked_site_property")%>" onclick="selectInputText('<%=SitesPages.INPUT_PASSWORD%>'); return false;"/>
                              </div>
                              <div class="cell"></div>
                           </div>
                           <div class="row">
                              <div class="cell">Key</div>
                              <div class="cell">
                                 <a href="#" onclick="getData('<%=SitesPages.INPUT_KEY%>'); return false;">
                                    <img src="<%=(edit)?("/img/action/refresh_12.png"):("/img/action/download_12.png")%>" id="get<%=SitesPages.INPUT_KEY%>"/>
                                 </a>
                              </div>
                              <div class="cell">
                                 <input name="<%=SitesPages.INPUT_KEY%>" id="<%=SitesPages.INPUT_KEY%>" value="" size="122" style="visibility: hidden"
                                          <%=(edit)?(""):("readonly")%> class="<%=(edit)?("site_property"):("masked_site_property")%>" onclick="selectInputText('<%=SitesPages.INPUT_KEY%>'); return false;"/>
                              </div>
                              <div class="cell"></div>
                           </div>
                           <div class="row">
                              <div class="cell">Comment</div>
                              <div class="cell">
                                 <a href="#" onclick="getData('<%=SitesPages.INPUT_COMMENT%>'); return false;">
                                    <img src="<%=(edit)?("/img/action/refresh_12.png"):("/img/action/download_12.png")%>" id="get<%=SitesPages.INPUT_COMMENT%>"/>
                                 </a>
                              </div>
                              <div class="cell">
                                 <input name="<%=SitesPages.INPUT_COMMENT%>" id="<%=SitesPages.INPUT_COMMENT%>" value="" style="visibility: hidden"
                                          <%=(edit)?(""):("readonly")%> size="122" class="site_property" onclick="selectInputText('<%=SitesPages.INPUT_COMMENT%>'); return false;"/>
                              </div>
                              <div class="cell"></div>
                           </div>
                           <div class="row">
                              <div class="cell">Image</div>
                              <div class="cell">
                                 <a href="#" onclick="getImageData('<%=SitesPages.INPUT_IMAGE_STRING%>', 'siteImage'); return false;">
                                    <img src="<%=(edit)?("/img/action/refresh_12.png"):("/img/action/download_12.png")%>" id="get<%=SitesPages.INPUT_IMAGE_STRING%>"/>
                                 </a>
                              </div>
                              <div class="cell" align="center">
                                 <input id="imageFileBrowser" type="file" accept="image/*" onchange="updateImageFromFile(); return false;" style="display: none"/>
                                 <input name="<%=SitesPages.INPUT_IMAGE_STRING%>" id="<%=SitesPages.INPUT_IMAGE_STRING%>" type="hidden" value=""/>
                                 <div id="siteImageWrapper" class="site_image_wrapper" style="visibility: hidden">
                                    <a href="#" onclick="getImageData('<%=SitesPages.INPUT_IMAGE_STRING%>', 'siteImage'); return false;">
                                       <img id="siteImage" class="site_image" style="visibility: hidden" src="" />
                                    </a>
                                 </div>
<%                            if (edit)
                              {
%>                               <input type="button" value="Browse" onclick="browseImage(); return false;" style="width: 80px"/>&nbsp;&nbsp;
                                 <img src="/img/action/delete_12.png"/>&nbsp;<a href="#" onclick="deleteImage(); return false;">Delete image</a>
<%                            }
%>                            </div>
                              <div class="cell"></div>
                           </div>
                           <div class="total_row">
                              <div class="cell"></div>
                              <div class="cell"></div>
                              <div class="cell" align="center">
                                 <a href="<%=(edit && !newSite)?(SitesPages.editSite(site)):(SitesPages.listSites())%>">Back</a>&nbsp;&nbsp;
<%                            if (edit)
                              {
%>                               <input type="submit" value="Save" style="width: 60px; margin-bottom: 0px"/>
<%                            }
%>                            </div>
                              <div class="cell"></div>
                           </div>
                        </div>
                     </form>
                  </div>
<%                }
%>          </div>
         </div>
      </div>
      <%@include file="/include/pagefooter.jspf"%>
   </div>
</body>
</html>