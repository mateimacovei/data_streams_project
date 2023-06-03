function getDataCenters(callback) {
    $.getJSON("http://localhost:8080/api/datacenters", callback)
}

function getRacks(datacenter_id, callback) {
    $.getJSON("http://localhost:8080/api/racks?datacenterId=" + datacenter_id, callback)
}

function getFormattedDate(date_string) {
    return date_string.slice(6, 10) + '-' + date_string[0] + date_string[1] + '-' + date_string[3] + date_string[4]
}

$(function () {

    var dateFormat = "mm/dd/yy",
        from = $("#from")
            .datepicker({
                defaultDate: "+1w",
                changeMonth: true,
                numberOfMonths: 3
            })
            .on("change", function () {
                to.datepicker("option", "minDate", getDate(this));
            }),
        to = $("#to").datepicker({
            defaultDate: "+1w",
            changeMonth: true,
            numberOfMonths: 3
        })
            .on("change", function () {
                from.datepicker("option", "maxDate", getDate(this));
            });

    function getDate(element) {
        var date;
        try {
            date = $.datepicker.parseDate(dateFormat, element.value);
        } catch (error) {
            date = null;
        }

        return date;
    }

    var valuesType = "AVERAGE"

    function create_line_graph(graph_parent_div, url, rack_id) {
        graph_parent_div.empty()
        var canvasHtml = $("<canvas>").attr("id", "myChartCanvas").appendTo(graph_parent_div)
        $.ajax({
            type: "POST",
            url: url,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                valuesType: valuesType,
                identifierId: rack_id,
                from: getFormattedDate(document.getElementById("from").value) + "t00:00",
                to: getFormattedDate(document.getElementById("to").value) + "t23:59"
            }),
            success: function (data) {
                var line_chart = new Chart(canvasHtml, {
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
                })
            }
        })
    }

    function create_values_type_selector() {
        let selectors = $("<div>").attr("class", "row align-items-center").appendTo(main_container);
        let value_type_selector = $("<div>").attr("class", "col").appendTo(selectors);

        let op1 = $("<div>").addClass("form-check").appendTo(value_type_selector)
        let r1 = $("<input>").addClass("form-check-input").attr("type", "radio").attr("name", "flexRadioDefault").attr("id", "flexRadioDefault1").appendTo(op1)
        $("<label>").addClass("form-check-label").attr("for", "flexRadioDefault1").html("See values").appendTo(op1)
        r1.change(function () {
            valuesType = "VALUE"
        });

        let op2 = $("<div>").addClass("form-check").appendTo(value_type_selector)
        let r2 = $("<input>").addClass("form-check-input").attr("type", "radio").attr("name", "flexRadioDefault").attr("id", "flexRadioDefault2").appendTo(op2)
        $("<label>").addClass("form-check-label").attr("for", "flexRadioDefault2").html("See average values").appendTo(op2)
        r2.change(function () {
            valuesType = "AVERAGE"
        });
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
        main_container.empty()
    })


});