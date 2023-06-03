function getDataCenters(callback) {
    $.getJSON("http://localhost:8080/api/datacenters", callback)
}

function getRacks(datacenter_id, callback) {
    $.getJSON("http://localhost:8080/api/racks?datacenterId=" + datacenter_id, callback)
}

function getFormattedDate(date_string) {
    return date_string.slice(6, 10) + '-' + date_string[0] + date_string[1] + '-' + date_string[3] + date_string[4]
}

function getLabel(data, datasetIndex, dataIndex) {
    let labelIndex = 0;
    for (let i = 0; i < datasetIndex; i++) {
        labelIndex += data.datasets[i].data.length;
    }
    return data.labels[labelIndex + dataIndex];
}

function getDatasetIndex(datasets, labelIndex) {
    let copy = labelIndex;
    let datasetIndex = 0;
    while (copy > 0) {
        copy = copy - datasets[datasetIndex].data.length;
        if(copy >= 0) {
            datasetIndex++;
        }
    }
    return datasetIndex;
}

$(function () {

    const to = $("#to").datepicker({
        defaultDate: "+1w",
        changeMonth: true,
        numberOfMonths: 3
    })
        .on("change", function () {
            from.datepicker("option", "maxDate", getDate(this));
        });
    const dateFormat = "mm/dd/yy",
        from = $("#from")
            .datepicker({
                defaultDate: "+1w",
                changeMonth: true,
                numberOfMonths: 3
            })
            .on("change", function () {
                to.datepicker("option", "minDate", getDate(this));
            });

    function getDate(element) {
        let date;
        try {
            date = $.datepicker.parseDate(dateFormat, element.value);
        } catch (error) {
            date = null;
        }

        return date;
    }

    let valuesType = "AVERAGE";
    let interpolation_points_nr = 3;

    function create_line_graph(graph_parent_div, url, identifierId) {
        graph_parent_div.empty()
        const canvasHtml = $("<canvas>").attr("id", "myChartCanvas").appendTo(graph_parent_div);
        $.ajax({
            type: "POST",
            url: url,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                valuesType: valuesType,
                identifierId: identifierId,
                interpolationPoints: interpolation_points_nr,
                from: getFormattedDate(document.getElementById("from").value) + "t00:00",
                to: getFormattedDate(document.getElementById("to").value) + "t23:59"
            }),
            success: function (data) {
                new Chart(canvasHtml, {
                    type: 'line',
                    data: data,
                    options: {
                        scales: {
                            x: {
                                ticks: {
                                    callback: function (value, index, ticks) {
                                        return null;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    function create_values_type_selector() {
        let selectors = $("<div>").attr("class", "row align-items-center").appendTo(main_container);
        let value_type_selector = $("<div>").attr("class", "col").appendTo(selectors);
        let interpolation_nr_selector = $("<div>").attr("class", "col").appendTo(selectors);

        let op1 = $("<div>").addClass("form-check").appendTo(value_type_selector)
        let r1 = $("<input>").addClass("form-check-input").attr("type", "radio").attr("name", "flexRadioDefault").attr("id", "flexRadioDefault1").appendTo(op1)
        $("<label>").addClass("form-check-label").attr("for", "flexRadioDefault1").html("See values").appendTo(op1)
        r1.change(function () {
            valuesType = "VALUE"
            interpolation_nr_selector.hide();
        });

        let op2 = $("<div>").addClass("form-check").appendTo(value_type_selector)
        let r2 = $("<input>").addClass("form-check-input").attr("type", "radio").attr("name", "flexRadioDefault").attr("id", "flexRadioDefault2").attr("checked", '').appendTo(op2)
        $("<label>").addClass("form-check-label").attr("for", "flexRadioDefault2").html("See average values").appendTo(op2)
        r2.change(function () {
            valuesType = "AVERAGE"
            interpolation_nr_selector.hide();
        });
        // default for active element
        interpolation_nr_selector.hide();

        let op3 = $("<div>").addClass("form-check").appendTo(value_type_selector)
        let r3 = $("<input>").addClass("form-check-input").attr("type", "radio").attr("name", "flexRadioDefault").attr("id", "flexRadioDefault3").appendTo(op3)
        $("<label>").addClass("form-check-label").attr("for", "flexRadioDefault3").html("See interpolated values").appendTo(op3)
        r3.change(function () {
            valuesType = "VALUE_INTERPOLATED"
            interpolation_nr_selector.show();
        });

        let op4 = $("<div>").addClass("form-check").appendTo(value_type_selector)
        let r4 = $("<input>").addClass("form-check-input").attr("type", "radio").attr("name", "flexRadioDefault").attr("id", "flexRadioDefault4").appendTo(op4)
        $("<label>").addClass("form-check-label").attr("for", "flexRadioDefault4").html("See interpolated average values").appendTo(op4)
        r4.change(function () {
            valuesType = "AVERAGE_INTERPOLATED"
            interpolation_nr_selector.show();
        });

        let row = $("<div>").attr("class", "row").appendTo(interpolation_nr_selector);
        let section = $("<section>").attr("class", "w-100 p-4 d-flex text-center").appendTo(row);
        let form_outline = $("<div>").attr("class", "form-outline").attr("style", "width: 22rem;").appendTo(section);
        $("<input>").attr("type", "number").attr("id", "typeNumber").attr("class", "form-control").attr("min", "3").attr("value", "10").appendTo(form_outline)
            .on("change", (changeEvent) => {
                interpolation_points_nr = changeEvent.target.value
                console.log(interpolation_points_nr);
            });
        $("<label>").attr("class", "form-label").attr("htmlFor", "typeNumber").attr("style", "margin-left: 0px;").html("Interpolation points (min 3)").appendTo(form_outline)
        let form_notch = $("<div>").attr("class", "form-notch").appendTo(form_outline);
        $("<div>").attr("class", "form-notch-leading").appendTo(form_notch);
        $("<div>").attr("class", "form-notch-middle").appendTo(form_notch);
        $("<div>").attr("class", "form-notch-trailing").appendTo(form_notch);
    }


    let main_container = $('#main_container')

    $("#btnradio1").on('click', () => {
        // processor temperature
        main_container.empty();
        create_values_type_selector();

        let selection_and_data = $("<div>").attr("class", "row").appendTo(main_container);

        let datacenter_list = $("<div>").attr("class", "col-3").appendTo(selection_and_data);
        let rack_list = $("<div>").attr("class", "col-3").appendTo(selection_and_data);
        let graph = $("<div>").attr("class", "col-6").appendTo(selection_and_data);

        getDataCenters(function (data) {
            for (let i = 0; i < data.length; i++) {
                $("<button>").addClass("list-group-item list-group-item-action").attr("type", "button").html(data[i].name)
                    .appendTo(datacenter_list).on('click', () => {

                    rack_list.empty();
                    getRacks(data[i].id, (racks_data) => {
                        for (let j = 0; j < racks_data.length; j++) {
                            $("<button>").addClass("list-group-item list-group-item-action").attr("type", "button").html(racks_data[j].name)
                                .appendTo(rack_list).on('click', () => {
                                create_line_graph(graph, "http://localhost:8080/api/stream/reportProcessorTemp", racks_data[j].id);
                            });
                        }
                    })
                });
            }
        });
    })

    $("#btnradio2").on('click', () => {
        // water temperature
        main_container.empty();
        create_values_type_selector();

        let selection_and_data = $("<div>").attr("class", "row").appendTo(main_container);

        let datacenter_list = $("<div>").attr("class", "col-4").appendTo(selection_and_data);
        let graph = $("<div>").attr("class", "col-8").appendTo(selection_and_data);

        getDataCenters(function (data) {
            for (let i = 0; i < data.length; i++) {
                $("<button>").addClass("list-group-item list-group-item-action").attr("type", "button").html(data[i].name)
                    .appendTo(datacenter_list).on('click', () => {
                    create_line_graph(graph, "http://localhost:8080/api/stream/reportWaterTemp", data[i].id);
                });
            }
        });
    })

    $("#btnradio3").on('click', () => {
        // water flow
        main_container.empty();
        create_values_type_selector();

        let selection_and_data = $("<div>").attr("class", "row").appendTo(main_container);

        let datacenter_list = $("<div>").attr("class", "col-4").appendTo(selection_and_data);
        let graph = $("<div>").attr("class", "col-8").appendTo(selection_and_data);

        getDataCenters(function (data) {
            for (let i = 0; i < data.length; i++) {
                $("<button>").addClass("list-group-item list-group-item-action").attr("type", "button").html(data[i].name)
                    .appendTo(datacenter_list).on('click', () => {
                    create_line_graph(graph, "http://localhost:8080/api/stream/reportWaterFlow", data[i].id);
                });
            }
        });
    })

    $("#btnradio4").on('click', () => {
        // incidents
        main_container.empty();

        const sub_main_container = $("<div>").attr("class", "row").appendTo(main_container);
        const incident_list = $("<div>").attr("class", "col-6").appendTo(sub_main_container);
        const graph_holder = $("<div>").attr("class", "col-6").appendTo(sub_main_container);

        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/stream/incidentsReport",
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                from: getFormattedDate(document.getElementById("from").value) + "t00:00",
                to: getFormattedDate(document.getElementById("to").value) + "t23:59"
            }),
            success: function (data) {
                let incident_strings_list = data.incidents;
                for (let i = 0; i < incident_strings_list.length; i++) {
                    $("<button>").addClass("list-group-item list-group-item-action").attr("type", "button")
                        .attr("data-toggle","tooltip").attr("data-placement","right").attr("title",incident_strings_list[i].date).html(incident_strings_list[i].description).appendTo(incident_list)
                }
                $('[data-toggle="tooltip"]').tooltip() // required to display the dates

                console.log(data.pieChartReport)

                const canvasHtmlPieChart = $("<canvas>").attr("id", "myPieChartCanvas").appendTo(graph_holder);
                new Chart(canvasHtmlPieChart, {
                    type: 'pie',
                    data: data.pieChartReport,
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                labels: {
                                    generateLabels: function (chart) {
                                        // Get the default label list
                                        const original = Chart.overrides.pie.plugins.legend.labels.generateLabels;
                                        const labelsOriginal = original.call(this, chart);

                                        // Build an array of colors used in the datasets of the chart
                                        let datasetColors = chart.data.datasets.map(function (e) {
                                            return e.backgroundColor;
                                        });
                                        datasetColors = datasetColors.flat();

                                        // Modify the color and hide state of each label
                                        labelsOriginal.forEach(label => {
                                            // // There are twice as many labels as there are datasets. This converts the label index into the corresponding dataset index
                                            // label.datasetIndex = (label.index - label.index % 2) / 2;
                                            label.datasetIndex = getDatasetIndex(chart.data.datasets, label.index);

                                            // The hidden state must match the dataset's hidden state
                                            label.hidden = !chart.isDatasetVisible(label.datasetIndex);

                                            // Change the color to match the dataset
                                            label.fillStyle = datasetColors[label.index];
                                        });

                                        return labelsOriginal;
                                    }
                                },
                                onClick: function (mouseEvent, legendItem, legend) {
                                    // toggle the visibility of the dataset from what it currently is
                                    legend.chart.getDatasetMeta(
                                        legendItem.datasetIndex
                                    ).hidden = legend.chart.isDatasetVisible(legendItem.datasetIndex);
                                    legend.chart.update();
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    title: function (context) {
                                        return getLabel(context[0].chart.data, context[0].datasetIndex, context[0].dataIndex);
                                    },
                                    label: function (context) {
                                        return 'value: ' + context.formattedValue;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    })


});