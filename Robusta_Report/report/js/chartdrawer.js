function ChartDrawer(placeholder, densitydata) {
	var self = this;
	this.placeholder = placeholder;
	this.densitydata = densitydata;
	this.flotChart = 0;
	this.data = [];
	this.ticks = [];
	
	var __construct = function() {
		$.each(	self.densitydata,
				function(index, badSmellCountObj) {
					self.data.push({data: [[index, badSmellCountObj.count]], label: badSmellCountObj.type});
					self.ticks.push([index, badSmellCountObj.type]);
				}
		);
	}();
	
	this.optionsBar = {
		series: {
			bars: {
				show: true,
				fill: true,
				fillColor: { colors: [ { opacity: 0.75 }, { opacity: 0.75 } ] }
			},
			valueLabels: {
				show: true,
				showAsHtml: true,
				align: "center",
				labelFormatter: function(v) {
					return v;
				}
			}
		},
		bars: {
			align: "center",
			barWidth: 0.5
		}, 
		xaxis: {
			ticks: self.ticks,
			autoscaleMargin: 0.02
		}, 
		yaxis: {
			tickDecimals: 0
		},
		legend: {
			show: false
		},
		grid: {
			hoverable: true
		},
		tooltip: true,
		tooltipOpts: {
			content: "<span id='bscount'>%y</span> %x",
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		}
	};

	this.optionsPie = {
		series: {
			pie: {
				show: true,
				innerRadius: 0.5,
				radius: 1,
				label: {
					show: true,
					radius: 3/4,
					formatter: function(label, series) {
						return '<div id="tooltipPie">'+Math.round(series.percent)+'%</div>';
					},
					background: { 
						opacity: 0.5,
						color: '#000'
					}
				}
			}			
		},
		legend: {
				show: true,
				margin: [50, 0]
			},
		grid: {
			hoverable: true
		},
		tooltip: true,
		tooltipOpts: {
			content: "<span id='bscount'>%s</span>",
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		}
	};
	
	this.drawBar = function() {
		self.flotChart = $.plot(this.placeholder, this.data, this.optionsBar);
	}
	
	this.drawPie = function() {
		self.flotChart = $.plot(this.placeholder, this.data, this.optionsPie);
	}
	
	this.bindToggle = function(toggle, textstate1, textstate2) {
		self.toggle = toggle;
		$(function() {
			// Add click handler to change the text
			$(self.toggle).click(function() {
				$(self.placeholder).unbind();
				if($(this).attr("value")=="bar") {
					self.drawPie();
					$(this).val("pie");
					$(this).html(textstate2);
				} else {
					self.drawBar();
					$(this).val("bar");
					$(this).html(textstate1);
				};
			});
			
			//At the beginning, set the text of the button to state 1
			$(self.toggle).html(textstate1);
		});
	}
}