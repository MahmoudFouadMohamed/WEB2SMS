//<![CDATA[
function OldPartitionSize(item) {
	var val = +item.value;

	if (val < 30) {
		item.value = 30;
		alert('You Can not Define a value that is less than 30');
	}

}
function AllowedPrecentage(item) {
	var val = +item.value;
	if (val > 100) {
		item.value = 100;
		alert('You Can not Define a value that is more than 100');
	}
}
function selectSrvc(checkbox, editFlage) {
	var checkCCService = "Please do not forget to assign merchants to the profile users, so they could process transactions from the checked service";
	var uncheckCCService = "Please note that all associations with the unchecked service and the profile users merchants will be deleted. this cannot be rolledback";
	if (editFlage) {
		if (checkbox.value == "25" || checkbox.value == "24") {
			var checked = checkbox.checked;
			if (checked) {
				alert(checkCCService);
			} else {//
				alert(uncheckCCService);
			}

		}
	}

}
function selectAllProjects() {
	var iLen = profileForm.elements.length;
	for ( var i = 0; i < iLen; i++) {
		if (document.userRegisterForm.elements[i].type == "checkbox"
				&& document.userRegisterForm.elements[i].value >= 1) {
			document.userRegisterForm.elements[i].checked = true;
		}
	}
}
// ]]>

$(document)
		.ready(
				function() {

					$("#tabs").tabs();

					$("#fraudTabs").tabs();
					$("#PIHTabs").tabs();
					$("#AIRTabs").tabs();
					$("#CMSTabs").tabs();
					$("#SchedulerTabs").tabs();
					$("#SMSTabs").tabs();
					$("#BINSTabs").tabs();

					$(".cTerminals").click(function() {
						$("#ChnlTerminal").dialog("open");
					});
					$("#ChnlTerminal").dialog({
						autoOpen : false,
						modal : true
					});

					$(".viewU").click(function() {
						$("#userDetails").dialog("open");
					});
					$("#userDetails").dialog({
						autoOpen : false,
						modal : true,
						width : 200

					});

					$(".viewQ").click(function() {
						$("#accDetails").dialog("open");
					});
					$("#accDetails").dialog({
						autoOpen : false,
						modal : true

					});

					$(".viewS").click(function() {
						$("#senderDetails").dialog("open");
					});
					$("#senderDetails").dialog({
						autoOpen : false,
						modal : true
					});

					// DatePickers
					$('.dtPicker').datetimepicker({
						showSecond : true,
						timeFormat : 'HH:mm:ss',
						dateFormat : 'dd-mm-yy'
					});
					$('.dtPickerNHours').datetimepicker({
						showSecond : false,
						showMinute : false,
						showHour : false,
						showTime : false,
						dateFormat : 'dd-mm-yy'
					});
					$('.dtPickerFrom').datetimepicker({
						showSecond : true,
						timeFormat : 'HH:mm:ss',
						dateFormat : 'dd-mm-yy'
					});
					$('.dtPickerTo').datetimepicker({
						showSecond : true,
						timeFormat : 'HH:mm:ss',
						dateFormat : 'dd-mm-yy'
					});
					// $('#billCycleDate').datetimepicker({
					// showSecond : true,
					// timeFormat : 'HH:mm:ss',
					// dateFormat : 'dd-mm-yy'
					// });

					// $("#datepicker").datepicker();

					// Slide Panels
					$(".PanelHdr").click(function() {
						$(this).next(".PanelBody").slideToggle(500);
						return false;
					});

					$(".reversePnlTitl").click(function() {
						$(this).next(".reversePnlBdy").slideToggle(500);
						return false;
					});
					// Default closed
					$(".closedPnl").slideToggle(500);
					// Slide Panels End

					// Tabs
					$($(".toggledTab a").attr('href')).show();

					$('.tabs a').click(function() {
						$('.tabpanels').children().hide();
						$('.tabs a').parent().removeClass("toggledTab");

						// Tabs CSS
						$(this).parent().addClass("toggledTab");
						$($(".toggledTab a").attr('href')).show();

						return false;
					});
					// / Tabs End

					// Nav
					$('.navMain').mouseover(function() {
						$(".subMenu").addClass('hideNav');
						$(this).next(".subMenu").toggleClass('hideNav');
					});
					$(document).mouseup(function(e) {
						$(".subMenu").addClass('hideNav');
					});
					// Nav End

					// Dialoges
					$(".viewTrx").click(function() {
						$("#dialog").dialog("open");
					});

					$("#dialog").dialog({
						autoOpen : false,
						show : {
							effect : "blind",
							duration : 1000
						},
						hide : {
							effect : "explode",
							duration : 1000
						}
					});
					$("#opener").click(function() {
						$("#dialog").dialog("open");
					});
					// //////////////////////////
					// Datatable Chk
					$(".chkCrdGrp").hide();
					$('.chkCrdGrp').click(function() {
						alert('here');
						this.next('.clkBtn').click();
					});
					$(".editLink").click(function() {

						$(".editAddDiv").toggleClass("editDiv");
						return false;
					});
					// ////////////////////////////////////// Sliders
					var sliderValue = $(".amount").value;
					$(".slider-range-min").slider({
						range : "min",
						min : 1,
						max : 30,
						value : sliderValue,
						slide : function(event, ui) {
							$(".amount").val(ui.value);
						}
					});
					// /////////////////////////////
					$(".reversePnlTitl").corner();
					$(".succeededMessage").corner();
					$(".failedMessage").corner();
					$(".errormessages").corner();
					$("#loginForm").corner();
					$(".cornerDiv").corner();
					$("#addTerminal").corner();
					$(".divSearch").corner();
					$("#messag").corner();
					// /////////////////////////
					$(".invaildIntegrity").parent().parent().addClass(
							"integrityInvalidTR");
					$(".invaildEncryption").parent().parent().addClass(
							"invaildEncryptionTR");
					$(".AggInvaildIntegrity").parent().parent().addClass(
							"AggIntegrityInvalidTR");

					// tmohamed
					// displaying inProgressDialog with every ajax request
					$('#InProgressDialog')
							.dialog(
									{
										autoOpen : false,
										width : 400,
										modal : true,
										open : function() {
											$(
													'[aria-describedby="InProgressDialog"] .ui-dialog-titlebar')
													.hide();
										}
									});

				});

