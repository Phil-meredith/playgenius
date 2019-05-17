window.onload = function() {

 const matchId = document.head.querySelector("[name=matchId][content]").content;

fetch("/totalDistance/"+matchId)
    .then(function(response){
    if (response.status !== 200) {
            console.log('Looks like there was a problem. Status Code: ' +
              response.status);
            return;
          }
     response.json().then(function(data) {
            console.log(data);
            makeChart(Object.keys(data), Object.values(data))

          });
    })
    .catch(function(err) {
        console.log('Fetch Error :-S', err);
      });
};


function makeChart(labelList, dataList){
var ctx = document.getElementById('totalDistance');
var myChart = new Chart(ctx, {
    type: 'horizontalBar',
    data: {
        labels: labelList,
        datasets: [{
            label: 'Total Distance',
            data: dataList,
            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)'
            ],
            borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'
            ],
            borderWidth: 1
        }]
    },
    options: {
        scales: {
            xAxes: [{position: 'top'}],
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        }
    }
});}