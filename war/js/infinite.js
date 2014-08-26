/****************************************************************
 * Handle confirmation messages                                 *
 ****************************************************************/
 
 function getConfirmation(message)
 {
   var confirmed = confirm(message);
   return confirmed;
 }
 
 /****************************************************************
  * Shows or hide the component 				                 *
  ****************************************************************/
 
function showHideComponent(component)
{
	if (component.style.visibility == "visible")
		component.style.visibility = 'hidden';
	else
		component.style.visibility = 'visible';
 }

/****************************************************************
 * Submit a form from a hyperlink								*
 ****************************************************************/
function submitForm(formId)
{
	document.forms[formId].submit();
}

/****************************************************************
 * Selects the text in a "text" input							*
 ****************************************************************/
function selectInputText(inputId)
{
	document.getElementById(inputId).select();
}