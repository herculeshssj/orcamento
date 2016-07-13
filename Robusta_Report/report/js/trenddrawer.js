var flotChartObj = 0;
function TrendDrawer(placeholder, legend_placeholder, trenddata) {
	this.placeholder = placeholder;
	this.trenddata = trenddata;
	this.legend_placeholder = legend_placeholder;
	var self = this;
	
	var __construct = function() {
		self.dataSeries = [];

		$.each(self.trenddata.badSmellType, function(index, value) {
			self.dataSeries.push({"label": value, "data": [], "index": index});
		});

		//Push data into each serie. i.e. smell type
		$.each(self.dataSeries, function(serieIndex, serie) {
			// For each serie (smell type), loop through all report and get the count according to that smell type
			$.each(self.trenddata.reports, function(reportIndex, report){
				// reportIndex == report.id (i.e. x value), y value is badSmellCount of that serieIndex
				serie.data.push([reportIndex, report.badSmellCount[serieIndex]]);
			});
		});

		self.dataSeries.push({"label": "Total Bad Smell", "data": [], "index": self.dataSeries.length});
		$.each(self.trenddata.reports, function(reportIndex, report){
			var total = 0;
			total = eval( report.badSmellCount.join('+') );
			self.dataSeries[self.dataSeries.length -1].data.push([reportIndex, total]);
		});
	}();
	
	if (!String.format) {
	  String.format = function(format) {
		var args = Array.prototype.slice.call(arguments, 1);
		return format.replace(/{(\d+)}/g, function(match, number) { 
		  return typeof args[number] != 'undefined' ? args[number] : match;
		});
	  };
	}

	this.options = {
		series: {
			lines: { show: true },
			points: { show: true }
		},
		grid: {
			hoverable: true
		},
		xaxis: {
			tickSize: 1,
			tickFormatter: function (val, axis) {
				return "#" + (val + 1);
			}
		},
		yaxis: {
			tickDecimals: 0
		},
		legend: {
			show: true,
			labelFormatter: function(label, series) {
				var displayLabel = '<label><input type="checkbox" name="badsmelltype" value="{0}" {1} onclick="togglePlot({0});">{2}</label>';
				displayLabel = String.format(displayLabel, series.index, series.lines.show ? 'checked="checked"' : "", label);
				return displayLabel;
			},
			container: self.legend_placeholder
		},
		tooltip: true,
		tooltipOpts: {
			content: "",
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false,
			onHover: function(item, tooltipEl) { 
				var tooltipCotent = '<span id="ttip"><span id="ttLable">{0}</span><span id="ttDate">Date: {1}</span><span id="ttValue">Value: {2}</span></span>';
				var itemPoint = self.trenddata.reports[item.dataIndex];
				tooltipCotent = String.format(tooltipCotent, item.series.label, itemPoint.date, item.datapoint[1]);
				tooltipEl.html(tooltipCotent);
				$("#flotTip").css("border", "4px solid " + item.series.color);
			}
		}
	};
	
	this.drawTrendReport = function() {
		flotChartObj = $.plot(self.placeholder, self.dataSeries, self.options);
		for(var i=0; i<7; i++)
			togglePlot(i);
	}
}

var togglePlot = function(seriesIdx) {
  var flotData = flotChartObj.getData();
  flotData[seriesIdx].lines.show = !flotData[seriesIdx].lines.show;
  flotData[seriesIdx].points.show = !flotData[seriesIdx].points.show;
  if (!flotData[seriesIdx].lines.show){
	  flotData[seriesIdx].tempData = flotData[seriesIdx].data;
	  flotData[seriesIdx].data = [];  // store old data and blank out real data
  } else {
	  flotData[seriesIdx].data = flotData[seriesIdx].tempData; // restore real data
  }
  flotChartObj.setData(flotData);
  flotChartObj.setupGrid();
  flotChartObj.draw();
}

var allPastReport = [];
$.each(trendReportData.reports, function(reportIndex, report){
	allPastReport.push([reportIndex + 1, report.date]);
	for(var i=0; i <report.badSmellCount.length; i++)
		allPastReport[reportIndex].push(report.badSmellCount[i]);
	var total = eval( report.badSmellCount.join('+') );
	allPastReport[reportIndex].push(total);
});