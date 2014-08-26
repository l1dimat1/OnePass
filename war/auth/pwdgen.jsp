
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-tdansitional.dtr">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <%@include file="/include/htmlheader.jspf"%>
   <script language="javascript" type="text/javascript">
      function init()
      {
    	  generatePassword();
      }
   
      function generatePassword()
      {
    	   var pwdLength = document.getElementById('nb_chars').value;
    	  
    	   var keylist = "";
    	   if (document.getElementById('upper_case').checked) keylist = keylist + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
         if (document.getElementById('lower_case').checked) keylist = keylist + "abcdefghijklmnopqrstuvwxyz";
         if (document.getElementById('digits'    ).checked) keylist = keylist + "0123456789";
         if (document.getElementById('spec_char' ).checked) keylist = keylist + document.getElementById('spec_chars').value;

         var pwd = '';
         for (i = 0; i < pwdLength; i++)
        	 pwd += keylist.charAt(Math.floor(Math.random() * keylist.length));
        	
         document.getElementById('password').value = pwd;
      }
      
      function specCharsChanged()
      {
    	   if (document.getElementById('spec_char' ).checked)
    		   document.getElementById('spec_chars').style.visibility = 'visible';
    	   else
    		   document.getElementById('spec_chars').style.visibility = 'hidden';
      }
   </script>
</head>
<body onload="init()">
   <div id="content">
	   <div class="box">
	      <div class="content">
	         Size:&nbsp;<input type="text" id="nb_chars" size="2" value="24" onclick="selectInputText('nb_chars'); return false;"/>
            <div style="float: right">
               <input type="checkbox" id="upper_case" checked>A-Z</input>&nbsp;&nbsp;&nbsp;&nbsp;
               <input type="checkbox" id="lower_case" checked>a-z</input>&nbsp;&nbsp;&nbsp;&nbsp;
               <input type="checkbox" id="digits"     checked>0-9</input>&nbsp;&nbsp;&nbsp;&nbsp;
               <input type="checkbox" id="spec_char"  onchange="specCharsChanged(); return false;">Special characters:&nbsp;</input>
               <input type="text"     id="spec_chars" size="30" value="!&quot;#$%&'()=-~^|\_/?.>,<;+:*]}[{@`]" onclick="selectInputText('spec_chars'); return false;" style="visibility:hidden"/>
            </div>
            <br/>
            <div style="height:5px"></div>
            <div class="separator"></div>
            <div style="height:5px"></div>
            <input type="submit" value="Generate" onclick="generatePassword()" style="width: 100px"/>
            <div style="float: right">Password:&nbsp;<input type="text" id="password" size=64 value="" onclick="selectInputText('password'); return false;"/></div>
         </div>
         <div class="inner_box" style="text-align: center"><a href="#" onclick="window.close(); return false;">Close</a></div>
		</div>
   </div>
</body>
</html>