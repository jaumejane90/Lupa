$(document).ready(function () {
	var id = window.location.search.substring(1).split('=')[1];

	$.getJSON("json/data_toVisualize/data_recommendation/recommendation_"+ id +".json",
		function (data) {			
			var tr;			
			for (var i = 0; i < data.length; i++) {  
			
				tr = $('<tr/>');
						
				tr.append("<td>" + data[i].id + "</td>");
				tr.append("<td>" + data[i].distance + "</td>");			
				$('table').append(tr);  			
			}
			
	});
});