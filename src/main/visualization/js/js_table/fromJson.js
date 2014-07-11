console.log("Start!");

/*$(document).ready(function () {
	$.getJSON("json/json.json",function(data){
		console.log(data.values.length)
		console.log(data.values[0].NumberTweetsInterval)
	});
});*/

$(document).ready(function () {
	$.getJSON("assets/json/json.json",
		function (data) {
			var tr;
			// No calen les capçaleres
			/*tr = $('<tr/>');
			tr.append("<td>" + data.traits[0] + "</td>");
			tr.append("<td>" + data.traits[1] + "</td>");
			tr.append("<td>" + data.traits[2] + "</td>");
			tr.append("<td>" + data.traits[3] + "</td>");
			$('table').append(tr);*/
			for (var i = 0; i < data.values.length; i++) {  
				
				tr = $('<tr/>');
				//<html:link action="/path/to/action?param1=2&param2=${param2Value}">Some text</html:link> 
				tr.append("<td style='font-size:16px'><a href='demotable.html?interval="+i+"'>" + data.values[i].intervals + "</a></td>");
				tr.append("<td style='font-size:16px'>" + data.values[i].totals + "</td>");
				tr.append("<td style='background-color:"+ data.values[i].Tweet_Minut_Colour +";'>"+ "<b>" + data.values[i].tweets_min +"</b>"+ "</td>");
				//tr.append("<td style='font-size:12px'>" + data.values[i].entradeta + "</td>");
				tr.append("<td style='background-color:"+ data.values[i].Colour_Sentiment +";'>" + "<b>" + data.values[i].pos + " - " + data.values[i].neg + " - " + data.values[i].neu +"</b>"+  "</td>");
				tr.append("<td style='font-size:16px'>" + data.values[i].topic_maj + "</td>");
				
				$('table').append(tr);  			
			}
			//TODO Importar JQuerry plugin del tablecloth 
			//$('table').tablecloth();
	});
});