console.log("Start!");

/*$(document).ready(function () {
	$.getJSON("json/json.json",function(data){
		console.log(data.values.length)
		console.log(data.values[0].NumberTweetsInterval)
	});
});*/

$(document).ready(function () {
	var id = window.location.search.substring(1).split('=')[1];

	$.getJSON("json/data_recommendation/recommendation_"+ id +".json",
		function (data) {
			var tr;			
			for (var i = 0; i < data.length; i++) {  
			
				tr = $('<tr/>');
				/*tr.append("<td style='font-size:16px'>" + data[i].id + "</td>");
				tr.append("<td style='font-size:12px'>" + data[i].distance + "</td>");	*/				
				tr.append("<td>" + data[i].id + "</td>");
				tr.append("<td>" + data[i].distance + "</td>");			
				$('table').append(tr);  			
			}
			
	});
});