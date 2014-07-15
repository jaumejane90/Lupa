function printText(text_id,div_id){
  $(document).ready(function () { 
    $.getJSON("json/data_toVisualize/data_resume/resume_"+ text_id +".json",
      function (data) {
         $(div_id + "  h1").text(" " + data[0].tittle); 
         $(div_id + "  p").text(" " + data[0].text);       
    });
  });

}


function insertText() 
{  
  var new_id = $("#id_text").val();
  var new_text = $("#text_content").val();
  var new_tittle = $("#tittle_content").val();
  /*console.log(new_text);
  alert(new_text);*/ 
  $.ajax({
    type: "POST",
    datatype:"json",
    data: {'id':new_id,'title':new_tittle,'text':new_text},
    url: "scripts/ajaxpost_insertText.py",  
  }).done(function( o ) {
      d3.select("#tree-container" + " > svg ").remove();
      drawCluster();
     // do something
  });
  


}


function loadWordFreq(text_id,div_id) 
{
	d3.select(div_id + " > svg ").remove();
  //$(div_id + " h1").text("Recommended Selected text " + text_id); 
 //console.log(window.location.search.substring(1).split("=")[1]);
  var margin = {top: 40, right: 20, bottom: 30, left: 40},
      width = 960 - margin.left - margin.right,
      height = 500/2 - margin.top - margin.bottom;

  var formatPercent = d3.format(".0%");

  var x = d3.scale.ordinal()
      .rangeRoundBands([0, width], .1);

  var y = d3.scale.linear()
      .range([height, 0]);

  var xAxis = d3.svg.axis()
      .scale(x)
      .orient("bottom");

  var yAxis = d3.svg.axis()
      .scale(y)
      .orient("left")
      .tickFormat(formatPercent);

  var tip = d3.tip()
    .attr('class', 'd3-tip')
    .offset([-10, 0])
    .html(function(d) {
      return "<strong>Frequency:</strong> <span style='color:red'>" + d.frequency + "</span>";
    })

  var svg = d3.select(div_id).append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
    .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  svg.call(tip);

  d3.tsv("json/data_toVisualize/data_tsv/data_"+text_id+".tsv", type, function(error, data) {
    x.domain(data.map(function(d) { return d.word; }));
    y.domain([0, d3.max(data, function(d) { return d.frequency; })]);

    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis)
      .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("Frequency");

    svg.selectAll(".bar")
        .data(data)
      .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function(d) { return x(d.word); })
        .attr("width", x.rangeBand())
        .attr("y", function(d) { return y(d.frequency); })
        .attr("height", function(d) { return height - y(d.frequency); })
        .on('mouseover', tip.show)
        .on('mouseout', tip.hide)

  });

  function type(d) {
    d.frequency = +d.frequency;
    return d;
  }

}