function displayInProgressDialog(data) {
	if (data.status == "begin") {
		$('#InProgressDialog').dialog('open');
	}// end if

	if (data.status == "success") {
		$('#InProgressDialog').dialog('close');
	}// end if
}// end of function displayProgress

function updateCountdown() {
	var a = smsCharCount(jQuery('.message').val());

	var input = document.getElementById('editAddForm:languageSelected');
	input.value = a[2];
}

var gsm7bitChars = "[]{}@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";

function smsCharCount(message) {
	var engMsg = true;
	var messageLen = message.length;
	var totalSeg = 1;
	var remaining = 160;
	var maxMsgLen = 160;

	for ( var i = 0; i < messageLen; i++) {
		if (engMsg || messageLen == 1) {
			if (gsm7bitChars.indexOf(message.charAt(i)) > -1) {
				engMsg = true;
			} else {
				engMsg = false;
			}
		}
	}

	if (engMsg) {
		if (messageLen > 160) {
			maxMsgLen = 153;
		}

	} else {
		if (messageLen > 70) {
			maxMsgLen = 67;
		} else {
			maxMsgLen = 70;
		}
	}
	if (messageLen > 0) {
		totalSeg = Math.ceil(messageLen / maxMsgLen);
		remaining = maxMsgLen * totalSeg - messageLen;

		// totalSeg = totalSeg > 0 ? totalSeg : 1;
		// remaining = remaining > 0 ? remaining : maxMsgLen;
	}

	return [ remaining, totalSeg, engMsg ];
}
