// const config = {
//     type: 'line',
//     data: data,
//     options: {
//           scales: {
//               x: {
//                   ticks: {
//                       callback: function(value, index, ticks) {
//                           return null;
//                       }
//                   }
//               }
//           }
//       }
//   };


var bar_chart = null;
var pie_chart = null;
function update_bar_chart(data) {
    if (bar_chart == null) {
        bar_chart = new Chart($('#myChartCanvas'), {
            type: 'bar',
            data: data,
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }
    else {
        bar_chart.data = data
        bar_chart.update();
    }
}

function update_pie_chart(data) {
    if (pie_chart == null) {
        pie_chart = new Chart($('#myPieChartCanvas'), {
            type: 'doughnut',
            data: data
        });
    }
    else {
        pie_chart.data = data;
        pie_chart.update();
    }
}

function create_bar_chart_data(emotions) {
    let labels = []
    let values = []
    let backgroundColors = []
    let borderColors = []

    let emotion_sum_dict = new Object();
    for (let i = 0; i < emotions.length; i++) {
        labels.push(emotions[i].label);
        values.push(emotions[i].value);
        backgroundColors.push(color_dict[emotions[i].label].backgroundColor);
        borderColors.push(color_dict[emotions[i].label].borderColor);

        if (emotion_sum_dict[emotions[i].label] == undefined)
            emotion_sum_dict[emotions[i].label] = emotions[i].value
        else
            emotion_sum_dict[emotions[i].label] += emotions[i].value
    }

    return {
        labels: labels,
        datasets: [{
            label: null,
            data: values,
            backgroundColor: backgroundColors,
            borderColor: borderColors,
            borderWidth: 1
        }]
    };
}

function create_pie_chart_data(emotions) {
    let labels = []
    let values = []
    let backgroundColors = []
    let emotion_sum_dict = new Object();
    for (let i = 0; i < emotions.length; i++) {
        labels.push(emotions[i].label);
        values.push(emotions[i].value);
        backgroundColors.push(color_dict[emotions[i].label].backgroundColor);

        if (emotion_sum_dict[emotions[i].label] == undefined)
            emotion_sum_dict[emotions[i].label] = emotions[i].value
        else
            emotion_sum_dict[emotions[i].label] += emotions[i].value
    }


    let labels_res = []
    let values_res = []
    let backgroundColors_res = []
    for (var label_key in emotion_sum_dict) {
        labels_res.push(label_key)
        values_res.push(emotion_sum_dict[label_key])
        backgroundColors_res.push(color_dict[label_key].backgroundColor);
    }

    let res = {
        labels: labels_res,
        datasets: [{
            label: 'Emotions pie chart',
            data: values_res,
            backgroundColor: backgroundColors_res,
            hoverOffset: 4
        }]
    }
    return res
}