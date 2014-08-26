// http://www.asquare.net/javascript/tests/KeyCode.html

var ctrlPressed = false;

document.onkeyup = function(e)
{
   if(e.which == 17)
	   ctrlPressed = false;
}
document.onkeydown = function(e)
{
   if(e.which == 17)
   {
	  ctrlPressed = true;
   }
   else if (ctrlPressed && ((e.which == 70) || (e.which == 102)))
   {
	  document.getElementById('filter_input').focus();
	  document.getElementById('filter_input').select();
	  return false;
   }
   return true;
}